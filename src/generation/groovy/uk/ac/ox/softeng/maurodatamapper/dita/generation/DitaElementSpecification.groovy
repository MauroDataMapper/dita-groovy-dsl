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
import org.apache.commons.lang3.StringUtils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Slf4j
class DitaElementSpecification {

    public static final String INDENT = '    '

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
        StringBuilder stringBuilder = constructElementFile()
        String filePath = basePath + '/elements/' + packagePath.join('/') + '/'
        writeFile(filePath, "${elementName}.groovy", stringBuilder)
    }

    void writeClassFileAsString() {
        StringBuilder stringBuilder = constructElementFile()
        log.info(stringBuilder.toString())
    }

    String constructJavadocDescription() {
        StringBuilder sb = new StringBuilder('/**\n')
        description.eachLine {line ->
            String trimmed = line.trim()
            if (trimmed) sb.append(' * ').append(trimmed).append('\n')
            else sb.append(' *\n')
        }
        sb.append('**/\n').toString()
    }

    StringBuilder constructElementFile() {
        StringBuilder stringBuilder = new StringBuilder()
            .append(licenseHeaderText).append('\n')
            .append('package uk.ac.ox.softeng.maurodatamapper.dita.elements.')
            .append(packagePath.join('.'))
            .append('\n\n')
            .append(constructImports())
            .append('\n')
            .append(constructClassDeclaration())

        if (docTypeDecl) {
            stringBuilder.append("${INDENT}String doctypeDecl = \"\"\"${docTypeDecl}\"\"\"\n\n")
        }

        stringBuilder.append("${INDENT}String ditaNodeName() {\n")
            .append("${INDENT}${INDENT}'${ditaName}'\n")
            .append("${INDENT}}\n")
            .append(constructBuildMethods())
            .append(constructExtraAttributesMethods())

        if (allowsText) stringBuilder.append(constructAllowsTextMethods())

        stringBuilder
            .append(constructContainedElementsMethods())
            .append(constructAttributeMapMethod())
            .append('}\n')
    }

    String constructImports() {
        StringBuilder stringBuilder = new StringBuilder()
        attributeGroups.each {attributeGroupName ->
            stringBuilder.append("import uk.ac.ox.softeng.maurodatamapper.dita.attributes.${attributeGroupName}AttributeGroup\n")
        }
        stringBuilder.append('import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement\n')
        if (allowsText) {
            stringBuilder.append('import uk.ac.ox.softeng.maurodatamapper.dita.meta.TextElement\n')
        }

        containedElements.each {elementContainment ->
            if (elementContainment.packagePath != this.packagePath) {
                stringBuilder.append('import uk.ac.ox.softeng.maurodatamapper.dita.elements.')
                elementContainment.packagePath.each {
                    stringBuilder
                        .append(it.toLowerCase())
                        .append('.')
                }
                stringBuilder
                    .append(elementContainment.elementName)
                    .append('\n')
            }
        }
        stringBuilder.toString()
    }

    String constructClassDeclaration() {
        StringBuilder stringBuilder = new StringBuilder()
            .append(constructJavadocDescription())
            .append("class ${elementName} extends DitaElement")

        if (attributeGroups.size() > 0) {
            stringBuilder
                .append(' implements ')
                .append(StringUtils.join(attributeGroups.collect {"${it}AttributeGroup"}, ', '))
        }
        stringBuilder
            .append(' {\n\n')
            .toString()
    }

    String constructBuildMethods() {
        new StringBuilder()
            .append("${INDENT}static ${elementName} build(Map args) {\n")
            .append("${INDENT}${INDENT}new ${elementName}(args)\n")
            .append("${INDENT}}\n\n")

            .append("${INDENT}static ${elementName} build(Map args, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ${elementName}) Closure closure) {\n")
            .append("${INDENT}${INDENT}new ${elementName}(args).tap(closure)\n")
            .append("${INDENT}}\n\n")

            .append("${INDENT}static ${elementName} build(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ${elementName}) Closure closure) {\n")
            .append("${INDENT}${INDENT}new ${elementName}().tap(closure)\n")
            .append("${INDENT}}\n\n")
            .toString()
    }

    String constructExtraAttributesMethods() {
        StringBuilder stringBuilder = new StringBuilder()
        extraAttributes.each {extraAttribute ->
            if (extraAttribute.deprecated) {
                stringBuilder.append("${INDENT}@Deprecated\n")
            }
            stringBuilder.append("${INDENT}String ${extraAttribute.attributeName}\n\n")
        }
        stringBuilder.toString()
    }

    String constructAllowsTextMethods() {
        // Add a no-arg constructor to ensure we keep the original map constructor
        new StringBuilder()
            .append("${INDENT}${elementName}() {\n")
            .append("${INDENT}${INDENT}super()\n")
            .append("${INDENT}}\n\n")

            .append("${INDENT}${elementName}(String content) {\n")
            .append("${INDENT}${INDENT}contents.add(new TextElement(content))\n")
            .append("${INDENT}}\n\n")

            .append("${INDENT}void _(String content) {\n")
            .append("${INDENT}${INDENT}contents.add(new TextElement(content))\n")
            .append("${INDENT}}\n\n")

            .append("${INDENT}void txt(String content) {\n")
            .append("${INDENT}${INDENT}contents.add(new TextElement(content))\n")
            .append("${INDENT}}\n\n")

            .append("${INDENT}void str(String content) {\n")
            .append("${INDENT}${INDENT}contents.add(new TextElement(content))\n")
            .append("${INDENT}}\n\n")
            .toString()
    }

    String constructContainedElementsMethods() {
        StringBuilder stringBuilder = new StringBuilder()
        containedElements.each {containedElement ->
            String containedElementName = containedElement.elementName
            String methodName = getMethodName(containedElement.elementName, elementName)

            stringBuilder
                .append("${INDENT}void $methodName($containedElementName new$containedElementName) {\n")
                .append("${INDENT}${INDENT}contents.add(new$containedElementName)\n")
                .append("${INDENT}}\n\n")

                .append("${INDENT}void $methodName(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = $containedElementName) Closure closure) {\n")
                .append("${INDENT}${INDENT}contents.add(${containedElementName}.build(closure))\n")
                .append("${INDENT}}\n\n")

                .append("${INDENT}void $methodName(Map args, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = $containedElementName) Closure closure) {\n")
                .append("${INDENT}${INDENT}contents.add(${containedElementName}.build(args, closure))\n")
                .append("${INDENT}}\n\n")

                .append("${INDENT}void $methodName(Map args) {\n")
                .append("${INDENT}${INDENT}contents.add(${containedElementName}.build(args))\n")
                .append("${INDENT}}\n\n")

                .append("${INDENT}List<${containedElementName}> get${containedElementName}${containedElementName.endsWith('s') ? '' : 's'}() {\n")
                .append("${INDENT}${INDENT}contents.findAll{ it instanceof ${containedElementName} }.collect{ (${containedElementName}) it }\n")
                .append("${INDENT}}\n\n")

            if (containedElement.allowsText) {
                stringBuilder
                    .append("${INDENT}void $methodName(String textContent) {\n")
                    .append("${INDENT}${INDENT}contents.add(new $containedElementName(textContent))\n")
                    .append("${INDENT}}\n\n")
            }
        }
        stringBuilder.toString()
    }

    String constructAttributeMapMethod() {
        StringBuilder stringBuilder = new StringBuilder()
            .append("${INDENT}@Override\n")
            .append("${INDENT}Map attributeMap() {\n")
            .append("${INDENT}${INDENT}Map ret = [:]\n")
        attributeGroups.each {attributeGroupName ->
            stringBuilder.append("${INDENT}${INDENT}ret << ${attributeGroupName}AttributeGroup.super.attributeMap()\n")
        }
        extraAttributes.each {extraAttribute ->
            stringBuilder.append("${INDENT}${INDENT}ret << ['${extraAttribute.ditaName}': ${extraAttribute.attributeName}]\n")
        }
        stringBuilder
            .append("${INDENT}${INDENT}ret\n")
            .append("${INDENT}}\n\n")
            .toString()
    }

    String getMethodName(String name, String owner) {
        String methodName = name
        methodName = lowerCaseFirstLetter(methodName)
        if (['abstract', 'boolean'].contains(methodName)) {
            methodName = '_' + methodName
        }
        methodName
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
