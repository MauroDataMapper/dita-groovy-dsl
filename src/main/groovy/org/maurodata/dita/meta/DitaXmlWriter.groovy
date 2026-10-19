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

import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamWriter

/**
 * Thin wrapper around an {@link XMLStreamWriter} which also allows pre-escaped content to be written verbatim.
 */
@CompileStatic
class DitaXmlWriter implements Closeable {

    private static final ThreadLocal<XMLOutputFactory> FACTORY = ThreadLocal.withInitial {XMLOutputFactory.newFactory()}

    private static class ElementState {
        boolean hasElementChildren
    }

    private final Writer writer
    private final XMLStreamWriter xml
    private final List<ElementState> elements = []

    DitaXmlWriter(Writer writer) {
        this.writer = writer
        this.xml = FACTORY.get().createXMLStreamWriter(writer)
    }

    /** Writes text directly to the output, before any element has been opened */
    void prolog(String text) {
        writer.write(text)
    }

    void startElement(String name, Map attributes, boolean empty) {
        if (!elements.isEmpty()) {
            elements[elements.size() - 1].hasElementChildren = true
            writeIndent(elements.size())
        }
        if (empty) {
            xml.writeEmptyElement(name)
        } else {
            xml.writeStartElement(name)
        }
        if (attributes != null) {
            for (Map.Entry<?, ?> attribute : attributes.entrySet()) {
                Object value = attribute.value
                if (value != null && !(value instanceof Collection && ((Collection) value).isEmpty())) {
                    String stringValue = value.toString()
                    if (stringValue) {
                        xml.writeAttribute(attribute.key.toString(), stringValue)
                    }
                }
            }
        }
        if (!empty) {
            elements.add(new ElementState())
        }
    }

    void endElement() {
        ElementState state = elements.remove(elements.size() - 1)
        if (state.hasElementChildren) {
            writeIndent(elements.size())
        }
        xml.writeEndElement()
    }

    void text(String text) {
        xml.writeCharacters(text)
    }

    /** Writes already escaped markup without further processing */
    void raw(String markup) {
        if (!markup) {
            return
        }
        xml.writeCharacters('')
        xml.flush()
        writer.write(markup)
    }

    private void writeIndent(int depth) {
        xml.writeCharacters('\n' + ('  ' * depth))
    }

    @Override
    void close() throws IOException {
        try {
            xml.writeEndDocument()
            xml.flush()
            xml.close()
        } catch (XMLStreamException exception) {
            throw new IOException(exception)
        }
        writer.flush()
    }
}
