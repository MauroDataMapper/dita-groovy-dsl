package uk.ac.ox.softeng.maurodatamapper.dita.elements


import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.HtmlElement

@Deprecated
class Title implements UniversalAttributeGroup, HtmlElement, OutputClassAttributeGroup {


    @Override
    String getNodeName() {
        "title"
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

    Title(Object content) {
        setContent(content)
    }
}
