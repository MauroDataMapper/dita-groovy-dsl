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
package uk.ac.ox.softeng.dita.test

import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.P
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Topic
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Format
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope

import groovy.xml.MarkupBuilder
import spock.lang.Specification

class XmlGenerationSpecification extends Specification {

    static MarkupBuilder markupBuilder

    def setupSpec() {

    }

    def "simple paragraph as xml"() {
        P p1 = P.build(outputClass: "border") {
            txt "Here is a new sentence.  "
            b {
                txt "This bit is in bold.  "
            }
            txt "And here is a third sentence."
        }

        P p2 = P.build(outputClass: "border") {
            txt "Here is a new sentence.  "
            b  "This bit is in bold.  "
            txt "And here is a third sentence."
        }

        String outputXml = "<p outputclass='border'>Here is a new sentence.  \n" +
        "  <b>This bit is in bold.  </b>And here is a third sentence.\n" +
        "</p>"

        expect:
        p1.toXmlString() == outputXml
        p2.toXmlString() == outputXml

    }

    def "simple topic as xml"() {
        Topic topic = Topic.build(id: "dockbook_or_dita") {
            title "DITA or DocBook?"
            shortdesc "Both DITA and DocBook are both mature, feature rich, document types,\n" +
                      "  so which one to choose?"
            body {
                p "DocBook 5 is a mature document type. It is well-documented (DocBook:\n" +
                  "    The Definitive Guide, DocBook XSL: The Complete Guide), featuring decent\n" +
                  "    XSL stylesheets allowing conversion to a variety of formats, based on the\n" +
                  "    best schema technologies: RELAX NG and Schematron."
                p ("DITA concepts (topics, maps, specialization, etc) have an immediate\n" +
                  "    appeal to the technical writer, making this document type more attractive\n" +
                  "    than DocBook. However the DocBook vocabulary is comprehensive and very\n" +
                  "    well thought out. So choose DITA if its technical vocabulary is\n" +
                  "    sufficiently expressive for your needs or if, anyway, you intend to\n" +
                  "    specialize DITA.")
            }
            relatedLinks {
                link (format: Format.HTML, href: "http://www.docbook.org/", scope: Scope.EXTERNAL) {
                    linktext "DocBook 5"
                }
                link (format: Format.HTML, href: "http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=dita", scope: Scope.EXTERNAL) {
                    linktext "DITA"
                }
            }
        }

        expect:
        topic.toXmlString() == "<topic id='dockbook_or_dita'>\n" +
        "  <title>DITA or DocBook?</title>\n" +
        "  <shortdesc>Both DITA and DocBook are both mature, feature rich, document types,\n" +
        "  so which one to choose?</shortdesc>\n" +
        "  <body>\n" +
        "    <p>DocBook 5 is a mature document type. It is well-documented (DocBook:\n" +
        "    The Definitive Guide, DocBook XSL: The Complete Guide), featuring decent\n" +
        "    XSL stylesheets allowing conversion to a variety of formats, based on the\n" +
        "    best schema technologies: RELAX NG and Schematron.</p>\n" +
        "    <p>DITA concepts (topics, maps, specialization, etc) have an immediate\n" +
        "    appeal to the technical writer, making this document type more attractive\n" +
        "    than DocBook. However the DocBook vocabulary is comprehensive and very\n" +
        "    well thought out. So choose DITA if its technical vocabulary is\n" +
        "    sufficiently expressive for your needs or if, anyway, you intend to\n" +
        "    specialize DITA.</p>\n" +
        "  </body>\n" +
        "  <related-links>\n" +
        "    <link href='http://www.docbook.org/' format='html' scope='external'>\n" +
        "      <linktext>DocBook 5</linktext>\n" +
        "    </link>\n" +
        "    <link href='http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=dita' format='html' scope='external'>\n" +
        "      <linktext>DITA</linktext>\n" +
        "    </link>\n" +
        "  </related-links>\n" +
        "</topic>"

    }


}
