package uk.ac.ox.softeng.maurodatamapper.dita.elements

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

    List<TopicRef> subTopicRefs = []

    @Override
    def toXml(MarkupBuilder builder) {
        builder.topicref(attributeMap()) {
            if(subTopicRefs) {
                subTopicRefs.each {subTopicRef ->
                    subTopicRef.toXml(builder)
                }
            }
        }
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << LinkRelationshipAttributeGroup.super.attributeMap()
        ret << CommonMapElementsAttributeGroup.super.attributeMap()
        ret << OutputClassAttribute.super.attributeMap()
        ret << TopicRefElementAttributeGroup.super.attributeMap()

        if(!href && topicRef && topicRef.id) {
            ret["href"] = topicRef.id + ".dita"
        }
        return ret
    }





}
