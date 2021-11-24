package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.ArchitecturalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttribute
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.TopLevelDitaElement

import groovy.xml.MarkupBuilder

class Topic implements TopLevelDitaElement, UniversalAttributeGroup, OutputClassAttribute, ArchitecturalAttributeGroup {

    String doctypeDecl = """<!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA Topic//EN" "topic.dtd">"""

    Title title
    ShortDesc shortDesc
    Abstract anAbstract
    Prolog prolog
    Body body
    RelatedLinks relatedLinks
    List<Topic> subTopics



/*    def static make(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Topic) Closure closure) {
        Topic topic = new Topic()
        closure.delegate = topic
        closure()
        return topic
    }
*/
    @Override
    def toXml(MarkupBuilder builder) {
        builder.topic (attributeMap()) {
            if(title)
                title.toXml(builder)
            if(shortDesc)
                shortDesc.toXml(builder)
            if(anAbstract)
                anAbstract.toXml(builder)
            if(prolog)
                prolog.toXml(builder)
            if(body)
                body.toXml(builder)
            if(relatedLinks)
                relatedLinks.toXml(builder)
            if(subTopics) {
                subTopics.each {subTopic ->
                    subTopic.toXml(builder)
                }
            }
        }
    }

    @Override
    List<String> validate() {
        List<String> containedErrors = UniversalAttributeGroup.super.validate()
        containedErrors.addAll(OutputClassAttribute.super.validate())
        if(!id || id == "") {
            containedErrors.add("Topic id is required")
        }
        return containedErrors
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttribute.super.attributeMap()
        ret << ArchitecturalAttributeGroup.super.attributeMap()
        return ret
    }

    String getFileSuffix() {
        ".dita"
    }

    @Override
    Map<String, TopLevelDitaElement> subFilesForWriting() {
        return null
    }
}
