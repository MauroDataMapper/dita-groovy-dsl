package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Importance implements DitaEnum {

	OBSOLETE ("obsolete"),
	DEPRECATED ("deprecated"),
	OPTIONAL ("optional"),
	DEFAULT ("default"),
	LOW ("low"),
	NORMAL ("normal"),
	HIGH ("high"),
	RECOMMENDED ("recommended"),
	REQUIRED ("required"),
	URGENT ("urgent"),
	DITA_USE_CONREF_TARGET ("-dita-use-conref-target")


	Importance (String stringValue) {
		this.stringValue = stringValue
	}

}
