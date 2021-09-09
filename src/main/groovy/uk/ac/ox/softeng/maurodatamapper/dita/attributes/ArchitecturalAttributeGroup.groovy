package uk.ac.ox.softeng.maurodatamapper.dita.attributes


import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait ArchitecturalAttributeGroup implements AttributeGroup {

    String ditaArchVersion
    String ditaArch
    SpaceSeparatedStringList domains

    Map attributeMap() {
        return [DITAArchVersion: ditaArchVersion,
                ditaarch: ditaArch,
                domains: domains
        ]
    }

    @Override
    List<String> validate() {
        return []
    }
}
