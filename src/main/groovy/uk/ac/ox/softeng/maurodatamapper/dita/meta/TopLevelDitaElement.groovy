package uk.ac.ox.softeng.maurodatamapper.dita.meta

import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder
import groovy.xml.MarkupBuilderHelper
import org.apache.commons.io.FilenameUtils

trait TopLevelDitaElement extends DitaElement {

    abstract String getDoctypeDecl()

    boolean outputAsFile(String filename) {
        String path = FilenameUtils.getFullPathNoEndSeparator(filename)
        new File(path).mkdirs()
        outputAsFile(new File(filename))
    }


    boolean outputAsFile(File outputFile) {


        FileWriter fileWriter = new FileWriter(outputFile)
        MarkupBuilder builder = new MarkupBuilder(fileWriter)
        builder.setOmitNullAttributes(true)
        builder.setOmitEmptyAttributes(true)

        def helper = new MarkupBuilderHelper(builder)
        helper.xmlDeclaration([version:'1.0', encoding:'UTF-8', standalone:'no'])
        helper.yieldUnescaped """${getDoctypeDecl()}\n"""
        toXml(builder)
        fileWriter.close()
        return true
    }

}
