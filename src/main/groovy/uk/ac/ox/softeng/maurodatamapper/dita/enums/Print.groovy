package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum Print implements DitaEnum {

    YES("yes"),
    NO("no"),
    PRINT_ONLY("printonly"),
    DITA_USE_CONREF_TARGET("-dita-use-conref-target")

    Print(String stringValue) {
        this.stringValue = stringValue
    }
}