package uk.ac.ox.softeng.maurodatamapper.dita


import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder

class Prolog implements UniversalAttributeGroup, DitaElement {

    List<Author> authors
    Source source
    Publisher publisher
    List<Copyright> copyrights
    CritDates critDates
    Permissions permissions
    List<Metadata> metadata
    List<ResourceId> resourceIds
    List<Data> data
    List<SortAs> sortAs
    List<DataAbout> dataAbouts
    List<Foreign> foreigns
    List<Unknown> unknowns

    @Override
    def toXml(MarkupBuilder builder) {
        builder.prolog (attributeMap()) {

            if(authors) {
                authors.each {author ->
                    author.toXml(builder)
                }
            }
            if(source)
                source.toXml(builder)
            if(publisher)
                publisher.toXml(builder)
            if(copyrights) {
                copyrights.each {copyright ->
                    copyright.toXml(builder)
                }
            }
            if(critDates)
                critDates.toXml(builder)
            if(permissions)
                permissions.toXml(builder)

            if(metadata) {
                metadata.each {md ->
                    md.toXml(builder)
                }
            }
            if(resourceIds) {
                resourceIds.each {resourceId ->
                    resourceId.toXml(builder)
                }
            }
            if(data) {
                data.each {d ->
                    d.toXml(builder)
                }
            }
            if(sortAs) {
                sortAs.each {sa ->
                    sa.toXml(builder)
                }
            }
            if(dataAbouts) {
                dataAbouts.each {dataAbout ->
                    dataAbout.toXml(builder)
                }
            }
            if(foreigns) {
                foreigns.each {foreign ->
                    foreign.toXml(builder)
                }
            }
            if(unknowns) {
                unknowns.each {unknown ->
                    unknown.toXml(builder)
                }
            }
        }
    }

    @Override
    List<String> validate() {
        List<String> containedErrors = UniversalAttributeGroup.super.validate()
        return containedErrors
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        return ret
    }


}
