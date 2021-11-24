package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Format
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup

trait LinkRelationshipAttributeGroup implements AttributeGroup {

    String href
    Format format
    Scope scope

    //TODO: Enumerate these...
    String type


        Map attributeMap() {
        return [
            href: href,
            format: format,
            scope: scope,
            type: type
        ]
    }

    @Override
    List<String> validate() {
        return []
    }
}
