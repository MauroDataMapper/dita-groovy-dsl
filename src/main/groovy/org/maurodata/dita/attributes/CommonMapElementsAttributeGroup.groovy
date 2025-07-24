/*
 * Copyright 2020-2025 University of Oxford and NHS England
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
package org.maurodata.dita.attributes

import org.maurodata.dita.enums.Cascade
import org.maurodata.dita.enums.CollectionType
import org.maurodata.dita.enums.Linking
import org.maurodata.dita.enums.LockTitle
import org.maurodata.dita.enums.Print
import org.maurodata.dita.enums.ProcessingRole
import org.maurodata.dita.enums.Search
import org.maurodata.dita.enums.Toc
import org.maurodata.dita.meta.AttributeGroup
import org.maurodata.dita.meta.SpaceSeparatedStringList

trait CommonMapElementsAttributeGroup implements AttributeGroup {
    Cascade cascade
    CollectionType collectionType
    ProcessingRole processingRole
    LockTitle lockTitle
    Linking linking
    Toc toc
    Print print
    Search search
    SpaceSeparatedStringList chunk = []
    String keyscope

    Map attributeMap() {
        return [
            'cascade'        : cascade,
            'collection-type': collectionType,
            'processing-role': processingRole,
            'locktitle'      : lockTitle,
            'linking'        : linking,
            'toc'            : toc,
            'print'          : print,
            'search'         : search,
            'chunk'          : chunk,
            'keyscope'       : keyscope,
        ]
    }

    void cascade(Cascade cascade) {
        this.cascade = cascade
    }

    void collectionType(CollectionType collectionType) {
        this.collectionType = collectionType
    }

    void processingRole(ProcessingRole processingRole) {
        this.processingRole = processingRole
    }

    void lockTitle(LockTitle lockTitle) {
        this.lockTitle = lockTitle
    }

    void linking(Linking linking) {
        this.linking = linking
    }

    void toc(Toc toc) {
        this.toc = toc
    }

    void print(Print print) {
        this.print = print
    }

    void search(Search search) {
        this.search = search
    }

    void chunk(SpaceSeparatedStringList chunk) {
        this.chunk = chunk
    }

    void chunk(Collection<String> chunk) {
        this.chunk = new SpaceSeparatedStringList(chunk)
    }

    void chunk(String chunk) {
        this.chunk = new SpaceSeparatedStringList(chunk.split(' ') as List)
    }

    void keyscope(String keyscope) {
        this.keyscope = keyscope
    }

    @Override
    List<String> validate() {
        return []
    }
}
