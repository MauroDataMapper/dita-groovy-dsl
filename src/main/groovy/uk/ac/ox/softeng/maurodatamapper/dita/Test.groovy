package uk.ac.ox.softeng.maurodatamapper.dita

import org.dita.dost.Processor
import org.dita.dost.ProcessorFactory
import org.dita.dost.util.Configuration

class Test {

    static void main(String[] args) {

        // Create a reusable processor factory with DITA-OT base directory
        ProcessorFactory pf = ProcessorFactory.newInstance(new File("/Users/james/git/mauro/plugins/dita-dsl/src/main/resources/dita-ot-3.6.1"))
        // and set the temporary directory
        pf.setBaseTempDir(new File("/Users/james/git/mauro/plugins/dita-dsl/src/main/resources/temp"))
        // Create a processor using the factory and configure the processor
        Processor p = pf.newProcessor("pdf2")
            .setInput(new File("/Users/james/git/mauro/plugins/dita-dsl/src/main/resources/examples/sample.ditamap"))
            .setOutputDir(new File("/Users/james/git/mauro/plugins/dita-dsl/src/main/resources/output"))
            .setProperty("nav-toc", "partial")
        // Run conversion
        p.run()


    }

}
