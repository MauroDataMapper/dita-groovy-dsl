package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait DebugAttributeGroup implements AttributeGroup {

	String xtrf
	String xtrc

	Map attributeMap() {
		return [
			"xtrf": xtrf,
			"xtrc": xtrc,
		]
	}
	void xtrf(String xtrf) {
		this.xtrf = xtrf
	}

	void xtrc(String xtrc) {
		this.xtrc = xtrc
	}

	@Override
	List<String> validate() {
		return []
	}
}
