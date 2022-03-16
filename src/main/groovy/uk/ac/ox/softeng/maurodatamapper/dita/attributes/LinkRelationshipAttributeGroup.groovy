package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Format
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait LinkRelationshipAttributeGroup implements AttributeGroup {

	String href
	Format format
	Scope scope
	String type

	Map attributeMap() {
		return [
			"href": href,
			"format": format,
			"scope": scope,
			"type": type,
		]
	}
	void href(String href) {
		this.href = href
	}

	void format(Format format) {
		this.format = format
	}

	void scope(Scope scope) {
		this.scope = scope
	}

	void type(String type) {
		this.type = type
	}

	@Override
	List<String> validate() {
		return []
	}
}
