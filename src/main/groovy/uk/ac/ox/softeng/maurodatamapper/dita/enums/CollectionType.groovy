package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum CollectionType implements DitaEnum {

	UNORDERED ("unordered"),
	SEQUENCE ("sequence"),
	CHOICE ("choice"),
	FAMILY ("family"),
	DITA_USE_CONREF_TARGET ("-dita-use-conref-target")


	CollectionType (String stringValue) {
		this.stringValue = stringValue
	}

}
