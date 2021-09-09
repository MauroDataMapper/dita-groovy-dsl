package uk.ac.ox.softeng.maurodatamapper.dita.attributes


import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup

trait OutputClassAttribute implements AttributeGroup {

    String outputClass

    Map attributeMap() {
        return [outputclass: outputClass
        ]
    }

    @Override
    List<String> validate() {
        return []
    }
}
