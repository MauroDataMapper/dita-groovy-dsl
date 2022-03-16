package uk.ac.ox.softeng.maurodatamapper.dita.elements

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.ArchitecturalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.TopLevelDitaElement

import groovy.xml.MarkupBuilder

@Deprecated
class Topic implements TopLevelDitaElement, UniversalAttributeGroup, OutputClassAttributeGroup, ArchitecturalAttributeGroup {

    String doctypeDecl = """<!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA Topic//EN" "topic.dtd">"""

    Title title
    ShortDesc shortDesc
    Abstract topicAbstract
    Prolog prolog
    Body body
    RelatedLinks relatedLinks
    List<Topic> subTopics = []

    static Topic build(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Topic) Closure closure) {
        new Topic().tap(closure)
    }

    void title(String title) {
        this.title = new Title(title)
    }

    void title(Title title) {
        this.title = title
    }

    void shortDesc(String shortDesc) {
        this.shortDesc = new ShortDesc(shortDesc)
    }

    void shortDesc(ShortDesc shortDesc) {
        this.shortDesc = shortDesc
    }

    void topicAbstract(String topicAbstract) {
        this.topicAbstract = new Abstract(topicAbstract)
    }

    void topicAbstract(Abstract topicAbstract) {
        this.topicAbstract = topicAbstract
    }


    @Override
    def toXml(MarkupBuilder builder) {
        toXml(builder, true)
    }

    @Override
    def toXml(MarkupBuilder builder, boolean printSubTopics) {
        builder.topic (attributeMap()) {
            if(title)
                title.toXml(builder)
            if(shortDesc)
                shortDesc.toXml(builder)
            if(topicAbstract)
                topicAbstract.toXml(builder)
            if(prolog)
                prolog.toXml(builder)
            if(body)
                body.toXml(builder)
            if(relatedLinks)
                relatedLinks.toXml(builder)
            if(printSubTopics && subTopics) {
                subTopics.each {subTopic ->
                    subTopic.toXml(builder)
                }
            }
        }
    }

    @Override
    List<String> validate() {
        List<String> containedErrors = UniversalAttributeGroup.super.validate()
        containedErrors.addAll(OutputClassAttributeGroup.super.validate())
        if(!id || id == "") {
            containedErrors.add("Topic id is required")
        }
        return containedErrors
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttributeGroup.super.attributeMap()
        ret << ArchitecturalAttributeGroup.super.attributeMap()
        return ret
    }

    String getFileSuffix() {
        ".dita"
    }

    /*@Override
    Map<String, TopLevelDitaElement> subFilesForWriting() {
        return null
    }*/
}
