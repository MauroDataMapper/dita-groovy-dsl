package uk.ac.ox.softeng.maurodatamapper.dita.elements

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttribute
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.HtmlElement

class Body implements UniversalAttributeGroup, OutputClassAttribute, HtmlElement {

    @Override
    String getNodeName() {
        return "body"
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

    Body(Object content) {
        setContent(content)
    }

}
