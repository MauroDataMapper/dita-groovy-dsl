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
package uk.ac.ox.softeng.maurodatamapper.dita

//import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Topic


class Test {

    static void main(String[] args) {

/*        Topic topic = Topic.build {
            id "myTopicId"
            title "Elements"
        }

        System.err.println(topic.toXmlString())
*/

/*        DitaProject ditaProject = new DitaProject().tap {
            title = "NHS Data Dictionary"
            filename = "changePaper"
        }

        Topic topic = Topic.build {
            id "elements"
            title "Elements"

            shortDesc "Elements description"
            body: new Body({
                1..10.each {
                    p {
                        "Element $it"
                    }
                }
            })

            }
        System.err.println(topic.id)

        ditaProject.addTopic("", topic)

        ditaProject.writeToDirectory("/Users/james/git/mauro/plugins/dita-dsl/src/main/resources/output")

*/


/*        Closure bodyContent = { System.err.println(it)}

        bodyContent >>= {p1 {}}
        bodyContent >>= {p2 {}}
        bodyContent >>= {p3 {}}

        Closure newBody = { container {
            owner.with bodyContent
        }}

        Topic topic = new Topic(id: "myId",
                                title: new Title("My First Dita Topic"),
                                shortDesc: new ShortDesc("Short description here"),
                                body: new Body(newBody)) */
//                                    htmlContent:  { b -> b.p { b.mkp.yield "Hello, World!"} }))

        /*
                System.err.println(topic.validate())

                System.err.println(topic.outputAsString())

                DitaProcessor.generatePdf(topic)
        */

        //DitaMap ditaMap = new DitaMap(title: "My first DITA Map")
        //ditaMap.addTopicRef(topic)

        //System.err.println(ditaMap.validate())

        //System.err.println(ditaMap.outputAsString())
        // System.err.println(topic.outputAsString())

        //DitaProcessor.generatePdf(ditaMap, "/Users/james/git/mauro/plugins/dita-dsl/output.pdf")


    }



}
