package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait OutputClassAttributeGroup implements AttributeGroup {

	String outputClass

	Map attributeMap() {
		return [
			"outputclass": outputClass,
		]
	}
	void outputClass(String outputClass) {
		this.outputClass = outputClass
	}

	@Override
	List<String> validate() {
		return []
	}
}
