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

import uk.ac.ox.softeng.maurodatamapper.dita.enums.ConAction
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup

trait IdAttributeGroup implements AttributeGroup {
    String id
    String conref
    String conrefend
    ConAction conaction
    String conkeyref

    Map attributeMap() {
        return [
            'id'       : id,
            'conref'   : conref,
            'conrefend': conrefend,
            'conaction': conaction,
            'conkeyref': conkeyref,
        ]
    }

    void id(String id) {
        this.id = id
    }

    void conref(String conref) {
        this.conref = conref
    }

    void conrefend(String conrefend) {
        this.conrefend = conrefend
    }

    void conaction(ConAction conaction) {
        this.conaction = conaction
    }

    void conkeyref(String conkeyref) {
        this.conkeyref = conkeyref
    }

    @Override
    List<String> validate() {
        return []
    }
}
