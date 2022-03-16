package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait ArchitecturalAttributeGroup implements AttributeGroup {

	String ditaArchVersion
	String ditaArch
	SpaceSeparatedStringList domains = []

	Map attributeMap() {
		return [
			"DITAArchVersion": ditaArchVersion,
			"ditaarch": ditaArch,
			"domains": domains,
		]
	}
	void ditaArchVersion(String ditaArchVersion) {
		this.ditaArchVersion = ditaArchVersion
	}

	void ditaArch(String ditaArch) {
		this.ditaArch = ditaArch
	}

	void domains(SpaceSeparatedStringList domains) {
		this.domains = domains
	}

	void domains(Collection<String> domains) {
		this.domains = new SpaceSeparatedStringList(domains)
	}

	void domains(String domains) {
		this.domains = new SpaceSeparatedStringList(domains.split(" ") as List)
	}

	@Override
	List<String> validate() {
		return []
	}
}
