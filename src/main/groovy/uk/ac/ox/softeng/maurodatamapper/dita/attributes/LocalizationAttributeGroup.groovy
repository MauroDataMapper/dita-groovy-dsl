package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Dir
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Translate
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup

trait LocalizationAttributeGroup implements AttributeGroup {

    Translate translate
    String xmlLang
    Dir dir

    Map attributeMap() {
        return [translate: translate,
                xmlLang: xmlLang,
                dir: dir
        ]
    }

    @Override
    List<String> validate() {
        return []
    }

}
