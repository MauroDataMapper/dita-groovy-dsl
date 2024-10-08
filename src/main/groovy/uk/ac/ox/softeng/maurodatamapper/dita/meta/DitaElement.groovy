/*
 * Copyright 2020-2024 University of Oxford and NHS England
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

import java.nio.file.Files
import java.nio.file.Path

@Slf4j
abstract class DitaElement {
    static XmlParser xmlParser = new XmlParser(false, false)

    protected DitaElementList contents = new DitaElementList([])

    abstract String ditaNodeName()

    void toXml(MarkupBuilder builder) {
        builder.(ditaNodeName())(attributeMap()) {
            contents.each {element ->
                element.toXml(builder)
            }
        }
    }

    String toXmlString(boolean includeXmlDeclaration = false) {
        StringWriter stringWriter = new StringWriter()
        MarkupBuilder builder = getMarkupBuilder(stringWriter)

        if (includeXmlDeclaration) {
            MarkupBuilderHelper helper = new MarkupBuilderHelper(builder)
            helper.xmlDeclaration([version: '1.0', encoding: 'UTF-8', standalone: 'no'])
        }

        toXml(builder)

        stringWriter.toString()
    }

    Node toXmlNode() {
        String xmlStr = toXmlString()
        xmlParser.setFeature('http://apache.org/xml/features/disallow-doctype-decl', true)
        xmlParser.parseText(xmlStr)
    }

    abstract Map attributeMap()

    void ditaContent(String content) {
        contents.add(new TextualDitaContent(content))
    }

    MarkupBuilder getMarkupBuilder(Writer writer) {
        new MarkupBuilder(writer).tap {
            omitNullAttributes = true
            omitEmptyAttributes = true
        }
    }

    Path writeToFile(Path outputFile) {
        Path directory = outputFile.parent
        Files.createDirectories(directory)
        log.debug('Writing file: ' + outputFile)
        outputFile.newWriter().withCloseable {bufferedWriter ->
            MarkupBuilder builder = getMarkupBuilder(bufferedWriter)
            MarkupBuilderHelper helper = new MarkupBuilderHelper(builder)
            helper.xmlDeclaration([version: '1.0', encoding: 'UTF-8', standalone: 'no'])
            String dtdl = getDoctypeDecl()
            if (dtdl) {
                helper.yieldUnescaped "${getDoctypeDecl()}\n"
            }
            toXml(builder)
        }
        outputFile

        /*        if(subFilesForWriting()) {
                    subFilesForWriting().each {entry ->
                        String filename = directory.getPath() + "/" + entry.key
                        entry.value.outputAsFile(new File(filename))
                    }
                }

         */
    }

    String getDoctypeDecl() {
        switch (this.ditaNodeName()) {
            case 'topic':
                return '<!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA Topic//EN" "topic.dtd">'
            case 'map':
                return '<!DOCTYPE map PUBLIC "-//OASIS//DTD DITA Map//EN" "map.dtd">'
        }
        null
    }

}
