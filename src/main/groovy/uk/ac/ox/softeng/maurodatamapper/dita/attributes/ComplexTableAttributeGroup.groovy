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

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Align
import uk.ac.ox.softeng.maurodatamapper.dita.enums.RowHeader
import uk.ac.ox.softeng.maurodatamapper.dita.enums.VAlign
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup

trait ComplexTableAttributeGroup implements AttributeGroup {
    Align align
    Character _char
    Integer charOff
    Boolean colSep
    Boolean rowSep
    RowHeader rowHeader
    VAlign vAlign

    @Deprecated
    String refCols

    Map attributeMap() {
        return [
            'align'    : align,
            'char'     : _char,
            'charoff'  : charOff,
            'colsep'   : colSep == null ? null : colSep.booleanValue() ? '1' : '0',
            'rowsep'   : rowSep == null ? null : rowSep.booleanValue() ? '1' : '0',
            'rowheader': rowHeader,
            'valign'   : vAlign
        ]
    }

    void align(Align align) {
        this.align = align
    }

    void _char(Character _char) {
        this._char = _char
    }

    void charOff(Integer charOff) {
        this.charOff = charOff
    }

    void colSep(Boolean colSep) {
        this.colSep = colSep
    }

    void rowSep(Boolean rowSep) {
        this.rowSep = rowSep
    }

    void rowHeader(RowHeader rowHeader) {
        this.rowHeader = rowHeader
    }

    void vAlign(VAlign vAlign) {
        this.vAlign = vAlign
    }

    @Override
    List<String> validate() {
        return []
    }
}
