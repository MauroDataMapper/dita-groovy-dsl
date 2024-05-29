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

import uk.ac.ox.softeng.maurodatamapper.dita.helpers.HtmlHelper

import spock.lang.Specification

class TidySpecification extends Specification {

    def before() {

    }

    def after() {


    }

    void "Incorrect close tag"() {
        when:
        String paragraph = "<p>Something</a>"
        then:
        assert (HtmlHelper.applyJTidy(paragraph).toString().trim() == "<p>Something</p>")
    }

    void "Empty Paragraph"() {
        when:
        String paragraph = "<p>Something</p><p/>"
        then:
        assert (HtmlHelper.applyJTidy(paragraph).toString().trim() == "<p>Something</p>")
    }

    void "Empty List Item"() {
        when:
        String paragraph = "<ul><li>Something</li><li/></ul>"
        then:
        assert (HtmlHelper.applyJTidy(paragraph).toString().trim() ==
"""<ul>
  <li>Something</li>
</ul>""")
    }


    void "Links in paragraphs in lis"() {
        when:
        String paragraph = "<ul><li><p><a>Link here</a> and some text</p></li></ul>"

        then:
        assert (HtmlHelper.applyJTidy(paragraph).toString().trim() ==
"""<ul>
  <li>
    <p>
    <a>Link here</a> and some text</p>
  </li>
</ul>""")

    }

    void "Links not in paragraphs in lis"() {
        when:
        String paragraph = "<ul><li><a>Link here</a> and some text</li></ul>"

        then:
        assert (HtmlHelper.applyJTidy(paragraph).toString().trim() ==
"""<ul>
  <li>
  <a>Link here</a> and some text</li>
</ul>""")

    }

    void "lis with brs in them"() {
        when:
        String paragraph = """<ul><li><br></li></ul>"""

        then:
        assert (HtmlHelper.applyJTidy(paragraph).toString().trim() ==
"""<ul>
  <li>
    <br />
  </li>
</ul>""")

        assert (HtmlHelper.replaceHtmlWithDita(paragraph).toXmlString(false) ==
                """<div />""")


    }

}
