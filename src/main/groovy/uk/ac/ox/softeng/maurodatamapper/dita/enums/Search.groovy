package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Search implements DitaEnum {

	YES ("yes"),
	NO ("no"),
	DITA_USE_CONREF_TARGET ("-dita-use-conref-target")


	Search (String stringValue) {
		this.stringValue = stringValue
	}

}
