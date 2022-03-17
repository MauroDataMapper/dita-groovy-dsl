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

import groovy.json.JsonSlurper
import groovy.xml.MarkupBuilder
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.ArchitecturalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.OutputClassAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.attributes.UniversalAttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.enums.DitaEnum
import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList
import uk.ac.ox.softeng.maurodatamapper.dita.meta.TopLevelDitaElement

class DslGenerator {

    static JsonSlurper slurper = new JsonSlurper()
    static String LANGUAGE_DEFINITION_FILENAME = "/Users/james/git/mauro/plugins/dita-groovy-dsl/src/main/resources/language/ditaLanguageDefinition.json"

    static String BASE_PACKAGE_DIR = "/Users/james/git/mauro/plugins/dita-groovy-dsl/src/main/groovy/uk/ac/ox/softeng/maurodatamapper/dita"

    static void main(String[] args) {

        def languageDefinition = slurper.parse(new File(LANGUAGE_DEFINITION_FILENAME))

        languageDefinition.attributeGroups.each { attributeGroup ->
            StringBuffer stringBuffer = createAttributeGroupClassFile(attributeGroup)
            writeFile(BASE_PACKAGE_DIR + "/attributes/" + attributeGroup.className + "AttributeGroup.groovy", stringBuffer)
        }

        languageDefinition.attributeGroups.each { attributeGroup ->
            attributeGroup.attributes.
                    findAll { attribute -> attribute.values.size() > 0 }.
                    each { attribute ->
                        StringBuffer stringBuffer = createEnumerationFile(attribute)
                        writeFile(BASE_PACKAGE_DIR + "/enums/" + attribute.type + ".groovy", stringBuffer)

            }

        }


    }

    static void writeFile(String filename, StringBuffer stringBuffer) {
        File file = new File(filename)
        file.getParentFile().mkdirs()
        file.createNewFile()
        FileOutputStream outputStream = new FileOutputStream(file)
        byte[] strToBytes = stringBuffer.toString().getBytes()
        outputStream.write(strToBytes)
        outputStream.close()
    }

    static StringBuffer createAttributeGroupClassFile(def attributeGroup) {

        String className = attributeGroup.className + "AttributeGroup"

        StringBuffer stringBuffer = new StringBuffer("")

        stringBuffer.append("package uk.ac.ox.softeng.maurodatamapper.dita.attributes\n")
        stringBuffer.append("\n")
        stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup\n")
        attributeGroup.attributes.findAll { attribute -> attribute.values.size() > 0 }.each { attribute ->
            stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.enums.${attribute.type}\n")
        }
        stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList\n")
        stringBuffer.append("\n")
        stringBuffer.append("trait ${className} implements AttributeGroup {\n")
        stringBuffer.append("\n")

        attributeGroup.attributes.each { attribute ->
            String initialValue = ""
            if(attribute.type == "SpaceSeparatedStringList") {
                initialValue = " = []"
            }
            stringBuffer.append("\t${attribute.type} ${attribute.javaName}${initialValue}\n")
        }
        stringBuffer.append("\n")
        stringBuffer.append("\tMap attributeMap() {\n")
        stringBuffer.append("\t\treturn [\n")
        attributeGroup.attributes.each { attribute ->
            String ditaName = attribute.ditaName?:attribute.javaName
            stringBuffer.append("\t\t\t\"${ditaName}\": ${attribute.javaName},\n")
        }
        stringBuffer.append("\t\t]\n")
        stringBuffer.append("\t}\n")

        attributeGroup.attributes.each { attribute ->
            stringBuffer.append("\tvoid ${attribute.javaName}(${attribute.type} ${attribute.javaName}) {\n")
            stringBuffer.append("\t\tthis.${attribute.javaName} = ${attribute.javaName}\n")
            stringBuffer.append("\t}\n\n")

            if(attribute.type == "SpaceSeparatedStringList") {
                stringBuffer.append("\tvoid ${attribute.javaName}(Collection<String> ${attribute.javaName}) {\n")
                stringBuffer.append("\t\tthis.${attribute.javaName} = new SpaceSeparatedStringList(${attribute.javaName})\n")
                stringBuffer.append("\t}\n\n")

                stringBuffer.append("\tvoid ${attribute.javaName}(String ${attribute.javaName}) {\n")
                stringBuffer.append("\t\tthis.${attribute.javaName} = new SpaceSeparatedStringList(${attribute.javaName}.split(\" \") as List)\n")
                stringBuffer.append("\t}\n\n")
            }

        }


        stringBuffer.append("\t@Override\n")
        stringBuffer.append("\tList<String> validate() {\n")
        stringBuffer.append("\t\treturn []\n")
        stringBuffer.append("\t}\n")

        stringBuffer.append("}\n")
        return stringBuffer
    }

    static StringBuffer createEnumerationFile(def attribute) {
        StringBuffer stringBuffer = new StringBuffer("")
        stringBuffer.append("package uk.ac.ox.softeng.maurodatamapper.dita.enums")
        stringBuffer.append("\n\n")
        stringBuffer.append("enum ${attribute.type} implements DitaEnum {\n\n")
        attribute.values.each { value ->
            stringBuffer.append("\t${value.javaName} (\"${value.ditaName}\")")
            if (value != attribute.values.last()) {
                stringBuffer.append(",")
            }
            stringBuffer.append("\n")
        }
        stringBuffer.append("\n\n")
        stringBuffer.append("\t${attribute.type} (String stringValue) {\n")
        stringBuffer.append("\t\tthis.stringValue = stringValue\n")
        stringBuffer.append("\t}\n\n")
        stringBuffer.append("}\n")

        return stringBuffer
    }


}
