package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Cascade
import uk.ac.ox.softeng.maurodatamapper.dita.enums.CollectionType
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Linking
import uk.ac.ox.softeng.maurodatamapper.dita.enums.LockTitle
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Print
import uk.ac.ox.softeng.maurodatamapper.dita.enums.ProcessingRole
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Search
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Toc
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait CommonMapElementsAttributeGroup implements AttributeGroup {

    Cascade cascade
    CollectionType collectionType
    ProcessingRole processingRole
    LockTitle lockTitle
    Linking linking
    Toc toc
    Print print
    Search search
    SpaceSeparatedStringList chunk
    String keyscope

        Map attributeMap() {
        return [
            cascade: cascade,
            "collection-type": collectionType,
            "processing-role": processingRole,
            locktitle: lockTitle,
            linking: linking,
            toc: toc,
            print: print,
            search: search,
            chunk: chunk,
            keyscope: keyscope
        ]
    }

    @Override
    List<String> validate() {
        return []
    }
}
