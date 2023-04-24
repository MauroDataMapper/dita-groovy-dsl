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
        // oProps.setProperty('new-empty-tags', 'xref')
        //oProps.setProperty('new-inline-tags', 'xref, a, lq')
        // oProps.setProperty('new-pre-tags', 'xref, a')
        // oProps.setProperty('vertical-space', 'false')
        TIDY.with {
            //setConfigurationFromProps(oProps)
            setDropFontTags(true)
            setDropEmptyParas(true)
            setShowWarnings(false)
            setXmlTags(false)
            setInputEncoding('UTF-8')
            setOutputEncoding('UTF-8')
            //setEncloseText(true)
            //setEncloseBlockText(true)
            setXmlOut(true)
            setMakeClean(true)
            setPrintBodyOnly(true)
            setWraplen(0)
            setQuiet(true)
            setNumEntities(true)
            setQuoteNbsp(false)
            setTrimEmptyElements(true)
        }
    }

    static final XmlParser XML_PARSER = new XmlParser(false, false)

    static final Map<String, List<String>> ATTRIBUTE_GROUP_ITEMS = [
        'Universal'        : ['id', 'conref', 'conrefend', 'conaction', 'conkeyref', 'props', 'base', 'platform', 'product', 'audience', 'otherProps',
                              'deliveryTarget', 'importance', 'rev', 'status', 'translate', 'xmlLang', 'dir', 'xtrf', 'xtrc'],
        'OutputClass'      : ['outputClass'],
        'KeyRef'           : ['keyref'],
        'Keys'             : ['keys'],
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

    static final List<String> ATTRIBUTE_REMOVALS = ['style', 'target', 'uin', 'alias', 'name', 'title', 'value', 'type', 'color', 'dir']

    static ByteArrayOutputStream applyJTidy(String html) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()

        try {
            TIDY.parse(new ByteArrayInputStream(html.getBytes()), baos)
        } catch (Exception e) {
            System.err.println('Couldn\'t tidy: ' + html.getBytes())
            throw e
        }
        return baos
    }

    static Node tidyAndConvertToNode(String html) {

        ByteArrayOutputStream baos = applyJTidy(html)
        Node div
        try {
            div = XML_PARSER.parseText("<div>${baos}</div>")
        } catch (Exception e) {
            log.error('Couldn\'t tidy: ' + html, e)
            div = XML_PARSER.parseText('<div></div>')
        }

        return div
    }

    static Div replaceHtmlWithDita(String html) {

        Node div = tidyAndConvertToNode(html)

        recursivelyRemoveEmptyNodes(div)

        Closure cl = {}
        if(div) { // Guards against the case where we've completely removed the node because there's no sensible content
            div.children().each {childNode ->
                cl = cl >> nodeToDita(childNode)
            }

        }

        Div.build(cl)
    }

    static Closure nodeToDita(String node) {
        return {
            txt node
        }
    }

    static Closure nodeToDita(Node node) {

        if (node.name().toString().toLowerCase() == 'table') {
            return {
                table replaceTableNode(node)
            }
        }

        Closure cl = {}
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
                    ph(updateAttributeMap(node), cl)
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
            case 'u':
                return {
                    u(updateAttributeMap(node), cl)
                }
            case 'sub':
                return {
                    sub(updateAttributeMap(node), cl)
                }
            case 'sup':
                return {
                    sup(updateAttributeMap(node), cl)
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
            case 'ol':
                return {
                    ol(updateAttributeMap(node), cl)
                }
            case 'li':
                return {
                    li(updateAttributeMap(node), cl)
                }
            case 'blockquote':
                return {
                    div(updateAttributeMap(node), cl)
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
        ATTRIBUTE_REMOVALS.each {oldAtt ->
            attributes.remove(oldAtt)
        }

        List<String> keysForRemoval = []
        attributes.keySet().each {key ->
            if (!ALL_ATTRIBUTES.contains(key)) {
                keysForRemoval.add(key)
            }
        }
        keysForRemoval.each {key ->
            attributes.remove(key)
        }
        attributes
    }

    static XRef replaceANode(Node node) {
        String href = node.attributes()['href']
        Closure contentClosure = {}
        node.children().each {aChild ->
            contentClosure = contentClosure >> nodeToDita(aChild)
        }

        if (href.startsWith('http') || href.startsWith('mailto')) {
            return XRef.build([
                scope: Scope.EXTERNAL,
                format: "html",
                href: node.attributes()['href'],
                outputClass: node.attributes()['class']
                ],contentClosure)


        }

        XRef.build([
            scope: Scope.LOCAL,
            keyRef: node.attributes()['href'],
            outputClass: node.attributes()['class']
            ], contentClosure)
    }

    static Table replaceTableNode(Node originalTable) {
        Node thead = originalTable.children().find {it instanceof Node && it.name().equalsIgnoreCase('thead')}
        Node tbody = originalTable.children().find {it instanceof Node && it.name().equalsIgnoreCase('tbody')}

        Node thContainer = thead ?: originalTable
        Node trContainer = tbody ?: originalTable

        List<Node> ths = []
        if(thead) {
            ths.addAll(thContainer.children().findAll {it instanceof Node && it.name().equalsIgnoreCase('tr')})
        }
        List<Node> trs = []
        if(tbody || !thead) {
            trs.addAll(trContainer.children().findAll {it instanceof Node && it.name().equalsIgnoreCase('tr')})
        }

        List<Node> allRows = []
        allRows.addAll(ths)
        allRows.addAll(trs)

        List<Colspec> colSpecs = getColumnSpecifications(allRows.get(0))

        Table table = Table.build {
            tgroup(cols: colSpecs.size()) {
                colSpecs.each {
                    colspec it
                }
                if (ths.size() > 0) {
                    tHead {
                        ths.each {th ->
                            row getRowClosure(th)
                        }
                    }
                }
                if (trs.size() > 0) {
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

        List<Node> tableCells = tr.children().findAll {it instanceof Node && (it.name().equalsIgnoreCase('td') || it.name().equalsIgnoreCase('th'))}
        List<Integer> widths = []

        tableCells.each { Node td ->
            Integer colWidth = null
            Integer colSpan = 1
            if (td.attributes()['width']) {
                colWidth = Integer.parseInt(td.attributes()['width'].toString().replace('%', ''))
            }
            if (td.attributes()['colspan']) {
                colSpan = Integer.parseInt(td.attributes()['colspan'].toString())
            }
            if (colWidth && colWidth != 1 && colSpan > 1) {
                colWidth = (colWidth / colSpan).abs()
            }
            for (int i = 0; i < colSpan; i++) {
                widths.add(colWidth)
            }
        }
        List<Integer> completeWidths = []
        int unsizedColumns = widths.count {it == null}
        if(unsizedColumns > 0) {
            Integer totalSoFar = widths.sum {it == null?0:it}
            Integer calculatedWidth = ((100 - totalSoFar) / unsizedColumns).abs()
            widths.each {
                if(it == null) {
                    completeWidths.add(calculatedWidth)
                } else {
                    completeWidths.add(it)
                }
            }
        } else {
            completeWidths = widths
        }

        int i = 0
        return completeWidths.collect {width ->
            new Colspec(
                colName: "col${i++}",
                colwidth: "${width}*"
            )
        }
    }

    static List<String> allowedEmptyNodes = ["td"]

    static void recursivelyRemoveEmptyNodes(Node node) {

        List<Node> childrenToRecurse = []

        node.children().findAll{it instanceof Node}.each {
            childrenToRecurse.add(it)
        }

        childrenToRecurse.each {
            recursivelyRemoveEmptyNodes(it)
        }

        if(node.children().size() == 0 && node.parent() && node.name().toString().toLowerCase() ) {
            if(!allowedEmptyNodes.contains(node.name().toString().toLowerCase())) {
                node.parent().remove(node)
            }
        }
        if(node.children().size() == 1 &&
           node.children().get(0) instanceof String &&
           ((String) node.children().get(0)).trim().isEmpty()) {
            if(!allowedEmptyNodes.contains(node.name().toString().toLowerCase())) {
                node.parent().remove(node)
            }
        }
    }
}
