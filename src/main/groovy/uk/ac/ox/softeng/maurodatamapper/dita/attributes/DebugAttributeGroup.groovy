package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Dir
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Translate
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup

trait DebugAttributeGroup implements AttributeGroup {

    String xtrf
    String xtrc

    Map attributeMap() {
        return [xtrf: xtrf,
                xtrc: xtrc
        ]
    }

    @Override
    List<String> validate() {
        return []
    }
}
