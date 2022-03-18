package uk.ac.ox.softeng.maurodatamapper.dita.elements

import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder

class TextElement implements DitaElement {

    String textContent
    boolean isStructuredContent = false

    TextElement(String content, boolean isStructured = false) {
        this.textContent = content
        this.isStructuredContent = isStructured
    }

    def toXml(MarkupBuilder builder) {
        if(isStructuredContent) {
            builder.mkp.yieldUnescaped textContent
        } else {
            builder.mkp.yield textContent
        }

    }

}
