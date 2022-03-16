package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait TopicRefElementAttributeGroup implements AttributeGroup {

	String copyTo
	String navTitle
	String query

	Map attributeMap() {
		return [
			"copy-to": copyTo,
			"navtitle": navTitle,
			"query": query,
		]
	}
	void copyTo(String copyTo) {
		this.copyTo = copyTo
	}

	void navTitle(String navTitle) {
		this.navTitle = navTitle
	}

	void query(String query) {
		this.query = query
	}

	@Override
	List<String> validate() {
		return []
	}
}
