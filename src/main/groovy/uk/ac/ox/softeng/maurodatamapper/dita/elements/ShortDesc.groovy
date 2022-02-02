package uk.ac.ox.softeng.maurodatamapper.dita.elements


import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttribute
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.HtmlElement;

class ShortDesc implements UniversalAttributeGroup, HtmlElement, OutputClassAttribute {


    @Override
    String getNodeName() {
        "shortdesc"
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

    ShortDesc(Object content) {
        setContent(content)
    }


}
