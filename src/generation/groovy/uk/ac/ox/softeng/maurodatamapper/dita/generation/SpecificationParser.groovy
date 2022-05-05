package uk.ac.ox.softeng.maurodatamapper.dita.generation

import uk.ac.ox.softeng.ebnf.parser.EbnfLexer
import uk.ac.ox.softeng.ebnf.parser.EbnfParser

import groovy.util.logging.Slf4j
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.ccil.cowan.tagsoup.Parser

import java.util.concurrent.Callable

import static uk.ac.ox.softeng.maurodatamapper.dita.generation.DocumentationParser.ATTRIBUTE_GROUP_ITEMS
import static uk.ac.ox.softeng.maurodatamapper.dita.generation.DocumentationParser.ATTRIBUTE_GROUP_MAP
import static uk.ac.ox.softeng.maurodatamapper.dita.generation.DocumentationParser.BASE_URL
import static uk.ac.ox.softeng.maurodatamapper.dita.generation.DocumentationParser.REPLACEMENTS

/**
 * @since 05/05/2022
 */
@Slf4j
class SpecificationParser implements Callable<Map<String, DitaElementSpecification>> {

    final Parser tagsoupParser
    final XmlSlurper slurper
    final String licenseHeader
    final String letter

    SpecificationParser(String licenseHeader, String letter) {
        tagsoupParser = new Parser()
        slurper = new XmlSlurper(tagsoupParser)
        this.letter = letter
        this.licenseHeader = licenseHeader
    }

    @Override
    Map<String, DitaElementSpecification> call() throws Exception {
        try {
            log.trace('Loading letter {}', letter)
            String fileUrl = BASE_URL + "cmlt${letter}.html"
            GPathResult doc = slurper.parse(fileUrl)
            return getSpecificationsFromWebPage(doc)
        } catch (Exception ignored) {
            // Assume it's either j or z which have no pages
            return [:]
        }
    }

    Map<String, DitaElementSpecification> getSpecificationsFromWebPage(GPathResult doc) {
        Map<String, DitaElementSpecification> elementMap = [:]
        doc.'**'.findAll {
            it.@class == 'section'
        }.each {section ->
            String href = section.h2.span.a.@href.text()
            String name = section.h2.span.a.code.text().replaceAll("[<|>]", "")
            GPathResult elementDescriptionDoc = slurper.parse(BASE_URL + href)
            String elementShortDescription = elementDescriptionDoc.'**'.find {
                it.@class == 'shortdesc'
            }.text()
            String attributesText = elementDescriptionDoc.'**'.find {
                it.@id.text().contains("__attributes")
            }.text()
            List<String> attributeGroupNames = []
            ATTRIBUTE_GROUP_MAP.each {key, value ->
                if (attributesText.contains(key)) {
                    attributeGroupNames.add(value)
                }
            }

            List<String> originalAttributes = []
            attributeGroupNames.each {
                originalAttributes.addAll(ATTRIBUTE_GROUP_ITEMS[it])
            }

            List<DitaAttributeSpecification> foundExtraAttributes = []

            def attributesSection = elementDescriptionDoc.'**'.find {
                it.name() == "section" && it.@id.text().contains("__attributes")
            }
            if (attributesSection) {
                def attributesDl = attributesSection.dl
                if (attributesDl.size() == 0) {
                    attributesDl = attributesSection.div.dl
                }
                if (attributesDl) {
                    attributesDl.dt.findAll {
                        it.@class.text().contains("dlterm")
                    }.each {dt ->
                        String extraAttName = dt.text().toString().replace('@', '')
                        boolean isRequired = extraAttName.contains("(REQUIRED)")
                        extraAttName = extraAttName.replace("(REQUIRED)", "")
                        boolean isDeprecated = extraAttName.contains("(DEPRECATED)")
                        extraAttName = extraAttName.replace("(DEPRECATED)", "")
                        extraAttName = extraAttName.replaceAll("[►◄ \t\n,]", "").trim()

                        if (!originalAttributes.contains(getAttributeName(extraAttName))) {
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

            EbnfParser.ExpressionContext expressionContext = calculateContainment(name, section.table[0].tbody.tr[0])

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
        elementMap
    }


    static List<String> getPackageName(String href) {
        String folder = href.replace("../", "").replace(".html", "")
        List<String> packageList = folder.split("/").collect {it.toLowerCase()}
        packageList.removeLast()
        packageList
    }


    static String getAttributeName(String elementName) {

        if (elementName.toLowerCase() == "href") {
            return "href"
        }
        String name = convertToCamelCase(elementName, false)
        REPLACEMENTS.each {replacement ->
            if (name.endsWith(replacement.toLowerCase()) && name != replacement.toLowerCase()) {
                name = name.replace(replacement.toLowerCase(), replacement)
            }
        }
        name
    }


    static String getClassName(String elementName) {

        String name = convertToCamelCase(elementName, true)
        REPLACEMENTS.each {replacement ->
            if (name.endsWith(replacement.toLowerCase()) && name != replacement.toLowerCase()) {
                name = name.replace(replacement.toLowerCase(), replacement)
            }
        }
        name
    }

    static String convertToCamelCase(String input, boolean capitaliseFirst) {
        String[] words = input.split("[\\W_]+")
        StringBuilder builder = new StringBuilder()
        for (int i = 0; i < words.length; i++) {
            String word = words[i]
            if (i != 0 || capitaliseFirst) {
                word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)).toString() + word.substring(1).toLowerCase()
            }
            builder.append(word)
        }
        builder.toString()
    }

    static EbnfParser.ExpressionContext calculateContainment(String name, def tableRow) {

        String pattern = tableRow.td[0].text()
        pattern = pattern.replaceAll("[►◄ \t\n,]", "").trim()

        if (pattern == "EMPTY" || pattern == "") {
            return new EbnfParser.ExpressionContext()
        }

        EbnfLexer lexer = new EbnfLexer(new ANTLRInputStream(pattern))
        EbnfParser parser = new EbnfParser(new CommonTokenStream(lexer))
        parser.buildParseTree = true
        parser.expression()
    }
}
