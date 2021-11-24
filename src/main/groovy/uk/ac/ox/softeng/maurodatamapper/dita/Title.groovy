package uk.ac.ox.softeng.maurodatamapper.dita


import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttribute
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.HtmlElement

class Title implements UniversalAttributeGroup, HtmlElement, OutputClassAttribute {


    @Override
    String getNodeName() {
        "title"
    }

    @Override
    Map attributeMap() {
        Map ret = [:]
        ret << UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttribute.super.attributeMap()
        return ret
    }

    @Override
    List<String> validate() {
        List<String> ret = []
        ret.addAll(OutputClassAttribute.super.validate())
        ret.addAll(UniversalAttributeGroup.super.validate())
        return ret
    }
}
