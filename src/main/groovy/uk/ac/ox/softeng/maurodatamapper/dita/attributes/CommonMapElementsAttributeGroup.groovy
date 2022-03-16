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
