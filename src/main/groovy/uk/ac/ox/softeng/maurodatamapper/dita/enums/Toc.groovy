package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Toc implements DitaEnum {

    YES("yes"),
    NO("no"),
    DITA_USE_CONREF_TARGET("-dita-use-conref-target")

    Toc(String stringValue) {
        this.stringValue = stringValue
    }
}