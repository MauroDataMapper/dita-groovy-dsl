package uk.ac.ox.softeng.maurodatamapper.dita.meta

import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder
import groovy.xml.MarkupBuilderHelper
import org.apache.commons.io.FilenameUtils

trait TopLevelDitaElement extends DitaElement {

    abstract String getFileSuffix()
    abstract String getDoctypeDecl()
    abstract Map<String, TopLevelDitaElement> subFilesForWriting()

    boolean outputAsFile(String filename) {
        String path = FilenameUtils.getFullPathNoEndSeparator(filename)
        new File(path).mkdirs()
        outputAsFile(new File(filename))
    }


    void outputAsFile(File outputFile) {
        System.err.println("Writing file: " + outputFile.name)

        FileWriter fileWriter = new FileWriter(outputFile)
        MarkupBuilder builder = new MarkupBuilder(fileWriter)
        builder.setOmitNullAttributes(true)
        builder.setOmitEmptyAttributes(true)

        def helper = new MarkupBuilderHelper(builder)
        helper.xmlDeclaration([version:'1.0', encoding:'UTF-8', standalone:'no'])
        helper.yieldUnescaped """${getDoctypeDecl()}\n"""
        toXml(builder)
        fileWriter.close()
        File directory = outputFile.getParentFile()
        if(subFilesForWriting()) {
            subFilesForWriting().each {entry ->
                String filename = directory.getPath() + "/" + entry.key
                entry.value.outputAsFile(new File(filename))
            }
        }
    }

    String outputAsString() {


        StringWriter stringWriter = new StringWriter()
        MarkupBuilder builder = new MarkupBuilder(stringWriter)
        builder.setOmitNullAttributes(true)
        builder.setOmitEmptyAttributes(true)

        def helper = new MarkupBuilderHelper(builder)
        helper.xmlDeclaration([version:'1.0', encoding:'UTF-8', standalone:'no'])
        helper.yieldUnescaped """${getDoctypeDecl()}\n"""
        toXml(builder)
        stringWriter.close()
        stringWriter.toString()
    }

}
