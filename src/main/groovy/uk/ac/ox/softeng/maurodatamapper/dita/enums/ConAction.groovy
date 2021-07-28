package uk.ac.ox.softeng.maurodatamapper.dita.enums

enum ConAction implements DitaEnum {


    MARK("mark"),
    PUSH_AFTER("pushafter"),
    PUSH_BEFORE("pushbefore"),
    PUSH_REPLACE("pushreplace"),
    DITA_USE_CONREF_TARGET("-dita-use-conref-target")

    ConAction(String stringValue) {
        this.stringValue = stringValue
    }

}