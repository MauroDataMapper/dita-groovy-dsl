package uk.ac.ox.softeng.maurodatamapper.dita.meta

import groovy.xml.MarkupBuilder

class DitaElementList extends ArrayList<DitaElement> {

    protected List<Class> allowedElements = []

    DitaElementList(List<Class> classes) {
        this.allowedElements = classes
    }


}
