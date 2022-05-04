/*
 * Copyright 2020-2022 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package uk.ac.ox.softeng.maurodatamapper.dita.generation

import uk.ac.ox.softeng.ebnf.parser.EbnfLexer
import uk.ac.ox.softeng.ebnf.parser.EbnfParser

import groovy.util.logging.Slf4j
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.ccil.cowan.tagsoup.Parser

import java.time.LocalDate

@Slf4j
class DocumentationParser {

    public static final String MDM_CORE_REPO = 'https://raw.githubusercontent.com/MauroDataMapper/mdm-core/main/'
    static String BASE_PACKAGE_DIR = "uk/ac/ox/softeng/maurodatamapper/dita"
    static String baseUrl = "https://docs.oasis-open.org/dita/dita/v1.3/errata02/os/complete/part3-all-inclusive/contentmodels/"

    def tagsoupParser = new Parser()
    XmlSlurper slurper = new XmlSlurper(tagsoupParser)
    String licenseHeader

    DocumentationParser() {
        licenseHeader = loadLicenseHeaderText()
    }

    String loadLicenseHeaderText() {
        String MDM_CORE_REPO = 'https://raw.githubusercontent.com/MauroDataMapper/mdm-core/main/'
        List<String> lines = "$MDM_CORE_REPO/gradle/NOTICE.tmpl".toURL().readLines()
        StringBuilder sb = new StringBuilder('/*\n')
        lines.each {line -> sb.append(' * ').append(line).append('\n')}
        sb.append(' */')
        sb.toString().replaceFirst(/\$\{year}/, LocalDate.now().year.toString())
    }

    Map<String, DitaElementSpecification> buildMapFromDocumentation() {

        Map<String, DitaElementSpecification> elementMap = [:]
        //def chars = 'a'..'z'
        def chars = 'a'..'z'
        chars.toList().each {letter ->
            GPathResult doc
            try {
                String fileUrl = baseUrl + "cmlt${letter}.html"
                doc = slurper.parse(fileUrl)
                elementMap.putAll(getSpecificationsFromWebPage(doc))
            } catch (Exception e) { // Assume it's either j or z which have no pages
                //e.printStackTrace()
                return
            }
        }
        return elementMap
    }

    Map<String, DitaElementSpecification> getSpecificationsFromWebPage(GPathResult doc) {

        Map<String, DitaElementSpecification> elementMap = [:]
        doc.'**'.findAll {
            it.@class == 'section'
        }.each { section ->
            String href = section.h2.span.a.@href.text()
            String name = section.h2.span.a.code.text().replaceAll("[<|>]", "")
            GPathResult elementDescriptionDoc = slurper.parse(baseUrl + href)
            String elementShortDescription = elementDescriptionDoc.'**'.find {
                it.@class == 'shortdesc'
            }.text()
            String attributesText = elementDescriptionDoc.'**'.find {
                it.@id.text().contains("__attributes")
            }.text()
            List<String> attributeGroupNames = []
            attributeGroupMap.each { key, value ->
                if (attributesText.contains(key)) {
                    attributeGroupNames.add(value)
                }
            }

            List<String> originalAttributes = []
            attributeGroupNames.each {
                originalAttributes.addAll(attributeGroupItems[it])
            }

            List<DitaAttributeSpecification> foundExtraAttributes = []

            def attributesSection = elementDescriptionDoc.'**'.find {
                it.name() == "section" && it.@id.text().contains("__attributes")
            }
            if(attributesSection ) {
                def attributesDl = attributesSection.dl
                if(attributesDl.size() == 0) {
                    attributesDl = attributesSection.div.dl
                }
                if(attributesDl) {
                    attributesDl.dt.findAll {
                        it.@class.text().contains("dlterm")
                    }.each { dt ->
                        String extraAttName = dt.text().toString().replace('@', '')
                        boolean isRequired = extraAttName.contains("(REQUIRED)")
                        extraAttName = extraAttName.replace("(REQUIRED)", "")
                        boolean isDeprecated = extraAttName.contains("(DEPRECATED)")
                        extraAttName = extraAttName.replace("(DEPRECATED)", "")
                        extraAttName = extraAttName.replaceAll("[►◄ \t\n,]", "").trim()

                        if(!originalAttributes.contains(getAttributeName(extraAttName))) {
                            foundExtraAttributes.add(new DitaAttributeSpecification(
                                ditaName: extraAttName,
                                required: isRequired,
                                deprecated: isDeprecated,
                                attributeName: getAttributeName(extraAttName)
                            ))
                        }
                    }
                }
            }

            EbnfParser.ExpressionContext expressionContext = calculateContainment(name, section.table[ 0].tbody.tr[ 0])

            SetContainmentEbnfVisitor listContainmentEbnfVisitor = new SetContainmentEbnfVisitor()
            Set<String> containedClasses = listContainmentEbnfVisitor.visit(expressionContext)

            elementMap[name] = new DitaElementSpecification().tap {
                elementName = getClassName(name)
                packagePath = getPackageName(href)
                ditaName = name
                description = elementShortDescription
                attributeGroups = attributeGroupNames
                containedElementNames = containedClasses
                extraAttributes = foundExtraAttributes
                licenseHeaderText = licenseHeader
            }

            //ditaElementSpecification.writeClassFile(BASE_PACKAGE_DIR)
        }
        return elementMap
    }

    static void main(String[] args) {
        if(!args) {
            System.err.println("No arguments provided!")
            System.err.println("Please provide the path of the output directory as the first argument!")
            System.exit(1)
        }
        DocumentationParser documentationParser = new DocumentationParser()
        log.debug("Generating library...")
        Map<String, DitaElementSpecification> elementMap = documentationParser.buildMapFromDocumentation()

        elementMap.each {name, spec ->
            spec.containedElementNames.each {
                if(it == "textdata") {
                    spec.allowsText = true
                } else {
                    String containedElementName = it.replaceAll("[<|>]", "")
                    DitaElementSpecification containedElement = elementMap[containedElementName]
                    if(containedElement) {
                        spec.containedElements.add(containedElement)
                    } else {
                        System.err.println("Cannot find contained element: ${containedElementName} (${it})")
                    }
                }
            }

         }
        elementMap.each {name, spec ->
            spec.writeClassFile(args[0] + "/" + BASE_PACKAGE_DIR)
        }

    }

    static List<String> getPackageName(String href) {
        String folder = href.replace("../", "").replace(".html", "")
        List<String> packageList = folder.split("/").collect {it.toLowerCase()}
        packageList.removeLast()
        return packageList
    }


    static String getAttributeName(String elementName) {

        if(elementName.toLowerCase() == "href") {
            return "href"
        }
        String name = convertToCamelCase(elementName, false)
        replacements.each {replacement ->
            if(name.endsWith(replacement.toLowerCase()) && name != replacement.toLowerCase()) {
                name = name.replace(replacement.toLowerCase(), replacement)
            }
        }
        return name
    }



    static String getClassName(String elementName) {

        String name = convertToCamelCase(elementName,true)
        replacements.each {replacement ->
            if(name.endsWith(replacement.toLowerCase()) && name != replacement.toLowerCase()) {
                name = name.replace(replacement.toLowerCase(), replacement)
            }
        }
        return name
    }

    static String convertToCamelCase(String input, boolean capitaliseFirst) {
        String[] words = input.split("[\\W_]+")
        StringBuilder builder = new StringBuilder()
        for (int i = 0; i < words.length; i++) {
            String word = words[i]
            if(i!=0 || capitaliseFirst) {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)).toString() + word.substring(1).toLowerCase()
            }
            builder.append(word);
        }
        return builder.toString();
    }

    static EbnfParser.ExpressionContext calculateContainment(String name, def tableRow) {

        String pattern = tableRow.td[0].text()
        pattern = pattern.replaceAll("[►◄ \t\n,]", "").trim()

        //System.err.println(name)
        //System.err.println(pattern)

        if(pattern == "EMPTY" || pattern == "" ) {
            return new EbnfParser.ExpressionContext()
        }

        EbnfLexer lexer = new EbnfLexer(new ANTLRInputStream(pattern))
        EbnfParser parser = new EbnfParser(new CommonTokenStream(lexer))
        parser.buildParseTree = true
        return parser.expression()
    }


    static Map<String, String> attributeGroupMap = [
        "Universal attribute group": "Universal",
        "outputclass": "OutputClass",
        "keyref": "KeyRef",
        "Link relationship attribute group": "LinkRelationship",
        "Attributes common to many map elements": "CommonMapElements",
        "Architectural attribute group": "Architectural",
        "Topicref element attributes group": "TopicRefElement",
        "Complex-table attribute group": "ComplexTable",
        "Data element attributes group": "DataElement",
        "Date attributes group": "Date",
        "Display attribute group": "Display",
        "Simpletable attribute group": "SimpleTable",
        "Specialization attributes group": "Specialization"
    ]

    static List<String> replacements = [
            "Abstract",
            "Apply",
            "Area",
            "Body",
            "ChangeHistory",
            "Def",
            "Details",
            "Event",
            "EventType",
            "Head",
            "Id",
            "Information",
            "Key",
            "List",
            "Matter",
            "Meta",
            "Name",
            "Ref",
            "Set",
            "Subject"
    ]

    static Map<String, List<String>> attributeGroupItems = [
        "Universal": ["id", "conref", "conrefend", "conaction", "conkeyref", "props", "base", "platform", "product", "audience", "otherProps",
                      "deliveryTarget", "importance", "rev", "status", "translate", "xmlLang", "dir", "xtrf", "xtrc"],
        "OutputClass": ["outputClass"],
        "KeyRef": ["keyref"],
        "LinkRelationship": ["href", "format", "scope", "type"],
        "CommonMapElements": ["cascade", "collectionType", "processingRole", "lockTitle", "linking", "toc", "print", "search", "chunk", "keyscope"],
        "Architectural": ["ditaArchVersion", "ditaArch", "domains"],
        "TopicRefElement": ["copyTo", "navTitle", "query"],
        "ComplexTable": ["align", "char", "charoff", "colsep", "rowsep", "rowheader", "valign"],
        "DataElement": ["name", "datatype", "value"],
        "Date": ["expiry", "golive"],
        "Display": ["expanse", "frame", "scale"],
        "SimpleTable": ["keycol", "relcolwidth", "refcols"],
        "Specialization": ["specentry", "spectitle"]
    ]

}
