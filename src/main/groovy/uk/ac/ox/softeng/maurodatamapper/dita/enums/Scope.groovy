package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Scope implements DitaEnum {

    LOCAL("local"),
    PEER("peer"),
    EXTERNAL("external"),
    DITA_USE_CONREF_TARGET("-dita-use-conref-target")

    Scope(String stringValue) {
        this.stringValue = stringValue
    }
}