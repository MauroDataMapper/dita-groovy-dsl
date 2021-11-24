package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum ProcessingRole implements DitaEnum {

    NORMAL("normal"),
    RESOURCE_ONLY("resource-only"),
    DITA_USE_CONREF_TARGET("-dita-use-conref-target")

    ProcessingRole(String stringValue) {
        this.stringValue = stringValue
    }
}