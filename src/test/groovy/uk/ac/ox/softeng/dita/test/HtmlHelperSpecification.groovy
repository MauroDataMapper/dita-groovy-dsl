package uk.ac.ox.softeng.dita.test

import uk.ac.ox.softeng.maurodatamapper.dita.html.HtmlHelper

import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.Diff
import spock.lang.Specification

class HtmlHelperSpecification extends Specification{

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
