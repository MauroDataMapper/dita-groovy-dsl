package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Dir
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Translate

trait DebugAttributeGroup {

    String xtrfText
    String xtrcText


    def xtrf(String xtrf) {
        this.xtrfText = xtrf
    }

    def xtrc(String xtrc) {
        this.xtrcText = xtrc
    }
}
