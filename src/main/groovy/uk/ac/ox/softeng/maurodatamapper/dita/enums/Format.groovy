package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Format implements DitaEnum {

	DITA ("dita"),
	DITA_MAP ("ditamap"),
	NO_VALUE (""),
	HTML ("html"),
	PDF ("pdf"),
	TXT ("txt")


	Format (String stringValue) {
		this.stringValue = stringValue
	}

}
