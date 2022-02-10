package uk.ac.ox.softeng.maurodatamapper.dita

import uk.ac.ox.softeng.maurodatamapper.dita.elements.Body
import uk.ac.ox.softeng.maurodatamapper.dita.elements.ShortDesc
import uk.ac.ox.softeng.maurodatamapper.dita.elements.Title
import uk.ac.ox.softeng.maurodatamapper.dita.elements.Topic

class Test {

    static void main(String[] args) {

        DitaProject ditaProject = new DitaProject().tap {
            title = "NHS Data Dictionary"
            filename = "changePaper"
        }

        Topic topic = new Topic(id: "elements",
                                title: new Title("Elements"),
                                shortDesc: new ShortDesc("Elements description"),
                                body: new Body({
                                    1..10.each {
                                        p {
                                            "Element $it"
                                        }
                                    }
                                }))

        ditaProject.addTopic("", topic)

        ditaProject.writeToDirectory("/Users/james/git/mauro/plugins/dita-dsl/src/main/resources/output")




/*        Closure bodyContent = { System.err.println(it)}

        bodyContent >>= {p1 {}}
        bodyContent >>= {p2 {}}
        bodyContent >>= {p3 {}}

        Closure newBody = { container {
            owner.with bodyContent
        }}

        Topic topic = new Topic(id: "myId",
                                title: new Title("My First Dita Topic"),
                                shortDesc: new ShortDesc("Short description here"),
                                body: new Body(newBody)) */
//                                    htmlContent:  { b -> b.p { b.mkp.yield "Hello, World!"} }))

        /*
                System.err.println(topic.validate())

                System.err.println(topic.outputAsString())

                DitaProcessor.generatePdf(topic)
        */

        //DitaMap ditaMap = new DitaMap(title: "My first DITA Map")
        //ditaMap.addTopicRef(topic)

        //System.err.println(ditaMap.validate())

        //System.err.println(ditaMap.outputAsString())
        // System.err.println(topic.outputAsString())

        //DitaProcessor.generatePdf(ditaMap, "/Users/james/git/mauro/plugins/dita-dsl/output.pdf")


    }

}
