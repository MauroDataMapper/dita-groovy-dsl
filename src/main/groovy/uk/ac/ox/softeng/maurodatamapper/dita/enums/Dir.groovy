package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Dir implements DitaEnum {

	LTR ("ltr"),
	RTL ("rtl"),
	LRO ("lro"),
	RLO ("rlo"),
	DITA_USE_CONREF_TARGET ("-dita-use-conref-target")


	Dir (String stringValue) {
		this.stringValue = stringValue
	}

}
