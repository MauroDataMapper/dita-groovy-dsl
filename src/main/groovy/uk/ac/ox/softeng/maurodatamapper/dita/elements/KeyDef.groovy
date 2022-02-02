package uk.ac.ox.softeng.maurodatamapper.dita.elements

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.ArchitecturalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.CommonMapElementsAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.IdAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.LinkRelationshipAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttribute
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.TopicRefElementAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.enums.ProcessingRole
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

import groovy.xml.MarkupBuilder

class KeyDef implements UniversalAttributeGroup, LinkRelationshipAttributeGroup, CommonMapElementsAttributeGroup, TopicRefElementAttributeGroup,
    OutputClassAttribute, DitaElement {

    SpaceSeparatedStringList keys = []
    String href
    ProcessingRole processingRole

    @Override
    List<String> validate() {
        List<String> containedErrors = UniversalAttributeGroup.super.validate()
        containedErrors.addAll(OutputClassAttribute.super.validate())
        return containedErrors
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttribute.super.attributeMap()
        ret << LinkRelationshipAttributeGroup.super.attributeMap()
        ret << TopicRefElementAttributeGroup.super.attributeMap()
        ret << CommonMapElementsAttributeGroup.super.attributeMap()
        ret << [
            keys: keys,
            href: href,
            "processing-role": processingRole
        ]
        return ret
    }

    @Override
    def toXml(MarkupBuilder builder) {
        builder.keydef(attributeMap()) {
        }
    }


}
