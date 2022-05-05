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

import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.B
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.P
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Topic
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Format
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope

import groovy.xml.MarkupBuilder
import spock.lang.Specification

class XmlGenerationSpec extends Specification {

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
            b "This bit is in bold.  "
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
        Topic topic = Topic.build(id: "my_first_dita_topic") {
            title "Hello, World!"
            shortdesc "This is an example DITA topic, " +
                      "with some very simple content"
            body {
                p "Lorem ipsum dolor sit amet. Ut adipisci perspiciatis cum sunt vero est repudiandae " +
                  " et dicta nihil et optio repellendus aut omnis corporis. Ut sequi deleniti et " +
                  "voluptatem recusandae quo Quis expedita. Et ducimus minus qui ipsa rerum et " +
                  "suscipit libero vel quibusdam reiciendis."
                p "Sed asperiores quibusdam rem modi porro sit velit quia qui quidem labore eum minus " +
                  "tenetur qui adipisci similique ut dicta omnis! Vel perferendis voluptate ut facilis " +
                  "laborum cum rerum impedit rem nemo corporis ea veniam possimus qui explicabo " +
                  "laboriosam et velit minima. Qui eveniet officiis sit unde deserunt nam animi accusamus."
            }
            relatedLinks {
                link(format: Format.HTML, href: "http://www.docbook.org/", scope: Scope.EXTERNAL) {
                    linktext "DocBook 5"
                }
                link(format: Format.HTML, href: "http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=dita", scope: Scope.EXTERNAL) {
                    linktext "DITA"
                }
            }
        }

        expect:
        topic.toXmlString() == "<topic id='my_first_dita_topic'>\n" +
        "  <title>Hello, World!</title>\n" +
        "  <shortdesc>This is an example DITA topic, with some very simple content</shortdesc>\n" +
        "  <body>\n" +
        "    <p>Lorem ipsum dolor sit amet. Ut adipisci perspiciatis cum sunt vero est repudiandae  et dicta nihil et optio repellendus aut omnis " +
        "corporis. Ut sequi deleniti et voluptatem recusandae quo Quis expedita. Et ducimus minus qui ipsa rerum et suscipit libero vel quibusdam " +
        "reiciendis.</p>\n" +
        "    <p>Sed asperiores quibusdam rem modi porro sit velit quia qui quidem labore eum minus tenetur qui adipisci similique ut dicta omnis! " +
        "Vel perferendis voluptate ut facilis laborum cum rerum impedit rem nemo corporis ea veniam possimus qui explicabo laboriosam et velit " +
        "minima. Qui eveniet officiis sit unde deserunt nam animi accusamus.</p>\n" +
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


    def "First Equivalence - method with closure parameter"() {

        P exampleParagraph1 = P.build() {
            b {
                txt "Some bold text here"
            }
        }

        B exampleBold = B.build() {
            txt "Some bold text here"
        }

        P exampleParagraph2 = P.build() {
            b exampleBold
        }

        expect:
        exampleParagraph1.toXmlString() == exampleParagraph2.toXmlString()

    }

    def "Second Equivalence - text method synonym"() {

        P p1 = P.build {
            txt "Hello, World!"
        }
        P p2 = P.build {
            _ "Hello, World!"
        }
        P p3 = P.build {
            str "Hello, World!"
        }

        expect:
        p1.toXmlString() == p2.toXmlString() && p2.toXmlString() == p3.toXmlString()

    }

    def "Third Equivalence - text constructor"() {

        P p1 = P.build {
            b "Hello, World!"
        }
        P p2 = P.build {
            b {
                txt "Hello, World!"
            }
        }

        expect:
        p1.toXmlString() == p2.toXmlString()
    }

    def "Fourth Equivalence - dita content method"() {

        P p1 = P.build {
            ditaContent "\n  <b>Hello, World!</b>\n"
        }
        P p2 = P.build {
            b {
                txt "Hello, World!"
            }
        }

        expect:
        p1.toXmlString() == p2.toXmlString()
    }


}