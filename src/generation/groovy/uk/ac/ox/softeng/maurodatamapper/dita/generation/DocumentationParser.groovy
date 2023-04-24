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
package uk.ac.ox.softeng.maurodatamapper.dita.generation

import groovy.util.logging.Slf4j

import java.time.LocalDate
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@Slf4j
class DocumentationParser {

    static final String MDM_LICENSE_GIST = 'https://gist.githubusercontent.com/jamesrwelch/680f9016e882a7a4317844580fd6a1fa/raw/2d94405979886d7ef40f08ad29f820704d3e2ad9/NOTICE.tmpl'
    static final String BASE_PACKAGE_DIR = 'uk/ac/ox/softeng/maurodatamapper/dita'
    static final String BASE_URL = 'https://docs.oasis-open.org/dita/dita/v1.3/errata02/os/complete/part3-all-inclusive/contentmodels/'

    static final Map<String, String> ATTRIBUTE_GROUP_MAP = [
        'Universal attribute group'             : 'Universal',
        'outputclass'                           : 'OutputClass',
        'keyref'                                : 'KeyRef',
        'keys'                                  : 'Keys',
        'Link relationship attribute group'     : 'LinkRelationship',
        'Attributes common to many map elements': 'CommonMapElements',
        'Architectural attribute group'         : 'Architectural',
        'Topicref element attributes group'     : 'TopicRefElement',
        'Complex-table attribute group'         : 'ComplexTable',
        'Data element attributes group'         : 'DataElement',
        'Date attributes group'                 : 'Date',
        'Display attribute group'               : 'Display',
        'Simpletable attribute group'           : 'SimpleTable',
        'Specialization attributes group'       : 'Specialization',
    ]

    static final List<String> REPLACEMENTS = [
        'Abstract',
        'Apply',
        'Area',
        'Body',
        'ChangeHistory',
        'Def',
        'Details',
        'Event',
        'EventType',
        'Head',
        'Id',
        'Information',
        'Key',
        'List',
        'Matter',
        'Meta',
        'Name',
        'Ref',
        'Set',
        'Subject',
    ]

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

    final String licenseHeader
    final Map<String, DitaElementSpecification> elementMap
    final ExecutorService executorService

    DocumentationParser() {
        licenseHeader = loadLicenseHeaderText()
        executorService = Executors.newFixedThreadPool(Runtime.runtime.availableProcessors().intdiv(2) ?: 1)
        elementMap = [:]
    }

    String loadLicenseHeaderText() {
        List<String> lines = "$MDM_LICENSE_GIST".toURL().readLines()
        StringBuilder sb = new StringBuilder('/*\n')
        lines.each {line ->
            if (line) sb.append(' * ').append(line).append('\n')
            else sb.append(' *\n')
        }
        sb.append(' */')
        sb.toString().replaceFirst(/\$\{year}/, LocalDate.now().year.toString())
    }

    void generateMapFromDocumentation() {
        log.debug('Loading documentation from website')

        List<String> chars = ('a'..'z')
        List<SpecificationParser> parsers = chars.collect {letter ->
            new SpecificationParser(licenseHeader, letter)
        }

        // Send all spec parsers to the executor service
        List<Future<Map<String, DitaElementSpecification>>> futures = executorService.invokeAll(parsers)

        // Wait for all the specifications to be loaded
        while (futures.any {!it.isDone()}) {sleep(10)}

        // Load all the specs into the element map
        futures.each {elementMap.putAll(it.get())}
    }

    void linkElementSpecifications() {
        log.debug('Linking Element Specifications')
        elementMap.each {name, spec ->
            spec.containedElementNames.each {
                if (it == 'textdata') {
                    spec.allowsText = true
                } else {
                    String containedElementName = it.replaceAll('[<|>]', '')
                    DitaElementSpecification containedElement = elementMap[containedElementName]
                    if (containedElement) {
                        spec.containedElements.add(containedElement)
                    } else {
                        log.warn("Cannot find contained element: ${containedElementName} (${it})")
                    }
                }
            }
        }
    }

    void writeSpecificationFilesToDirectory(String directoryPath) {
        log.debug('Writing specification files')
        elementMap.each {name, spec ->
            log.trace('Writing {}', name)
            spec.writeClassFile(directoryPath + '/' + BASE_PACKAGE_DIR)
        }
    }

    void shutdownAndAwaitTermination() {
        executorService.shutdown()
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow()
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                    log.warn('Pool did not terminate')
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    @SuppressWarnings(['SystemErrPrint', 'SystemExit'])
    static void main(String[] args) {
        if (!args) {
            System.err.println('No arguments provided!')
            System.err.println('Please provide the path of the output directory as the first argument!')
            System.exit(1)
        }
        DocumentationParser documentationParser = new DocumentationParser()
        log.debug('Generating library')

        documentationParser.generateMapFromDocumentation()
        documentationParser.linkElementSpecifications()
        documentationParser.writeSpecificationFilesToDirectory(args[0])
        documentationParser.shutdownAndAwaitTermination()
        log.debug('Library generated')
    }
}
