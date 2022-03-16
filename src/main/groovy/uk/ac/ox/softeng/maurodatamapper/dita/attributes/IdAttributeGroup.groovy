package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.enums.ConAction
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait IdAttributeGroup implements AttributeGroup {

	String id
	String conref
	String conrefend
	ConAction conaction
	String conkeyref

	Map attributeMap() {
		return [
			"id": id,
			"conref": conref,
			"conrefend": conrefend,
			"conaction": conaction,
			"conkeyref": conkeyref,
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
