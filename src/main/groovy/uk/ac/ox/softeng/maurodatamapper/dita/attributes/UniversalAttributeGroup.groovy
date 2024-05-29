/*
 * Copyright 2020-2024 University of Oxford and NHS England
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
package uk.ac.ox.softeng.maurodatamapper.dita.attributes

trait UniversalAttributeGroup implements IdAttributeGroup, MetadataAttributeGroup, LocalizationAttributeGroup, DebugAttributeGroup {
    @Override
    Map attributeMap() {
        Map ret = [:]
        ret << IdAttributeGroup.super.attributeMap()
        ret << MetadataAttributeGroup.super.attributeMap()
        ret << LocalizationAttributeGroup.super.attributeMap()
        ret << DebugAttributeGroup.super.attributeMap()
        ret
    }

    @Override
    List<String> validate() {
        List<String> ret = []
        ret.addAll(IdAttributeGroup.super.validate())
        ret.addAll(MetadataAttributeGroup.super.validate())
        ret.addAll(LocalizationAttributeGroup.super.validate())
        ret.addAll(DebugAttributeGroup.super.validate())
        ret
    }

}
