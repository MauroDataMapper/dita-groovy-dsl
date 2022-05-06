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

import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Colspec
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Div
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.Table
import uk.ac.ox.softeng.maurodatamapper.dita.elements.langref.base.XRef
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Format
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Scope

import groovy.util.logging.Slf4j
import groovy.xml.XmlParser
import org.w3c.tidy.Tidy

@Slf4j
@SuppressWarnings('CatchException')
class HtmlHelper {
    static final Tidy TIDY = new Tidy()
    static {
        Properties oProps = new Properties()
        //oProps.setProperty('new-empty-tags', 'xref')
        oProps.setProperty('new-inline-tags', 'xref, a, lq')
        // oProps.setProperty('new-pre-tags', 'xref, a')
        // oProps.setProperty('vertical-space', 'false')
        TIDY.with {
            setDropFontTags(true)
            setConfigurationFromProps(oProps)
            setShowWarnings(false)
            setXmlTags(false)
            setInputEncoding('UTF-8')
            setOutputEncoding('UTF-8')
            setEncloseText(true)
            setEncloseBlockText(true)
            setXHTML(true)
            setMakeClean(true)
            setPrintBodyOnly(true)
            setWraplen(0)
            setQuiet(true)
            setNumEntities(true)
            setQuoteNbsp(false)
        }
    }

    static final XmlParser XML_PARSER = new XmlParser()

    static final Map<String, List<String>> ATTRIBUTE_GROUP_ITEMS = [
        'Universal'        : ['id', 'conref', 'conrefend', 'conaction', 'conkeyref', 'props', 'base', 'platform', 'product', 'audience', 'otherProps',
                              'deliveryTarget', 'importance', 'rev', 'status', 'translate', 'xmlLang', 'dir', 'xtrf', 'xtrc'],
        'OutputClass'      : ['outputClass'],
        'KeyRef'           : ['keyref'],
        'LinkRelationship' : ['href', 'format', 'scope', 'type'],
        'CommonMapElements': ['cascade', 'collectionType', 'processingRole', 'lockTitle', 'linking', 'toc', 'print', 'search', 'chunk', 'keyscope'],
        'Architectural'    : ['ditaArchVersion', 'ditaArch', 'domains'],
        'TopicRefElement'  : ['copyTo', 'navTitle', 'query'],
        'ComplexTable'     : ['align', 'char', 'charoff', 'colsep', 'rowsep', 'rowheader', 'valign'],
        'DataElement'      : ['name', 'datatype', 'value'],
        'Date'             : ['expiry', 'golive'],
        'Display'          : ['expanse', 'frame', 'scale'],
        'SimpleTable'      : ['keycol', 'relcolwidth', 'refcols'],
        'Specialization'   : ['specentry', 'spectitle'],
    ]

    static final List<String> ALL_ATTRIBUTES = ATTRIBUTE_GROUP_ITEMS.values().collectMany {it}

    static final Map<String, String> ATTRIBUTE_REPLACEMENTS = ['class'      : 'outputClass',
                                                               'outputclass': 'outputClass',]

    static final List<String> ATTRIBUTE_REMOVALS = ['style', 'target', 'uin', 'alias', 'name', 'title', 'style', 'value', 'type', 'color',]

    static Div replaceHtmlWithDita(String html) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()

        try {
            TIDY.parse(new ByteArrayInputStream(html.getBytes()), baos)
        } catch (Exception e) {
            log.error('Couldn\'t tidy: ' + html.getBytes())
        }

        //log.debug(node)
        //tidy.pprint(node, System.err)
        //log.debug(baos.toString())

        //log.debug('content2 : ' + baos.toString())
        Node div
        try {
            div = XML_PARSER.parseText("<div>${baos}</div>")
        } catch (Exception e) {
            log.error('Couldn\'t tidy: ' + baos, e)
            div = XML_PARSER.parseText('<div></div>')
        }

        Closure cl = {}
        div.children().each {childNode ->
            cl = cl >> nodeToDita((Node) childNode)
        }

