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
package uk.ac.ox.softeng.maurodatamapper.dita.meta

import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup

import groovy.xml.MarkupBuilder

abstract class HtmlElement extends DitaElement {

    String stringContent
    String htmlStringContent
    def htmlContent

    abstract String getNodeName()

    abstract Map attributeMap()

    @Override
    def toXml(MarkupBuilder builder) {

        if(stringContent) {
            builder."${getNodeName()}"(attributeMap(), stringContent)
        } else if (htmlStringContent) {
            builder."${getNodeName()}"(attributeMap(), { builder.mkp.yieldUnescaped(htmlStringContent)})
        } else {
            builder."${getNodeName()}"(attributeMap(), { builder.with htmlContent } )
        }
    }

    void setContent(Object content) {
        if(content instanceof String) {
            if (content.contains("<")) {
                this.htmlStringContent = content
            } else {
                this.stringContent = content
            }
        } else {
            this.htmlContent = content
        }
    }

}