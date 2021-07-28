package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.ConAction

trait IdAttributeGroup {

    String idText
    String conrefText
    String conrefendText
    ConAction conactionText
    String conkeyrefText

    def id(String id) {
        this.idText = id
    }

    def conref(String conref) {
        this.conrefText = conref
    }

    def conrefend(String conrefend) {
        this.conrefendText = conrefend
    }

    def conaction(ConAction conaction) {
        this.conactionText = conaction
    }

    def conkeyref(String conkeyref) {
        this.conkeyText = conkeyref
    }


}
