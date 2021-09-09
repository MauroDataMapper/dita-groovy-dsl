package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Importance
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Status
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait MetadataAttributeGroup implements AttributeGroup {

    SpaceSeparatedStringList props = []
    SpaceSeparatedStringList base = []

    SpaceSeparatedStringList platform = []
    SpaceSeparatedStringList product = []
    SpaceSeparatedStringList audience = []
    SpaceSeparatedStringList otherProps = []

    String deliveryTarget
    Importance importance
    String rev
    Status status

    Map attributeMap() {
        return [props: props,
                base: base,
                platform: platform,
                product: product,
                audience: audience,
                otherProps: otherProps,
                deliveryTarget: deliveryTarget,
                importance: importance,
                rev: rev,
                status: status
        ]
    }

    @Override
    List<String> validate() {
        return []
    }

}
