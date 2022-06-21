package uk.ac.ox.softeng.dita.test

import uk.ac.ox.softeng.maurodatamapper.dita.html.HtmlHelper

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
