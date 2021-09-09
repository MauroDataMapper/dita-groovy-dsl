package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.ConAction
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup

trait IdAttributeGroup implements AttributeGroup {

    String id
    String conref
    String conrefend
    ConAction conaction
    String conkeyref

    Map attributeMap() {
        return [id: id,
                conref: conref,
                conrefend: conrefend,
                conaction: conaction,
                conkeyref: conkeyref
        ]
    }

    @Override
    List<String> validate() {
        return []
    }

}
