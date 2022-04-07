package uk.ac.ox.softeng.maurodatamapper.dita.meta

import groovy.xml.MarkupBuilder

class TextElement extends DitaElement {

    String textContent

    TextElement(String content) {
        textContent = content
    }

    @Override
    def toXml(MarkupBuilder builder) {
        builder.mkp.yield textContent
    }

    @Override
    Map attributeMap() {
        return null
    }

    @Override
    String ditaNodeName() {
        return null
    }
}