        Div.build(cl)
    }

    static Closure nodeToDita(String node) {
        return {
            txt node
        }
    }

    static Closure nodeToDita(Node node) {
        Closure cl = {}
        if (node.name().toString().toLowerCase() == 'table') {
            return {
                table replaceTableNode(node)
            }
        }
        node.children().each {childNode ->
            if (childNode instanceof Node) {
                cl = nodeToDita(childNode) << cl
            } else if (childNode instanceof String) {
                cl = {txt childNode} << cl
            } else {
                log.warn("Unhandled node type: ${childNode.getClass()} (${childNode})")
            }
        }

        switch (node.name().toString().toLowerCase()) {
            case 'p':
                return {
                    p(updateAttributeMap(node), cl)
                }
            case 'div':
                return {
                    div(updateAttributeMap(node), cl)
                }
            case 'span':
                return {
                    div(updateAttributeMap(node), cl)
                }
            case 'em':
                return {
                    i(updateAttributeMap(node), cl)
                }
            case 'b':
                return {
                    b(updateAttributeMap(node), cl)
                }
            case 'strong':
                return {
                    b(updateAttributeMap(node), cl)
                }
            case 'a':
                if (node.attributes()['href']) {
                    return {
                        xRef replaceANode(node)
                    }
                }
                return {}
            case 'h5':
            case 'h4':
            case 'h3':
            case 'h2':
            case 'h1':
                return {
                    p(updateAttributeMap(node), cl)
                }
            case 'ul':
                return {
                    ul(updateAttributeMap(node), cl)
                }
            case 'li':
                return {
                    li(updateAttributeMap(node), cl)
                }
            case 'blockquote':
                return {
                    lq(updateAttributeMap(node), cl)
                }
            case 'code':
                return {
                    pre(updateAttributeMap(node), cl)
                }

                // Some tags we will purposefully drop:
            case 'br':
            case 'hr':
            case 'img':
                return {}

                // By default, we'll drop and log an error
            default:
                log.error('Unrecognised html element!')
                log.error(node.toString())
                return {}
        }
    }

    static Map<String, String> updateAttributeMap(Node node) {
        Map<String, String> attributes = node.attributes()
        ATTRIBUTE_REPLACEMENTS.each {oldAtt, newAtt ->
            if (attributes[oldAtt]) {
                attributes[newAtt] = attributes[oldAtt]
                attributes.remove(oldAtt)
            }
        }
        //attributeRemovals.each {oldAtt ->
        //    attributes.remove(oldAtt)
        //}
        attributes.keySet().each {key ->
            if (!ALL_ATTRIBUTES.contains(key)) {
                attributes.remove(key)
            }
        }

        attributes
    }

    static XRef replaceANode(Node node) {
        String href = node.attributes()['href']
        if (href.startsWith('http') || href.startsWith('mailto')) {
            return XRef.build(
                scope: Scope.EXTERNAL,
                format: Format.HTML,
                href: node.attributes()['href'],
                ) {
                txt node.children().first()
            }
        }
        XRef.build(
            scope: Scope.EXTERNAL,
            format: Format.HTML,
            keyRef: node.attributes()['href'],
            ) {
            txt node.children().first()
        }
    }

    static Table replaceTableNode(Node originalTable) {
        Node thead = originalTable.children().find {it instanceof Node && it.name().equalsIgnoreCase('thead')}
        Node tbody = originalTable.children().find {it instanceof Node && it.name().equalsIgnoreCase('tbody')}
        Node thContainer = thead ?: originalTable
        Node trContainer = tbody ?: originalTable

        List<Node> ths = thContainer.children().findAll {it instanceof Node && it.name().equalsIgnoreCase('tr')}
        List<Node> trs = trContainer.children().findAll {it instanceof Node && it.name().equalsIgnoreCase('tr')}

        List<Node> allRows = []
        allRows.addAll(ths)
        allRows.addAll(trs)

        List<Colspec> colSpecs = getColumnSpecifications(allRows.get(0))

        Table table = Table.build {
            tgroup(cols: colSpecs.size()) {
                colSpecs.each {
                    colspec it
                }
                if (ths) {
                    tHead {
                        ths.each {th ->
                        }
                    }
                }
                if (trs) {
                    tBody {
                        trs.each {tr ->
                            row getRowClosure(tr)
                        }
                    }
                }
            }
        }
        table
    }

    static Closure getRowClosure(Node tr) {
        return {
            List<Node> tds = tr.children().findAll {
                it instanceof Node &&
                (it.name().equalsIgnoreCase('td') || it.name().equalsIgnoreCase('th'))
            }
            int position = 0
//            int cols = 0
            for (int i = 0; i < tds.size(); i++) {
                Node td = tds[i]
                Integer entryMoreRows = null
                String entryNameSt = ''
                String entryNameEnd = ''
                String entryOutputClass = ''
                String entryScope = ''
                if (td.attributes()['rowspan']) {
                    entryMoreRows = Integer.parseInt(td.attributes()['rowspan'].toString()) - 1
                }
                if (td.attributes()['colspan']) {
                    int colspan = Integer.parseInt(td.attributes()['colspan'].toString())
                    entryNameSt = "col${position}"
                    position += (colspan - 1)
                    entryNameEnd = "col${position}"
                }
                position++
                if (td.name().equalsIgnoreCase('th') || td.attributes()['class'].toString().contains('duckblue')) {
                    entryScope = 'col'
                }
                if (td.attributes()['class']) {
                    entryOutputClass = td.attributes()['class'].toString()
                }
                Closure entryClosure = {}
                td.children().each {tdChild ->
                    entryClosure = entryClosure >> nodeToDita(tdChild)
                }

                entry([
                          scope      : entryScope,
                          namest     : entryNameSt,
                          nameend    : entryNameEnd,
                          outputClass: entryOutputClass,
                          morerows   : entryMoreRows
                      ], entryClosure)
            }
        }
    }

    /*
tGroup.cols = maxCols
for(int i=0;i<maxCols;i++) {
    tGroup.colSpecs.add(new ColSpec(colName: 'col${i+1}', colWidth: '1*'))
}
List<Node> tds = trs.first().children().findAll {it instanceof groovy.util.Node &&
                                                 (it.name().equalsIgnoreCase('td') || it.name().equalsIgnoreCase('th'))}
int idx = 0
tds.each { td ->
}

return table.toXmlNode()
}
*/

    static List<Colspec> getColumnSpecifications(Node tr) {
        List<Integer> widths = []

        tr.children().findAll {it instanceof Node && (it.name().equalsIgnoreCase('td') || it.name().equalsIgnoreCase('th'))}.each {Node td ->
            Integer colWidth = 1
            if (td.attributes()['width']) {
                colWidth = Integer.parseInt(td.attributes()['width'].toString().replace('%', ''))
            }
            Integer colSpan = 1
            if (td.attributes()['colspan']) {
                colSpan = Integer.parseInt(td.attributes()['colspan'].toString())
            }
            if (colWidth != 1 && colSpan > 1) {
                colWidth = (colWidth / colSpan).abs()
            }
            for (int i = 0; i < colSpan; i++) {
                widths.add(colWidth)
            }
        }
        int i = 0
        widths.collect {width ->
            new Colspec(
                colName: "col${i++}",
                colwidth: "${width}*"
            )
        }
    }
}
