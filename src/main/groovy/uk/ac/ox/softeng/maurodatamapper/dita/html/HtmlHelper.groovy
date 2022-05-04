/*
 * Copyright 2020-2022 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package uk.ac.ox.softeng.maurodatamapper.dita.html

import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Div
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.XRef
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Format
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope
import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.util.logging.Slf4j
import groovy.xml.XmlParser
import org.w3c.tidy.Tidy

@Slf4j
class HtmlHelper {

    static Tidy tidy = new Tidy()
    static {
        Properties oProps = new Properties()
        //oProps.setProperty("new-empty-tags", "xref")
        oProps.setProperty("new-inline-tags", "xref, a, lq")
        // oProps.setProperty("new-pre-tags", "xref, a")
        // oProps.setProperty("vertical-space", "false")
        tidy.setDropFontTags(true)
        tidy.setConfigurationFromProps(oProps)
        tidy.setShowWarnings(false)
        tidy.setXmlTags(false)
        tidy.setInputEncoding("UTF-8")
        tidy.setOutputEncoding("UTF-8")
        tidy.setEncloseText(true)
        tidy.setEncloseBlockText(true)
        tidy.setXHTML(true)
        tidy.setMakeClean(true)
        tidy.setPrintBodyOnly(true)
        tidy.setWraplen(0)
        tidy.setQuiet(true)
        tidy.setNumEntities(true)
        tidy.setQuoteNbsp(false)

    }

    static XmlParser xmlParser = new XmlParser()


    static DitaElement replaceHtmlWithDita(String html) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()

        try {
            tidy.parse(new ByteArrayInputStream(html.getBytes()), baos)

        } catch(Exception e) {
            log.error("Couldn't tidy: " + html.getBytes())
        }

        //log.debug(node)
        //tidy.pprint(node, System.err)
        //log.debug(baos.toString())

        //log.debug("content2 : " + baos.toString())
        Node div
        try {
            div = xmlParser.parseText("<div>${baos.toString()}</div>")
            System.err.println(div)
        } catch(Exception e) {
            e.printStackTrace()
            log.error("Couldn't tidy: " + baos.toString())
            div = xmlParser.parseText("<div></div>")
        }

        Closure cl = {}
        div.children().each { childNode ->
            cl = cl >> nodeToDita((Node) childNode)
        }

        return Div.build(cl)

    }

    static Closure nodeToDita(Node node) {
        Closure cl = {}
        node.children().each { childNode ->
            if(childNode instanceof Node) {
                cl = nodeToDita(childNode) << cl
            } else if(childNode instanceof String) {
                cl = { txt childNode } << cl
            }
            else {
                System.err.println("Unhandled node type: ${childNode.getClass()} (${childNode})" )
            }
        }

        switch(node.name().toString().toLowerCase()) {
            case "p" :
                return {
                    p(updateAttributeMap(node), cl)
                }

            case "em":
                return {
                    i (updateAttributeMap(node), cl)
                }
            case "a":
                return replaceANode(node)
            case "h5":
            case "h4":
            case "h3":
            case "h2":
            case "h1":
                return {
                    p(updateAttributeMap(node), cl)
                }

            // Some tags we will purposefully drop:
            case "br":
            case "hr":
            case "img":
                return {}

            // By default, we'll drop and log an error
            default:
                log.error("Unrecognised html element!")
                log.error(node.toString())
                return { span {} }
        }
    }

    static final Map<String, String> attributeReplacements = ["class": "outputClass"]

    static final List<String> attributeRemovals = ["style", "target", "uin", "alias", "name", "title", "style", "value", "type", "color"]


    static Map<String, String> updateAttributeMap(Node node) {
        Map<String, String> attributes = node.attributes()
        attributeReplacements.each {oldAtt, newAtt ->
            if(attributes[oldAtt]) {
                attributes[newAtt] = attributes[oldAtt]
                attributes.remove(oldAtt)
            }
        }
        attributeRemovals.each {oldAtt ->
            attributes.remove(oldAtt)
        }

        return attributes
    }

    static Closure replaceANode(Node node) {
        if(!node.attributes()["href"]) {
            // drop empty anchor tags
            return {}
        }
        if(node.attributes()["href"].toString().startsWith("http")) {
            return {
                xRef (
                    scope: Scope.EXTERNAL,
                    format: Format.HTML,
                    href: node.attributes()["href"]
                ){
                    txt node.children().first()
                }
            }
        } else {
            return {
                xRef (
                    scope: Scope.EXTERNAL,
                    format: Format.HTML,
                    keyRef: node.attributes()["href"]
                ){
                    txt node.children().first()
                }
            }
        }
    }

}
