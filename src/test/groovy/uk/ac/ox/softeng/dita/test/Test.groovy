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
package uk.ac.ox.softeng.dita.test

import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Div
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.P
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Topic

class Test {

    static void main(String[] args) {

        System.out.println(System.getProperties().get('java.class.path'));

        P newP = P.build(outputClass: 'border') {
            txt 'Here is a new sentence.'
            b 'This bit is in bold.'

            txt 'And here is a third sentence.'

            ditaContent '<u>Here is some text inside a u.</u>'
        }

        Topic topic = Topic.build(id: 'myFirstTopic') {
            title 'My first Topic'
            body {
                p newP
            }

        }

        Div div = Div.build {
            p {
                txt 'Here is a paragraph'
            }
            p {
                txt 'Here is another'
            }
        }

        System.err.println(div.toXmlString())

    }
}
