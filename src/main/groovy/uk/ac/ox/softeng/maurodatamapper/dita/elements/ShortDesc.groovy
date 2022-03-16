package uk.ac.ox.softeng.maurodatamapper.dita.elements


import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.HtmlElement;

@Deprecated
class ShortDesc implements UniversalAttributeGroup, HtmlElement, OutputClassAttributeGroup {


    @Override
    String getNodeName() {
        "shortdesc"
    }

    @Override
    Map attributeMap() {
        Map ret = [:]
        ret << UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttributeGroup.super.attributeMap()
        return ret
    }

    @Override
    List<String> validate() {
        List<String> ret = []
        ret.addAll(OutputClassAttributeGroup.super.validate())
        ret.addAll(UniversalAttributeGroup.super.validate())
        return ret
    }

    ShortDesc(Object content) {
        setContent(content)
    }


}
