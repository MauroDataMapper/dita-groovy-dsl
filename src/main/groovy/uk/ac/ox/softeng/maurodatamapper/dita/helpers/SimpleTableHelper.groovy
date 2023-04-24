package uk.ac.ox.softeng.maurodatamapper.dita.helpers

import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Simpletable

class SimpleTableHelper {

    static Simpletable createSimpletable(List<Map<String, String>> values, boolean displayHeader = true) {
        if(values.size() == 0) {
            return null
        }

        Simpletable.build() {
            if(displayHeader) {
                stHead {
                    values[0].keySet().each {key ->
                        stentry key
                    }
                }
            }
            values.each {map ->
                strow {
                    map.each {key, value ->
                        stentry value
                    }
                }

            }
        }



    }

}
