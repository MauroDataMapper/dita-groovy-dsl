package uk.ac.ox.softeng.maurodatamapper.dita.elements

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder

@Deprecated
class NavRef implements UniversalAttributeGroup, OutputClassAttributeGroup, DitaElement {

    String keyRef
    String mapRef

    @Override
    Map attributeMap() {
        Map ret = [:]
        ret << UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttributeGroup.super.attributeMap()
        ret["keyref"] = keyRef
        ret["mapref"] = mapRef
        return ret
    }

    @Override
    List<String> validate() {
        List<String> ret = []
        ret.addAll(OutputClassAttributeGroup.super.validate())
        ret.addAll(UniversalAttributeGroup.super.validate())
        return ret
    }

    @Override
    def toXml(MarkupBuilder builder) {
        builder.navref(attributeMap()) {

        }
    }
}