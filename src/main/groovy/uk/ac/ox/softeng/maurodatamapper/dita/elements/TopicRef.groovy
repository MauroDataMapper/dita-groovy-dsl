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

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.CommonMapElementsAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.LinkRelationshipAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.TopicRefElementAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder

@Deprecated
class TopicRef implements DitaElement, UniversalAttributeGroup, LinkRelationshipAttributeGroup, CommonMapElementsAttributeGroup,
    OutputClassAttributeGroup, TopicRefElementAttributeGroup {

    String stringRef
    Topic topicRef

    List<TopicRef> subTopicRefs = []

    @Override
    def toXml(MarkupBuilder builder) {
        builder.topicref(attributeMap()) {
            if(subTopicRefs) {
                subTopicRefs.each {subTopicRef ->
                    subTopicRef.toXml(builder)
                }
            }
        }
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << LinkRelationshipAttributeGroup.super.attributeMap()
        ret << CommonMapElementsAttributeGroup.super.attributeMap()
        ret << OutputClassAttributeGroup.super.attributeMap()
        ret << TopicRefElementAttributeGroup.super.attributeMap()

        if(!href && topicRef && topicRef.id) {
            ret["href"] = topicRef.id + ".dita"
        }
        return ret
    }





}
