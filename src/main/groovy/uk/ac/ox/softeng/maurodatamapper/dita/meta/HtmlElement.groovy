package uk.ac.ox.softeng.maurodatamapper.dita.meta

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup

import groovy.xml.MarkupBuilder

trait HtmlElement implements DitaElement {

    String stringContent
    String htmlStringContent
    def htmlContent

    abstract String getNodeName()

    abstract Map attributeMap()

    @Override
    def toXml(MarkupBuilder builder) {

        if(stringContent) {
            builder."${getNodeName()}"(attributeMap(), stringContent)
        } else if (htmlStringContent) {
            builder."${getNodeName()}"(attributeMap(), { builder.mkp.yieldUnescaped(htmlStringContent)})
        } else {
            builder."${getNodeName()}"(attributeMap(), { builder.with htmlContent } )
        }
    }

    void setContent(Object content) {
        if(content instanceof String) {
            if (content.contains("<")) {
                this.htmlStringContent = content
            } else {
                this.stringContent = content
            }
        } else {
            this.htmlContent = content
        }
    }

}