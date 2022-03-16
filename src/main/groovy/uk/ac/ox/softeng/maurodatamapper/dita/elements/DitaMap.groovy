package uk.ac.ox.softeng.maurodatamapper.dita.elements


import uk.ac.ox.softeng.maurodatamapper.dita.attributes.ArchitecturalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.CommonMapElementsAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.IdAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.TopLevelDitaElement

import groovy.xml.MarkupBuilder

@Deprecated
class DitaMap implements TopLevelDitaElement, UniversalAttributeGroup, OutputClassAttributeGroup, ArchitecturalAttributeGroup, IdAttributeGroup, CommonMapElementsAttributeGroup {

    String doctypeDecl = """<!DOCTYPE map PUBLIC "-//OASIS//DTD DITA Map//EN" "map.dtd">"""

    Title title


    TopicMeta topicMeta
    List<Anchor> anchors = []
    List<Data> data = []
    List<SortAs> sortAs = []
    List<DataAbout> dataAbouts = []
    List<NavRef> navRefs = []
    List<RelTable> relTables = []
    List<TopicRef> topicRefs = []
    List<AnchorRef> anchorRefs = []
    List<KeyDef> keyDefs = []
    List<MapRef> mapRefs = []
    List<TopicGroup> topicGroups = []
    List<TopicHead> topicHeads = []
    List<TopicSet> topicSets = []
    List<TopicSetRef> topicSetRefs = []
    List<DitaValRef> ditaValRefs = []
    List<GlossRef> glossRefs = []

/*    def static make(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Topic) Closure closure) {
        Topic topic = new Topic()
        closure.delegate = topic
        closure()
        return topic
    }
*/

    def toXml(MarkupBuilder builder, boolean printSubTopics) {
        toXml(builder)
    }

    @Override
    def toXml(MarkupBuilder builder) {
        builder.map (attributeMap()) {
            if(title)
                title.toXml(builder)
            if(topicMeta) {
                topicMeta.toXml(builder)
            }
            if(keyDefs) {
                keyDefs.each {keyDef ->
                    keyDef.toXml(builder)
                }
            }
            if(topicRefs) {
                topicRefs.each {topicRef ->
                    topicRef.toXml(builder)
                }
            }
            if(mapRefs) {
                mapRefs.each {mapRef ->
                    mapRef.toXml(builder)
                }
            }
        }
    }

    @Override
    List<String> validate() {
        List<String> containedErrors = UniversalAttributeGroup.super.validate()
        containedErrors.addAll(OutputClassAttributeGroup.super.validate())
        return containedErrors
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttributeGroup.super.attributeMap()
        ret << ArchitecturalAttributeGroup.super.attributeMap()
        ret << IdAttributeGroup.super.attributeMap()
        ret << CommonMapElementsAttributeGroup.super.attributeMap()
        return ret
    }

    String setTitle(String titleString) {
        this.title = new Title(titleString)
    }

    String getFileSuffix() {
        ".ditamap"
    }

/*    @Override
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
*/

    void addTopicRef(Topic topic) {
        topicRefs << new TopicRef(topicRef: topic)
    }

    void addTopicRef(Topic topic, String href) {
        topicRefs << new TopicRef(topicRef: topic, href: href)
    }

    static DitaMap build(Closure closure) {
        new DitaMap().tap(closure)
    }

    void title(String title) {
        this.title = new Title(title)
    }

    void title(Title title) {
        this.title = title
    }

}
