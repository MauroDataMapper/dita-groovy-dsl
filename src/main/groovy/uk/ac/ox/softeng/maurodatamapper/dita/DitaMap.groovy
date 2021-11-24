package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.ArchitecturalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.CommonMapElementsAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.IdAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttribute
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.TopLevelDitaElement

import groovy.xml.MarkupBuilder

class DitaMap implements TopLevelDitaElement, UniversalAttributeGroup, OutputClassAttribute, ArchitecturalAttributeGroup, IdAttributeGroup, CommonMapElementsAttributeGroup {

    String doctypeDecl = """<!DOCTYPE map PUBLIC "-//OASIS//DTD DITA Map//EN" "map.dtd">"""

    Title title


    TopicMeta topicMeta
    List<Anchor> anchors
    List<Data> data
    List<SortAs> sortAs
    List<DataAbout> dataAbouts
    List<NavRef> navRefs
    List<RelTable> relTables
    List<TopicRef> topicRefs
    List<AnchorRef> anchorRefs
    List<KeyDef> keyDefs
    List<MapRef> mapRefs
    List<TopicGroup> topicGroups
    List<TopicHead> topicHeads
    List<TopicSet> topicSets
    List<TopicSetRef> topicSetRefs
    List<DitaValRef> ditaValRefs
    List<GlossRef> glossRefs

/*    def static make(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Topic) Closure closure) {
        Topic topic = new Topic()
        closure.delegate = topic
        closure()
        return topic
    }
*/
    @Override
    def toXml(MarkupBuilder builder) {
        builder.map (attributeMap()) {
            if(title)
                title.toXml(builder)
            if(topicMeta) {
                topicMeta.toXml(builder)
            }
            if(topicRefs) {
                topicRefs.each {topicRef ->
                    topicRef.toXml(builder)
                }
            }
        }
    }

    @Override
    List<String> validate() {
        List<String> containedErrors = UniversalAttributeGroup.super.validate()
        containedErrors.addAll(OutputClassAttribute.super.validate())
        return containedErrors
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttribute.super.attributeMap()
        ret << ArchitecturalAttributeGroup.super.attributeMap()
        ret << IdAttributeGroup.super.attributeMap()
        ret << CommonMapElementsAttributeGroup.super.attributeMap()
        return ret
    }

    String setTitle(String titleString) {
        this.title = new Title(stringContent: titleString)
    }

    String getFileSuffix() {
        ".ditamap"
    }

    @Override
    Map<String, TopLevelDitaElement> subFilesForWriting() {
        Map ret = [:]
        if(topicRefs) {
            topicRefs.each {topicRef ->
                if(topicRef.getTopicRef()) {
                    ret[topicRef.getTopicRef().id + ".dita"] = topicRef.topicRef
                }
            }
        }
        return ret

    }

    void addTopicRef(Topic topic) {
        if(!topicRefs) {
            topicRefs = []
        }
        topicRefs << new TopicRef(topicRef: topic)


    }


}
