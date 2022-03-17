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
import uk.ac.ox.softeng.maurodatamapper.dita.enums.ProcessingRole
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

import groovy.xml.MarkupBuilder

@Deprecated
class KeyDef implements UniversalAttributeGroup, LinkRelationshipAttributeGroup, CommonMapElementsAttributeGroup, TopicRefElementAttributeGroup,
    OutputClassAttributeGroup, DitaElement {

    SpaceSeparatedStringList keys = []
    String href
    ProcessingRole processingRole

    @Override
    List<String> validate() {
        List<String> containedErrors = UniversalAttributeGroup.super.validate()
        containedErrors.addAll(OutputClassAttributeGroup.super.validate())
        return containedErrors
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttributeGroup.super.attributeMap()
        ret << LinkRelationshipAttributeGroup.super.attributeMap()
        ret << TopicRefElementAttributeGroup.super.attributeMap()
        ret << CommonMapElementsAttributeGroup.super.attributeMap()
        ret << [
            keys: keys,
            href: href,
            "processing-role": processingRole
        ]
        return ret
    }

    @Override
    def toXml(MarkupBuilder builder) {
        builder.keydef(attributeMap()) {
        }
    }


}
