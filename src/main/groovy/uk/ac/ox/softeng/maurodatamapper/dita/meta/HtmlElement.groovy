package uk.ac.ox.softeng.maurodatamapper.dita.meta

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup

import groovy.xml.MarkupBuilder

trait HtmlElement implements DitaElement {

    String stringContent
    String htmlContent

    abstract String getNodeName()

    abstract Map attributeMap()

    @Override
    def toXml(MarkupBuilder builder) {
        builder."${getNodeName()}"(attributeMap(), contentToXml(builder))
    }

    def contentToXml(MarkupBuilder builder) {

        if(stringContent) {
            return stringContent
        } else {
            builder.mkp.yieldUnescaped(htmlContent)
        }


    }

}