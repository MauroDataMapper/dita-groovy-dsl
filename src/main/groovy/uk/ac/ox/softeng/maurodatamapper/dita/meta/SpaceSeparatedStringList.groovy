package uk.ac.ox.softeng.maurodatamapper.dita.meta

import org.apache.commons.lang3.StringUtils


class SpaceSeparatedStringList extends ArrayList<String> {

    String toString() {
        return StringUtils.join(this, " ")
    }

    SpaceSeparatedStringList(Collection<String> collection) {
        super()
        if(collection) {
            this.addAll(collection)
        }
    }


}
