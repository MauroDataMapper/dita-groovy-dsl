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

import org.apache.commons.lang3.StringUtils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DitaElementSpecification {


    String elementName
    String ditaName

    String description

    List<String> packagePath = []
    List<String> attributeGroups = []
    List<DitaAttributeSpecification> extraAttributes = []
    String docTypeDecl
    String licenseHeaderText

    Set<String> containedElementNames = []
    Set<DitaElementSpecification> containedElements = []

    boolean allowsText = false

    void writeClassFile(String basePath) {
        StringBuilder stringBuilder = createElementFile()
        String filePath = basePath + '/elements/' + packagePath.join('/') + '/'
        writeFile(filePath, "${elementName}.groovy", stringBuilder)
    }

    void writeClassFileAsString() {
        StringBuilder stringBuilder = createElementFile()
        System.err.println(stringBuilder.toString())
    }

    StringBuilder createElementFile() {
        StringBuilder stringBuilder = new StringBuilder("")
        stringBuilder.append(licenseHeaderText).append('\n')
        String packageName = "uk.ac.ox.softeng.maurodatamapper.dita.elements." + packagePath.join(".")
        stringBuilder.append("package ${packageName}")
        stringBuilder.append('\n\n')

        attributeGroups.each {attributeGroupName ->
            stringBuilder.append("import uk.ac.ox.softeng.maurodatamapper.dita.attributes.${attributeGroupName}AttributeGroup\n")
        }
        stringBuilder.append('import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement\n')
        if(allowsText) {
            stringBuilder.append('import uk.ac.ox.softeng.maurodatamapper.dita.meta.TextElement\n')
        }

        containedElements.each {elementContainment ->
            if(!elementContainment.packagePath.equals(this.packagePath)) {
                stringBuilder.append('import uk.ac.ox.softeng.maurodatamapper.dita.elements.')
                elementContainment.packagePath.each {
                    stringBuilder.append(it.toLowerCase())
                    stringBuilder.append('.')
                }
                stringBuilder.append(elementContainment.elementName)
                stringBuilder.append('\n')
            }
        }

        stringBuilder.append('\n')
        stringBuilder.append('\n\n')

        stringBuilder.append("/* ${description}")
        stringBuilder.append('\n*/\n\n')
        stringBuilder.append("class ${elementName} extends DitaElement")

        if(attributeGroups.size() > 0) {
            stringBuilder.append(' implements ')
            stringBuilder.append(StringUtils.join(attributeGroups.collect { "${it}AttributeGroup"}, ", "))
        }
        stringBuilder.append(' {\n\n')
        if(docTypeDecl) {
            stringBuilder.append("\tString doctypeDecl = \"\"\"${docTypeDecl}\"\"\"\n\n")
        }

        stringBuilder.append('\n')

        stringBuilder.append("\tString ditaNodeName() {\n")
        stringBuilder.append("\t\t'${ditaName}'\n")
        stringBuilder.append('\t}\n')

        stringBuilder.append("\tstatic ${elementName} build(java.util.Map args) {\n")
        stringBuilder.append("\t\tnew ${elementName}(args)\n")
        stringBuilder.append('\t}\n\n')

        stringBuilder.append("\tstatic ${elementName} build(java.util.Map args, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ${elementName}) Closure closure) {\n")
        stringBuilder.append("\t\tnew ${elementName}(args).tap(closure)\n")
        stringBuilder.append('\t}\n\n')

        stringBuilder.append("\tstatic ${elementName} build(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ${elementName}) Closure closure) {\n")
        stringBuilder.append("\t\tnew ${elementName}().tap(closure)\n")
        stringBuilder.append('\t}\n\n')

        extraAttributes.each {extraAttribute ->
            if(extraAttribute.deprecated) {
                stringBuilder.append('\t@Deprecated\n')
            }
            stringBuilder.append("\tString ${extraAttribute.attributeName}\n\n")
        }

        if(allowsText) {

            // Add a no-arg constructor to ensure we keep the original map constructor
            stringBuilder.append("\t${elementName}() {\n")
            stringBuilder.append('\t\tsuper()\n')
            stringBuilder.append('\t}\n\n')


            stringBuilder.append("\t${elementName}(String content) {\n")
            stringBuilder.append('\t\tcontents.add(new TextElement(content))\n')
            stringBuilder.append('\t}\n\n')

            stringBuilder.append('\tvoid _(String content) {\n')
            stringBuilder.append('\t\tcontents.add(new TextElement(content))\n')
            stringBuilder.append('\t}\n\n')

            stringBuilder.append('\tvoid txt(String content) {\n')
            stringBuilder.append('\t\tcontents.add(new TextElement(content))\n')
            stringBuilder.append('\t}\n\n')

            stringBuilder.append('\tvoid str(String content) {\n')
            stringBuilder.append('\t\tcontents.add(new TextElement(content))\n')
            stringBuilder.append('\t}\n\n')

        }

        containedElements.each { containedElement ->
            String containedElementName = containedElement.elementName
            String methodName = getMethodName(containedElement.elementName, elementName)

            stringBuilder.append("\tvoid $methodName($containedElementName new$containedElementName) {\n")
            stringBuilder.append("\t\tcontents.add(new$containedElementName)\n")
            stringBuilder.append('\t}\n\n')

            stringBuilder.append("\tvoid $methodName(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = $containedElementName) Closure closure) {\n")
            stringBuilder.append("\t\tcontents.add(${containedElementName}.build(closure))\n")
            stringBuilder.append('\t}\n\n')

            stringBuilder.append("\tvoid $methodName(java.util.Map args, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = $containedElementName) Closure closure) {\n")
            stringBuilder.append("\t\tcontents.add(${containedElementName}.build(args, closure))\n")
            stringBuilder.append('\t}\n\n')

            stringBuilder.append("\tvoid $methodName(java.util.Map args) {\n")
            stringBuilder.append("\t\tcontents.add(${containedElementName}.build(args))\n")
            stringBuilder.append('\t}\n\n')

            stringBuilder.append("\tList<${containedElementName}> get${containedElementName}${containedElementName.endsWith("s")?"":"s"}() {\n")
            stringBuilder.append("\t\tcontents.findAll{ it instanceof ${containedElementName} }.collect{ (${containedElementName}) it }\n")
            stringBuilder.append('\t}\n\n')


            if(containedElement.allowsText) {
                stringBuilder.append("\tvoid $methodName(String textContent) {\n")
                stringBuilder.append("\t\tcontents.add(new $containedElementName(textContent))\n")
                stringBuilder.append('\t}\n\n')

            }

        }

        stringBuilder.append('\tjava.util.Map attributeMap() {\n')
        stringBuilder.append('\t\tjava.util.Map ret = [:]\n')
        attributeGroups.each { attributeGroupName ->
            stringBuilder.append("\t\tret << ${attributeGroupName}AttributeGroup.super.attributeMap()\n")
        }
        extraAttributes.each {extraAttribute ->
            stringBuilder.append("\t\tret << [\"${extraAttribute.ditaName}\": ${extraAttribute.attributeName}]\n")
        }
        stringBuilder.append('\t\tret\n')
        stringBuilder.append('\t}\n\n')


        stringBuilder.append('}\n')

        return stringBuilder
    }

    String getMethodName(String name, String owner) {
        String methodName = name
        methodName = lowerCaseFirstLetter(methodName)
        if(['abstract', 'boolean'].contains(methodName)) {
            methodName = "_" + methodName
        }
        return methodName
    }

    static String lowerCaseFirstLetter(String input) {
        char[] c = input.toCharArray()
        c[0] = Character.toLowerCase(c[0])
        new String(c)
    }

    static void writeFile(String directory, String filename, StringBuilder stringBuilder) {
        Path directoryPath = Paths.get(directory)
        Files.createDirectories(directoryPath)
        Path path = directoryPath.resolve(filename)
        Files.write(path, stringBuilder.toString().bytes)
    }
}
