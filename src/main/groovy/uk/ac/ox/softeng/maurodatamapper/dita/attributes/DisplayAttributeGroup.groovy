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

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Expanse
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Frame
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scale
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup

trait DisplayAttributeGroup implements AttributeGroup {

	Expanse expanse
    Frame frame
    Scale scale

	Map attributeMap() {
		return [
			"expanse": expanse,
			"frame": frame,
            "scale": scale
		]
	}

    void expanse(Expanse expanse) {
        this.expanse = expanse
    }

    void frame(Frame frame) {
        this.frame = frame
    }

    void scale(Scale scale) {
        this.scale = scale
    }

    @Override
	List<String> validate() {
		return []
	}
}
