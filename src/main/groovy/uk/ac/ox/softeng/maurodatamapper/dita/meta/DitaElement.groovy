package uk.ac.ox.softeng.maurodatamapper.dita.meta

import groovy.xml.MarkupBuilder
import groovy.xml.MarkupBuilderHelper
import groovy.xml.XmlParser

trait DitaElement {

    static XmlParser xmlParser = new XmlParser()

    abstract def toXml(MarkupBuilder builder)


    String toXmlString() {
        StringWriter stringWriter = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(stringWriter)
        def helper = new MarkupBuilderHelper(builder)
        helper.xmlDeclaration([version:'1.0', encoding:'UTF-8', standalone:'no'])
        builder.setOmitNullAttributes(true)
        builder.setOmitEmptyAttributes(true)

        toXml(builder)

        return stringWriter.toString()
    }

    Node toXmlNode() {
        String xmlStr = toXmlString()
        xmlParser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)

        return xmlParser.parseText(xmlStr)

    }

    List<String> validate() {
        return []
    }

}
