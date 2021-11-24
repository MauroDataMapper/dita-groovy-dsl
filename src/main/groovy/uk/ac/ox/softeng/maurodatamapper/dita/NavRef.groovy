package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttribute
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder

class NavRef implements UniversalAttributeGroup, OutputClassAttribute, DitaElement {

    String keyRef
    String mapRef

    @Override
    Map attributeMap() {
        Map ret = [:]
        ret << UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttribute.super.attributeMap()
        ret["keyref"] = keyRef
        ret["mapref"] = mapRef
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
    def toXml(MarkupBuilder builder) {
        builder.navref(attributeMap()) {

        }
    }
}