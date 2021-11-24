package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Format
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup

trait TopicRefElementAttributeGroup implements AttributeGroup {

    String copyTo
    String navTitle
    String query


    Map attributeMap() {
        return [
            "copy-to": copyTo,
            navtitle: navTitle,
            query: query
        ]
    }

    @Override
    List<String> validate() {
        return []
    }
}
