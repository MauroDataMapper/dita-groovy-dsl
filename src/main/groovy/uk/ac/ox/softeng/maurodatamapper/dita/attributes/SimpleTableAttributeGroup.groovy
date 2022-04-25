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
package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait SimpleTableAttributeGroup implements AttributeGroup {

	Integer keyCol
    SpaceSeparatedStringList relColWidth

    @Deprecated
    String refCols

	Map attributeMap() {
		return [
			"keycol": keyCol,
			"relcolwidth": relColWidth
		]
	}

    void keyCol(Integer keyCol) {
        this.keyCol = keyCol
    }

    void relColWidth(SpaceSeparatedStringList domains) {
        this.relColWidth = domains
    }

    void relColWidth(Collection<String> relColWidth) {
        this.relColWidth = new SpaceSeparatedStringList(relColWidth)
    }

    void relColWidth(String relColWidth) {
        this.relColWidth = new SpaceSeparatedStringList(relColWidth.split(" ") as List)
    }

    @Override
	List<String> validate() {
		return []
	}
}
