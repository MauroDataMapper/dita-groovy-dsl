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


import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder

@Deprecated
class Prolog implements UniversalAttributeGroup, DitaElement {

    List<Author> authors
    Source source
    Publisher publisher
    List<Copyright> copyrights
    CritDates critDates
    Permissions permissions
    List<Metadata> metadata
    List<ResourceId> resourceIds
    List<Data> data
    List<SortAs> sortAs
    List<DataAbout> dataAbouts
    List<Foreign> foreigns
    List<Unknown> unknowns

    @Override
    def toXml(MarkupBuilder builder) {
        builder.prolog (attributeMap()) {

            if(authors) {
                authors.each {author ->
                    author.toXml(builder)
                }
            }
            if(source)
                source.toXml(builder)
            if(publisher)
                publisher.toXml(builder)
            if(copyrights) {
                copyrights.each {copyright ->
                    copyright.toXml(builder)
                }
            }
            if(critDates)
                critDates.toXml(builder)
            if(permissions)
                permissions.toXml(builder)

            if(metadata) {
                metadata.each {md ->
                    md.toXml(builder)
                }
            }
            if(resourceIds) {
                resourceIds.each {resourceId ->
                    resourceId.toXml(builder)
                }
            }
            if(data) {
                data.each {d ->
                    d.toXml(builder)
                }
            }
            if(sortAs) {
                sortAs.each {sa ->
                    sa.toXml(builder)
                }
            }
            if(dataAbouts) {
                dataAbouts.each {dataAbout ->
                    dataAbout.toXml(builder)
                }
            }
            if(foreigns) {
                foreigns.each {foreign ->
                    foreign.toXml(builder)
                }
            }
            if(unknowns) {
                unknowns.each {unknown ->
                    unknown.toXml(builder)
                }
            }
        }
    }

    @Override
    List<String> validate() {
        List<String> containedErrors = UniversalAttributeGroup.super.validate()
        return containedErrors
    }

    Map attributeMap() {
        Map ret = UniversalAttributeGroup.super.attributeMap()
        return ret
    }


}
