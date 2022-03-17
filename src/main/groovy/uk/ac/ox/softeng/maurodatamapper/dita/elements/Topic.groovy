/*
 * Copyright 2020-2022 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package uk.ac.ox.softeng.maurodatamapper.dita.elements

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.ArchitecturalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.TopLevelDitaElement

import groovy.xml.MarkupBuilder

@Deprecated
class Topic implements TopLevelDitaElement, UniversalAttributeGroup, OutputClassAttributeGroup, ArchitecturalAttributeGroup {

    String doctypeDecl = """<!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA Topic//EN" "topic.dtd">"""

    Title title
    ShortDesc shortDesc
    Abstract topicAbstract
    Prolog prolog
    Body body
    RelatedLinks relatedLinks
    List<Topic> subTopics = []

    static Topic build(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Topic) Closure closure) {
        new Topic().tap(closure)
    }

    void title(String title) {
        this.title = new Title(title)
    }

    void title(Title title) {
        this.title = title
    }

    void shortDesc(String shortDesc) {
        this.shortDesc = new ShortDesc(shortDesc)
    }

    void shortDesc(ShortDesc shortDesc) {
        this.shortDesc = shortDesc
    }

    void topicAbstract(String topicAbstract) {
        this.topicAbstract = new Abstract(topicAbstract)
    }

    void topicAbstract(Abstract topicAbstract) {
        this.topicAbstract = topicAbstract
    }


    @Override
    def toXml(MarkupBuilder builder) {
        toXml(builder, true)
    }

    @Override
    def toXml(MarkupBuilder builder, boolean printSubTopics) {
        builder.topic (attributeMap()) {
            if(title)
                title.toXml(builder)
            if(shortDesc)
                shortDesc.toXml(builder)
            if(topicAbstract)
                topicAbstract.toXml(builder)
            if(prolog)
                prolog.toXml(builder)
            if(body)
                body.toXml(builder)
            if(relatedLinks)
                relatedLinks.toXml(builder)
            if(printSubTopics && subTopics) {
                subTopics.each {subTopic ->
                    subTopic.toXml(builder)
                }
            }
        }
    }

    @Override
    List<String> validate() {
        List<String> containedErrors = UniversalAttributeGroup.super.validate()
        containedErrors.addAll(OutputClassAttributeGroup.super.validate())
        if(!id || id == "") {
            containedErrors.add("Topic id is required")
        }
        return containedErrors
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttributeGroup.super.attributeMap()
        ret << ArchitecturalAttributeGroup.super.attributeMap()
        return ret
    }

    String getFileSuffix() {
        ".dita"
    }

    /*@Override
    Map<String, TopLevelDitaElement> subFilesForWriting() {
        return null
    }*/
}
