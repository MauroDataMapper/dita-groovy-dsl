package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.CommonMapElementsAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.LinkRelationshipAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttribute
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.TopicRefElementAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder

class TopicRef implements DitaElement, UniversalAttributeGroup, LinkRelationshipAttributeGroup, CommonMapElementsAttributeGroup,
    OutputClassAttribute, TopicRefElementAttributeGroup {

    String stringRef
    Topic topicRef

    @Override
    def toXml(MarkupBuilder builder) {
        builder.topicref(attributeMap()) {
        }
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << LinkRelationshipAttributeGroup.super.attributeMap()
        ret << CommonMapElementsAttributeGroup.super.attributeMap()
        ret << OutputClassAttribute.super.attributeMap()
        ret << TopicRefElementAttributeGroup.super.attributeMap()

        if(topicRef && topicRef.id) {
            ret["href"] = topicRef.id + ".dita"
        }
        return ret
    }





}
