package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Status implements DitaEnum {

	NEW ("new"),
	CHANGED ("changed"),
	DELETED ("deleted"),
	UNCHANGED ("unchanged"),
	DITA_USE_CONREF_TARGET ("-dita-use-conref-target")


	Status (String stringValue) {
		this.stringValue = stringValue
	}

}
