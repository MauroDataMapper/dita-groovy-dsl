package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.processor.DitaProcessor

import org.dita.dost.Processor
import org.dita.dost.ProcessorFactory
import org.dita.dost.util.Configuration

import java.awt.Dialog
import java.nio.file.Files

class Test {

    static void main(String[] args) {


        Topic topic = new Topic(id: "myId",
                                title: new Title(stringContent: "My First Dita Topic"),
                                shortDesc: new ShortDesc(stringContent: "Short description here"))
        /*
                System.err.println(topic.validate())

                System.err.println(topic.outputAsString())

                DitaProcessor.generatePdf(topic)
        */

        DitaMap ditaMap = new DitaMap(title: "My first DITA Map")
        ditaMap.addTopicRef(topic)

        System.err.println(ditaMap.validate())

        System.err.println(ditaMap.outputAsString())

        DitaProcessor.generatePdf(ditaMap, "/Users/james/git/mauro/plugins/dita-dsl/output.pdf")


    }

}
