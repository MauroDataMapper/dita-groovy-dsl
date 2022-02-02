package uk.ac.ox.softeng.maurodatamapper.dita.elements

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.CommonMapElementsAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.LinkRelationshipAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttribute
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.TopicRefElementAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder

class MapRef implements UniversalAttributeGroup, LinkRelationshipAttributeGroup, CommonMapElementsAttributeGroup, TopicRefElementAttributeGroup,
    OutputClassAttribute, DitaElement {

    //DitaMap mapRef


    @Override
    def toXml(MarkupBuilder builder) {
        builder.mapref(attributeMap()) {
        }
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << LinkRelationshipAttributeGroup.super.attributeMap()
        ret << CommonMapElementsAttributeGroup.super.attributeMap()
        ret << OutputClassAttribute.super.attributeMap()
        ret << TopicRefElementAttributeGroup.super.attributeMap()

/*        if(!href && mapRef && mapRef.id) {
            ret["href"] = mapRef.id + ".ditamap"
        }

 */
        return ret
    }

}
