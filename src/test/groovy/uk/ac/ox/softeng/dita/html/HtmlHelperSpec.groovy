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
package uk.ac.ox.softeng.dita.html

import uk.ac.ox.softeng.maurodatamapper.dita.html.HtmlHelper

import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.Diff
import spock.lang.Specification

class HtmlHelperSpec extends Specification{

    def setup() {
    }

    def cleanup() {

    }


    void "Test simple paragraph"() {

        when:
        String paragraph = "<p>Here is a paragraph</p>"
        String expectedResult = "<div>" + paragraph + "</div>"
        String result = HtmlHelper.replaceHtmlWithDita(paragraph).toXmlString()

        then:
        similarXml(result, expectedResult)
    }

    void "Test two paragraphs"() {

        when:
        String paragraph = "<p>Here is a paragraph</p><p>And here is another</p>"
        String expectedResult = "<div>" + paragraph + "</div>"
        String result = HtmlHelper.replaceHtmlWithDita(paragraph).toXmlString()

        then:
        similarXml(result, expectedResult)
    }

    void "Test simple paragraph with attributes for replacement"() {

        when:
        String paragraph = "<p class='myStyle'>Here is a paragraph</p>"
        String expectedResult = "<div><p outputclass='myStyle'>Here is a paragraph</p></div>"
        String result = HtmlHelper.replaceHtmlWithDita(paragraph).toXmlString()

        then:
        similarXml(result, expectedResult)
    }

    void "Test simple paragraph with attributes for removal"() {

        when:
        String paragraph = "<p style='font-weight: bold'>Here is a paragraph</p>"
        String expectedResult = "<div><p>Here is a paragraph</p></div>"
        String result = HtmlHelper.replaceHtmlWithDita(paragraph).toXmlString()

        then:
        similarXml(result, expectedResult)
    }

    void "Test paragraph with emphasis"() {

        when:
        String paragraph = "<p>Here is a <em>special</em> paragraph</p>"
        String expectedResult = "<div><p>Here is a <i>special</i> paragraph</p></div>"
        String result = HtmlHelper.replaceHtmlWithDita(paragraph).toXmlString()

        then:
        similarXml(result, expectedResult)
    }

    void "Test paragraph with an internal link"() {

        when:
        String paragraph = "<p>Here is a <a href='http://www.google.com'>link</a></p>"
        String expectedResult = "<div><p>Here is a <xref href='http://www.google.com' format='html' scope='external'>link</xref></p></div>"
        String result = HtmlHelper.replaceHtmlWithDita(paragraph).toXmlString()

        then:
        similarXml(result, expectedResult)
    }

    void "Test table with implicit width"() {

        when:
        String paragraph = """
<table>
  <tr>
    <td width="10%">A</td>
    <td width="10%">B</td>
    <td>C</td>
  </tr>
</table>
"""
        String expectedResult = """
<div>
  <table>
    <tgroup cols='3'>
      <colspec colname='col0' colwidth='10*' />
      <colspec colname='col1' colwidth='10*' />
      <colspec colname='col2' colwidth='80*' />
      <tbody>
        <row>
          <entry>A</entry>
          <entry>B</entry>
          <entry>C</entry>
        </row>
      </tbody>
    </tgroup>
  </table>
</div>
"""
        String result = HtmlHelper.replaceHtmlWithDita(paragraph).toXmlString()
        System.err.println(result)
        then:
        similarXml(result, expectedResult)
    }


    void "Test table with explicit width"() {

        when:
        String paragraph = """
<table>
  <tr>
    <td width="10%">A</td>
    <td width="10%">B</td>
    <td width="80%">C</td>
  </tr>
</table>
"""
        String expectedResult = """
<div>
  <table>
    <tgroup cols='3'>
      <colspec colname='col0' colwidth='10*' />
      <colspec colname='col1' colwidth='10*' />
      <colspec colname='col2' colwidth='80*' />
      <tbody>
        <row>
          <entry>A</entry>
          <entry>B</entry>
          <entry>C</entry>
        </row>
      </tbody>
    </tgroup>
  </table>
</div>
"""
        String result = HtmlHelper.replaceHtmlWithDita(paragraph).toXmlString()
        System.err.println(result)
        then:
        similarXml(result, expectedResult)
    }



    boolean similarXml(String s1, String s2) {
        Diff d = DiffBuilder.compare(Input.fromString(s1))
            .withTest(Input.fromString(s2))
            .ignoreWhitespace()
            .ignoreComments()
            .normalizeWhitespace()
            .checkForSimilar()
            .build()

        !d.hasDifferences()
    }

}
