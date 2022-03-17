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
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Cascade
import uk.ac.ox.softeng.maurodatamapper.dita.enums.CollectionType
import uk.ac.ox.softeng.maurodatamapper.dita.enums.ProcessingRole
import uk.ac.ox.softeng.maurodatamapper.dita.enums.LockTitle
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Linking
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Toc
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Print
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Search
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

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
			"cascade": cascade,
			"collection-type": collectionType,
			"processing-role": processingRole,
			"locktitle": lockTitle,
			"linking": linking,
			"toc": toc,
			"print": print,
			"search": search,
			"chunk": chunk,
			"keyscope": keyscope,
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
		this.chunk = new SpaceSeparatedStringList(chunk.split(" ") as List)
	}

	void keyscope(String keyscope) {
		this.keyscope = keyscope
	}

	@Override
	List<String> validate() {
		return []
	}
}
