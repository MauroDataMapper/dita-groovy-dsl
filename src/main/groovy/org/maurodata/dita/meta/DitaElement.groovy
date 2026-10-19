/*
 * Copyright 2020-2026 University of Oxford and NHS England
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
package org.maurodata.dita.meta

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import groovy.xml.MarkupBuilder
import groovy.xml.MarkupBuilderHelper
import groovy.xml.XmlParser

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

@Slf4j
@CompileStatic
abstract class DitaElement {
    static XmlParser xmlParser = new XmlParser(false, false)

    protected DitaElementList contents = new DitaElementList([])

    abstract String ditaNodeName()

    // Map constructors cannot be statically compiled, so set the properties explicitly
    static <T extends DitaElement> T withProperties(T element, Map args) {
        args.each {key, value ->
            element.setProperty(key as String, value)
        }
        element
    }

    void toXml(MarkupBuilder builder) {
        Closure childWriter = {
            for (DitaElement element : contents) {
                element.toXml(builder)
            }
        }
        builder.invokeMethod(ditaNodeName(), [attributeMap(), childWriter] as Object[])
    }

    void toXml(DitaXmlWriter writer) {
        writer.startElement(ditaNodeName(), attributeMap(), contents.isEmpty())
        if (!contents.isEmpty()) {
            for (DitaElement element : contents) {
                element.toXml(writer)
            }
            writer.endElement()
        }
    }

    String toXmlStreamString() {
        StringWriter stringWriter = new StringWriter()
        new DitaXmlWriter(stringWriter).withCloseable {writer ->
            toXml(writer)
        }
        stringWriter.toString()
    }

    String toXmlString(boolean includeXmlDeclaration = false) {
        StringWriter stringWriter = new StringWriter()
        MarkupBuilder builder = getMarkupBuilder(stringWriter)

        if (includeXmlDeclaration) {
            MarkupBuilderHelper helper = new MarkupBuilderHelper(builder)
            helper.xmlDeclaration([version: '1.0', encoding: 'UTF-8', standalone: 'no'])
        }

        toXml(builder)

        return stringWriter.toString()
    }

    Node toXmlNode() {
        String xmlStr = toXmlStreamString()
        xmlParser.setFeature('http://apache.org/xml/features/disallow-doctype-decl', true)
        return xmlParser.parseText(xmlStr)
    }

    abstract Map attributeMap()

    void ditaContent(String content) {
        contents.add(new TextualDitaContent(content))
    }

    MarkupBuilder getMarkupBuilder(Writer writer) {
        return new MarkupBuilder(writer).tap {
            omitNullAttributes = true
            omitEmptyAttributes = true
        }
    }

    MarkupBuilder getMarkupBuilder(OutputStream outputStream) {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
        return new MarkupBuilder(outputStreamWriter).tap {
            omitNullAttributes = true
            omitEmptyAttributes = true
        }
    }

    Path writeToFile(Path outputFile) {
        Path directory = outputFile.parent
        Files.createDirectories(directory)
        log.trace('Writing file: ' + outputFile)
        Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8).withCloseable {bufferedWriter ->
            writeDocument(bufferedWriter)
        }
        return outputFile

        /*        if(subFilesForWriting()) {
                    subFilesForWriting().each {entry ->
                        String filename = directory.getPath() + "/" + entry.key
                        entry.value.outputAsFile(new File(filename))
                    }
                }

         */
    }

    ByteArrayOutputStream writeToBuffer() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(baos, StandardCharsets.UTF_8)
        writeDocument(outputStreamWriter)
        return baos
    }

    private void writeDocument(Writer target) {
        new DitaXmlWriter(target).withCloseable {writer ->
            writer.prolog("<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n")
            String dtdl = getDoctypeDecl()
            if (dtdl) {
                writer.prolog("${dtdl}\n")
            }
            toXml(writer)
        }
    }

    String getDoctypeDecl() {
        switch (this.ditaNodeName()) {
            case 'topic':
                return '<!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA Topic//EN" "topic.dtd">'
            case 'map':
                return '<!DOCTYPE map PUBLIC "-//OASIS//DTD DITA Map//EN" "map.dtd">'
        }
        return null
    }

}
