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
package uk.ac.ox.softeng.maurodatamapper.dita.meta

import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement

import groovy.xml.MarkupBuilder
import groovy.xml.MarkupBuilderHelper
import org.apache.commons.io.FilenameUtils

trait TopLevelDitaElement extends DitaElement {

    abstract String getFileSuffix()
    abstract String getDoctypeDecl()

    //abstract Map<String, TopLevelDitaElement> subFilesForWriting()

    boolean outputAsFile(String filename, boolean printSubTopics = false) {
        String path = FilenameUtils.getFullPathNoEndSeparator(filename)
        new File(path).mkdirs()
        outputAsFile(new File(filename), printSubTopics)
    }


    abstract def toXml(MarkupBuilder builder, boolean printSubTopics)


    void outputAsFile(File outputFile, boolean printSubTopics = false) {
        System.err.println("Writing file: " + outputFile.name)

        FileWriter fileWriter = new FileWriter(outputFile)
        MarkupBuilder builder = new MarkupBuilder(fileWriter)
        builder.setOmitNullAttributes(true)
        builder.setOmitEmptyAttributes(true)

        def helper = new MarkupBuilderHelper(builder)
        helper.xmlDeclaration([version:'1.0', encoding:'UTF-8', standalone:'no'])
        helper.yieldUnescaped """${getDoctypeDecl()}\n"""
        toXml(builder, printSubTopics)
        fileWriter.close()
        File directory = outputFile.getParentFile()
/*        if(subFilesForWriting()) {
            subFilesForWriting().each {entry ->
                String filename = directory.getPath() + "/" + entry.key
                entry.value.outputAsFile(new File(filename))
            }
        }

 */
    }

    String outputAsString(boolean printSubTopics = false) {
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
