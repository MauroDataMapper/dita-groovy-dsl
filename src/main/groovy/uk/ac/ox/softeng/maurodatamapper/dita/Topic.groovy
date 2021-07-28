package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.IdAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.TopLevelDitaElement

import groovy.xml.MarkupBuilder

class Topic implements TopLevelDitaElement, UniversalAttributeGroup {

    String doctypeDecl = """<!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA Topic//EN" "topic.dtd">"""

    Title titleText



    def static make(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Topic) Closure closure) {
        Topic topic = new Topic()
        closure.delegate = topic
        closure()
        return topic
    }

    @Override
    def toXml(MarkupBuilder builder) {
        builder.topic (id: idText) {
        }
    }

    @Override
    List<String> validate() {
        List<String> containedErrors = super.validate()
        if(!idText || idText == "") {
            containedErrors.add("Topic id is required")
        }
        return containedErrors
    }
}
