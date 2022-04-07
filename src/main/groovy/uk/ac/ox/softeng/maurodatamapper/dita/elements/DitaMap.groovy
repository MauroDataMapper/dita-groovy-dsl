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
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.CommonMapElementsAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.IdAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.TopLevelDitaElement

import groovy.xml.MarkupBuilder

@Deprecated
class DitaMap extends TopLevelDitaElement implements UniversalAttributeGroup, OutputClassAttributeGroup, ArchitecturalAttributeGroup,
    IdAttributeGroup, CommonMapElementsAttributeGroup {

    String doctypeDecl = """<!DOCTYPE map PUBLIC "-//OASIS//DTD DITA Map//EN" "map.dtd">"""


    def toXml(MarkupBuilder builder, boolean printSubTopics) {
        toXml(builder)
    }

    @Override
    String ditaNodeName() {
        "map"
    }

    @Override
    def toXml(MarkupBuilder builder) {
        builder.map (attributeMap()) {

        }
    }

    @Override
    List<String> validate() {
        List<String> containedErrors = UniversalAttributeGroup.super.validate()
        containedErrors.addAll(OutputClassAttributeGroup.super.validate())
        return containedErrors
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        ret << OutputClassAttributeGroup.super.attributeMap()
        ret << ArchitecturalAttributeGroup.super.attributeMap()
        ret << IdAttributeGroup.super.attributeMap()
        ret << CommonMapElementsAttributeGroup.super.attributeMap()
        return ret
    }


    String getFileSuffix() {
        ".ditamap"
    }

/*    @Override
    Map<String, TopLevelDitaElement> subFilesForWriting() {
        Map ret = [:]
        if(topicRefs) {
            topicRefs.each {topicRef ->
                if(topicRef.getTopicRef()) {
                    ret[topicRef.getTopicRef().id + ".dita"] = topicRef.topicRef
                }
            }
        }
        return ret

    }
*/

}
