package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttribute
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement
import uk.ac.ox.softeng.maurodatamapper.dita.meta.HtmlElement

import groovy.xml.MarkupBuilder

class Anchor implements UniversalAttributeGroup, OutputClassAttribute, DitaElement {

    @Override
    Map attributeMap() {
        Map ret = [:]
        ret << UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttribute.super.attributeMap()
        return ret
    }

    @Override
    List<String> validate() {
        List<String> ret = []
        ret.addAll(OutputClassAttribute.super.validate())
        ret.addAll(UniversalAttributeGroup.super.validate())
        return ret
    }

    @Override

    @Override
    def toXml(MarkupBuilder builder) {
        builder.anchor(attributeMap()) {
        }
    }

}