package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Dir
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Translate

trait LocalizationAttributeGroup {

    Translate translateText
    String xmlLangText
    Dir dirText


    def translate(Translate translate) {
        this.translateText = translate
    }

    def dir(Dir dir) {
        this.dirText = dir
    }



}
