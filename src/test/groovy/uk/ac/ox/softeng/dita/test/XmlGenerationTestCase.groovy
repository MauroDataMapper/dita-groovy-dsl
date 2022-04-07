package uk.ac.ox.softeng.dita.test

import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.P
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Topic

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
                link (format: "html", href: "http://www.docbook.org/", scope: "external") {
                    linktext "DocBook 5"
                }
            }
        }
        System.err.println(topic.toXmlString())

        expect:
        topic.toXmlString() == ""

    }


}
