package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.processor.DitaProcessor

import org.dita.dost.Processor
import org.dita.dost.ProcessorFactory
import org.dita.dost.util.Configuration

class Test {

    static void main(String[] args) {


        Topic topic = new Topic(id: "myId",
                                title: new Title(stringContent: "My First Dita Topic"),
                                shortDesc: new ShortDesc(stringContent: "Short description here"))

        System.err.println(topic.validate())

        System.err.println(topic.outputAsString())

        DitaProcessor.generatePDF(topic)

    }

}
