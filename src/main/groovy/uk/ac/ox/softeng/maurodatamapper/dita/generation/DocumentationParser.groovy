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

import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.ccil.cowan.tagsoup.Parser

class DocumentationParser {

    static String BASE_PACKAGE_DIR = "/Users/james/git/mauro/plugins/dita-dsl/build/generated-src/groovy/main/uk/ac/ox/softeng/maurodatamapper/dita"
    static String baseUrl = "https://docs.oasis-open.org/dita/dita/v1.3/errata02/os/complete/part3-all-inclusive/contentmodels/"

    def tagsoupParser = new Parser()
    XmlSlurper slurper = new XmlSlurper(tagsoupParser)

    Map<String, DitaElementSpecification> buildMapFromDocumentation() {

        Map<String, DitaElementSpecification> elementMap = [:]
        //def chars = 'a'..'z'
        def chars = 'a'..'z'
        chars.toList().each { letter ->
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
            }

            //ditaElementSpecification.writeClassFile(BASE_PACKAGE_DIR)
        }
        return elementMap
    }

    static void main(String[] args) {

        DocumentationParser documentationParser = new DocumentationParser()
        Map<String, DitaElementSpecification> elementMap = documentationParser.buildMapFromDocumentation()

        System.err.println(elementMap.size())

        elementMap.each {name, spec ->
            spec.containedElementNames.each {
                System.err.println(it)
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

            //if(name.startsWith("a")) {
            /*spec.contains.each { containment ->
                if (!elementMap[containment.containedElementString]) {
                    System.err.println("Cannot find element: " + containment.containedElementString)
                } else {
                    containment.containedElement = elementMap[containment.containedElementString]
                }
            }*/
            spec.writeClassFile(BASE_PACKAGE_DIR)
        }

    }

    static List<String> getPackageName(String href) {
        String folder = href.replace("../", "").replace(".html", "")
        List<String> packageList = folder.split("/").collect {it.toLowerCase()}
        packageList.removeLast()
        return packageList
    }


    static String getClassName(String elementName) {

        String name = convertToCamelCase(elementName)
        replacements.each {replacement ->
            if(name.endsWith(replacement.toLowerCase())) {
                name = name.replace(replacement.toLowerCase(), replacement)
            }
        }
        return name
    }

    static String convertToCamelCase(String input) {
        String[] words = input.split("[\\W_]+")
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)).toString() + word.substring(1).toLowerCase()

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
            "Link relationship attribute group": "LinkRelationship",
            "Attributes common to many map elements": "CommonMapElements",
            "Architectural attribute group": "Architectural",
            "Topicref element attributes group": "TopicRefElement"
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

}
