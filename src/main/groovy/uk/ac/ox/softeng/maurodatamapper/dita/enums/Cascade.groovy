package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Cascade implements DitaEnum {

    MERGE("merge"),
    NO_MERGE("nomerge")

    Cascade(String stringValue) {
        this.stringValue = stringValue
    }
}