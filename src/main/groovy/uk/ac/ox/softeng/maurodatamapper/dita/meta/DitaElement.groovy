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
package uk.ac.ox.softeng.maurodatamapper.dita.meta

import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder
import groovy.xml.MarkupBuilderHelper
import groovy.xml.XmlParser
import org.apache.commons.io.FilenameUtils

@Slf4j
abstract class DitaElement {

    static XmlParser xmlParser = new XmlParser()

    protected DitaElementList contents = new DitaElementList([])

    abstract String ditaNodeName()

    def toXml(MarkupBuilder builder) {
        builder.(ditaNodeName())(attributeMap()) {
            contents.each {element ->
                element.toXml(builder)
            }
        }
    }


    String toXmlString(boolean includeXmlDeclaration = false) {
        StringWriter stringWriter = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(stringWriter)

        if(includeXmlDeclaration) {
            def helper = new MarkupBuilderHelper(builder)
            helper.xmlDeclaration([version:'1.0', encoding:'UTF-8', standalone:'no'])
        }
        builder.setOmitNullAttributes(true)
        builder.setOmitEmptyAttributes(true)

        toXml(builder)

        return stringWriter.toString()
    }

    Node toXmlNode() {
        String xmlStr = toXmlString()
        xmlParser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        return xmlParser.parseText(xmlStr)
    }

    abstract Map attributeMap()

    void ditaContent(String content) {
        contents.add(new TextualDitaContent(content))
    }

    File outputAsFile(String filename) {
        String path = FilenameUtils.getFullPathNoEndSeparator(filename)
        new File(path).mkdirs()
        File mapFile = new File(filename)
        outputAsFile(mapFile)
        return mapFile
    }

    void outputAsFile(File outputFile) {
        log.debug("Writing file: " + outputFile.name)

        FileWriter fileWriter = new FileWriter(outputFile)
        MarkupBuilder builder = new MarkupBuilder(fileWriter)
        builder.setOmitNullAttributes(true)
        builder.setOmitEmptyAttributes(true)

        def helper = new MarkupBuilderHelper(builder)
        helper.xmlDeclaration([version:'1.0', encoding:'UTF-8', standalone:'no'])
        String dtdl = getDoctypeDecl()
        if(dtdl) {
            helper.yieldUnescaped """${getDoctypeDecl()}\n"""
        }
        toXml(builder)
        fileWriter.close()
        File directory = outputFile.getParentFile()
        /*        if(subFilesForWriting()) {
                    subFilesForWriting().each {entry ->
                        String filename = directory.getPath() + "/" + entry.key
                        entry.value.outputAsFile(new File(filename))
                    }
                }

         */
    }


    String getDoctypeDecl() {

        switch(this.ditaNodeName()) {
            case "topic":
                return """<!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA Topic//EN" "topic.dtd">"""
            case "map":
                return """<!DOCTYPE map PUBLIC "-//OASIS//DTD DITA Map//EN" "map.dtd">"""

        }
        return null

    }



}
