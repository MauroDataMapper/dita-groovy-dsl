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
package uk.ac.ox.softeng.dita.processor

import uk.ac.ox.softeng.maurodatamapper.dita.DitaProject
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Topic
import uk.ac.ox.softeng.maurodatamapper.dita.processor.DitaProcessor
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class DitaProcessorSpec extends Specification{

    DitaProcessor ditaProcessor

    def setup() {
        ditaProcessor = new DitaProcessor()
    }

    def cleanup() {

    }

    void "Test simple pdf generation"() {

        when:
        Topic testTopic = Topic.build(
            id: "myFirstTopic"
        ) {
            title "My first topic"
            body {
                p {
                    b "Hello, "
                    txt "World!"
                }
            }
        }

        DitaProject ditaProject = new DitaProject("My First DITA Project", "myFirstDitaProject")

        ditaProject.registerTopic("", testTopic)
        ditaProject.mainMap.topicRef {
            keyRef "myFirstTopic"
        }

        byte[] fileContents = ditaProcessor.generatePdf(ditaProject)
        Files.write(Paths.get('build/tmp/pdftest.pdf'), fileContents)


        then:
        fileContents.size() == 7771 // The number of bytes of the generated pdf file

    }

    void "Test simple docx generation"() {

        when:
        Topic testTopic = Topic.build(
            id: "myFirstTopic"
        ) {
            title "My first topic"
            body {
                p {
                    b "Hello, "
                    txt "World!"
                }
            }
        }

        DitaProject ditaProject = new DitaProject("My First DITA Project", "myFirstDitaProject")

        ditaProject.registerTopic("", testTopic)
        ditaProject.mainMap.topicRef {
            keyRef "myFirstTopic"
        }


        byte[] fileContents = ditaProcessor.generateDocx(ditaProject)
        Files.write(Paths.get('build/tmp/docxtest.docx'), fileContents)


        then:
        fileContents.size() == 68728 // The number of bytes of the generated doc file

    }
}
