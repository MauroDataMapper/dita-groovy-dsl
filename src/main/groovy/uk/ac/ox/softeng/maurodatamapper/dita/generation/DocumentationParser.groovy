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

import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import org.ccil.cowan.tagsoup.Parser

import java.util.regex.Matcher
import java.util.regex.Pattern

class DocumentationParser {

    static String BASE_PACKAGE_DIR = "/Users/james/git/mauro/plugins/dita-groovy-dsl/src/main/groovy/uk/ac/ox/softeng/maurodatamapper/dita"
    static String baseUrl = "https://docs.oasis-open.org/dita/dita/v1.3/errata02/os/complete/part3-all-inclusive/contentmodels/"

    static void main(String[] args) {

        def tagsoupParser = new Parser()
        XmlSlurper slurper = new XmlSlurper(tagsoupParser)
        Map<String, DitaElementSpecification> elementMap = [:]
        def chars = 'a'..'z'
        chars.toList().each { letter ->
            GPathResult doc
            try {
                String fileUrl = baseUrl + "cmlt${letter}.html"
                doc = slurper.parse(fileUrl)
            } catch (Exception e) {
                //e.printStackTrace()
                // Assume it's either j or z which have no pages
                return
            }
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
                List<ElementContainment> containedItems = []
                Set<String> foundElementNames = [] as Set
                Pattern manyItemsStarPattern = Pattern.compile("\\([^)]*\\)\\*")
                Pattern singleItemStarPattern = Pattern.compile("<([^>]*)>\\*")
                Pattern singleItemQMPattern = Pattern.compile("<([^>]*)>\\?")
                Pattern singleItemPattern = Pattern.compile("<([^>]*)>")

                boolean thisAllowsText = false
                section.table[0].tbody.tr.each { tr ->
                    tr.td.each { td ->
                        String containedItemsText = td.text()
                        if (containedItemsText.contains("text data")) {
                            thisAllowsText = true
                            containedItemsText = containedItemsText.replace("text data", "")
                        }
                        Matcher matcher = manyItemsStarPattern.matcher(containedItemsText)
                        while (matcher.find()) {
                            //System.err.println(matcher.group())
                            String manyItems = matcher.group()
                            containedItemsText = containedItemsText.replace(manyItems, "")
                            Matcher matcher2 = singleItemPattern.matcher(manyItems)
                            while (matcher2.find()) {
                                //System.err.println(matcher2.group(1))
                                if (!foundElementNames.contains(matcher2.group(1))) {
                                    containedItems.add(new ElementContainment(
                                            containedElementString: matcher2.group(1),
                                            allowMany: true,
                                            mustOccur: false
                                    ))
                                    foundElementNames.add(matcher2.group(1))
                                }
                            }
                        }
                        matcher = singleItemStarPattern.matcher(containedItemsText)
                        while (matcher.find()) {
                            String singleItem = matcher.group()
                            containedItemsText = containedItemsText.replace(singleItem, "")
                            if (!foundElementNames.contains(matcher.group(1))) {
                                containedItems.add(new ElementContainment(
                                        containedElementString: matcher.group(1),
                                        allowMany: true,
                                        mustOccur: false
                                ))
                                foundElementNames.add(matcher.group(1))
                            }

                        }
                        matcher = singleItemQMPattern.matcher(containedItemsText)
                        while (matcher.find()) {
                            String singleItem = matcher.group()
                            containedItemsText = containedItemsText.replace(singleItem, "")
                            if (!foundElementNames.contains(matcher.group(1))) {
                                containedItems.add(new ElementContainment(
                                        containedElementString: matcher.group(1),
                                        allowMany: false,
                                        mustOccur: false
                                ))
                                foundElementNames.add(matcher.group(1))
                            }

                        }
                        matcher = singleItemPattern.matcher(containedItemsText)
                        while (matcher.find()) {
                            String singleItem = matcher.group()
                            containedItemsText = containedItemsText.replace(singleItem, "")
                            if (!foundElementNames.contains(matcher.group(1))) {
                                containedItems.add(new ElementContainment(
                                        containedElementString: matcher.group(1),
                                        allowMany: false,
                                        mustOccur: true
                                ))
                                foundElementNames.add(matcher.group(1))
                            }

                        }

                        System.err.println(containedItemsText)
                    }
                }

                DitaElementSpecification ditaElementSpecification = new DitaElementSpecification().tap {
                    elementName = getClassName(name)
                    packagePath = getPackageName(href)
                    ditaName = name
                    description = elementShortDescription
                    attributeGroups = attributeGroupNames
                    contains = containedItems
                    contains.each {ec -> ec.containedBy = it}
                    allowsText = thisAllowsText
                }
                elementMap[name] = ditaElementSpecification
                //ditaElementSpecification.writeClassFile(BASE_PACKAGE_DIR)
            }

        }

        elementMap.each {name, spec ->
            //if(name.startsWith("a")) {
            spec.contains.each { containment ->
                if (!elementMap[containment.containedElementString]) {
                    System.err.println("Cannot find element: " + containment.containedElementString)
                } else {
                    containment.containedElement = elementMap[containment.containedElementString]
                }
            }
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
