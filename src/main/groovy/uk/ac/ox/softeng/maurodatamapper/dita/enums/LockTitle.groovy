package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum LockTitle implements DitaEnum {

	YES ("yes"),
	NO ("no"),
	DITA_USE_CONREF_TARGET ("-dita-use-conref-target")


	LockTitle (String stringValue) {
		this.stringValue = stringValue
	}

}
