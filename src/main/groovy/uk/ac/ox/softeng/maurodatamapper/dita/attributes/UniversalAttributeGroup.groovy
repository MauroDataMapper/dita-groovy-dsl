package uk.ac.ox.softeng.maurodatamapper.dita.attributes

trait UniversalAttributeGroup implements IdAttributeGroup, MetadataAttributeGroup, LocalizationAttributeGroup, DebugAttributeGroup {

    @Override
    Map attributeMap() {
        Map ret = [:]
        ret << IdAttributeGroup.super.attributeMap()
        ret << MetadataAttributeGroup.super.attributeMap()
        ret << LocalizationAttributeGroup.super.attributeMap()
        ret << DebugAttributeGroup.super.attributeMap()
        return ret
    }

    @Override
    List<String> validate() {
        List<String> ret = []
        ret.addAll(IdAttributeGroup.super.validate())
        ret.addAll(MetadataAttributeGroup.super.validate())
        ret.addAll(LocalizationAttributeGroup.super.validate())
        ret.addAll(DebugAttributeGroup.super.validate())
        return ret
    }


}
