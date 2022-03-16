package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Translate implements DitaEnum {

	YES ("yes"),
	NO ("no"),
	DITA_USE_CONREF_TARGET ("-dita-use-conref-target")


	Translate (String stringValue) {
		this.stringValue = stringValue
	}

}
