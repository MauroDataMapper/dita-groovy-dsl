package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Linking implements DitaEnum {

	TARGET_ONLY ("targetonly"),
	SOURCE_ONLY ("sourceonly"),
	NORMAL ("normal"),
	NONE ("none"),
	DITA_USE_CONREF_TARGET ("-dita-use-conref-target")


	Linking (String stringValue) {
		this.stringValue = stringValue
	}

}
