package uk.ac.ox.softeng.maurodatamapper.dita.meta

import groovy.xml.MarkupBuilder

class DitaElementList implements DitaElement {

    protected List<DitaElement> elements = []
    protected List<Class> allowedElements = []

    DitaElementList(List<Class> classes) {
        this.allowedElements = classes
    }

    void add(DitaElement element) {
        elements.add(element)
    }

    def toXml(MarkupBuilder builder) {
        elements.each {it.toXml(builder) }
    }


}
