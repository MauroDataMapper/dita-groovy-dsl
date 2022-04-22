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

import uk.ac.ox.softeng.ebnf.parser.EbnfParser

import org.apache.commons.lang3.StringUtils

class DitaElementSpecification {

    static final String LICENSE_HEADER_TEXT = "/*\n" +
                                              " * Copyright 2020-2022 University of Oxford and Health and Social Care Information Centre, also " +
                                              "known as NHS Digital\n" +
                                              " *\n" +
                                              " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                                              " * you may not use this file except in compliance with the License.\n" +
                                              " * You may obtain a copy of the License at\n" +
                                              " *\n" +
                                              " *     http://www.apache.org/licenses/LICENSE-2.0\n" +
                                              " *\n" +
                                              " * Unless required by applicable law or agreed to in writing, software\n" +
                                              " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                                              " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                                              " * See the License for the specific language governing permissions and\n" +
                                              " * limitations under the License.\n" +
                                              " *\n" +
                                              " * SPDX-License-Identifier: Apache-2.0\n" +
                                              " */"

    String elementName
    String ditaName

    String description

    List<String> packagePath = []
    List<String> attributeGroups = []
    List<DitaAttributeSpecification> extraAttributes = []
    String docTypeDecl
    String fileSuffix



    EbnfParser.ExpressionContext parsePattern

    Set<String> containedElementNames = []
    Set<DitaElementSpecification> containedElements = []

    boolean allowsText = false

    void writeClassFile(String basePath) {
        StringBuffer stringBuffer = createElementFile()
        String filePath = basePath + "/elements/" + packagePath.join("/") + "/"

        writeFile(filePath + elementName + ".groovy", stringBuffer)
    }

    void writeClassFileAsString() {
        StringBuffer stringBuffer = createElementFile()
        System.err.println(stringBuffer.toString())
    }


    StringBuffer createElementFile() {
        StringBuffer stringBuffer = new StringBuffer("")
        stringBuffer.append(LICENSE_HEADER_TEXT)
        String packageName = "uk.ac.ox.softeng.maurodatamapper.dita.elements." + packagePath.join(".")
        stringBuffer.append("package ${packageName}")
        stringBuffer.append("\n\n")

        attributeGroups.each { attributeGroupName ->
            stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.attributes.${attributeGroupName}AttributeGroup\n")
        }
        stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement\n")
        if(allowsText) {
            stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.meta.TextElement\n")
        }

        containedElements.each {elementContainment ->
            if(!elementContainment.packagePath.equals(this.packagePath)) {
                stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.elements.")
                elementContainment.packagePath.each {
                    stringBuffer.append(it.toLowerCase())
                    stringBuffer.append(".")
                }
                stringBuffer.append(elementContainment.elementName)
                stringBuffer.append("\n")
            }
        }

        stringBuffer.append("\n")
        stringBuffer.append("\n\n")

        stringBuffer.append("/* " + description)
        stringBuffer.append("\n*/\n\n")
        stringBuffer.append("class ${elementName} extends DitaElement")

        if(attributeGroups.size() > 0) {
            stringBuffer.append(" implements ")
            stringBuffer.append(StringUtils.join(attributeGroups.collect { "${it}AttributeGroup"}, ", "))
        }
        stringBuffer.append(" {\n\n")
        if(docTypeDecl) {
            stringBuffer.append("\tString doctypeDecl = \"\"\"${docTypeDecl}\"\"\"\n\n")
        }

        stringBuffer.append("\n")

        stringBuffer.append("\tString ditaNodeName() {\n")
        stringBuffer.append("\t\treturn \"${ditaName}\"\n")
        stringBuffer.append("\t}\n")

        stringBuffer.append("\tstatic ${elementName} build(java.util.Map args) {\n")
        stringBuffer.append("\t\tnew ${elementName}(args)\n")
        stringBuffer.append("\t}\n\n")

        stringBuffer.append("\tstatic ${elementName} build(java.util.Map args, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ${elementName}) Closure closure) {\n")
        stringBuffer.append("\t\tnew ${elementName}(args).tap(closure)\n")
        stringBuffer.append("\t}\n\n")

        stringBuffer.append("\tstatic ${elementName} build(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ${elementName}) Closure closure) {\n")
        stringBuffer.append("\t\tnew ${elementName}().tap(closure)\n")
        stringBuffer.append("\t}\n\n")

        extraAttributes.each {extraAttribute ->
            stringBuffer.append("\tString ${extraAttribute.attributeName}\n\n")
        }

        if(allowsText) {

            // Add a no-arg constructor to ensure we keep the original map constructor
            stringBuffer.append("\t${elementName}() {\n")
            stringBuffer.append("\t\tsuper()\n")
            stringBuffer.append("\t}\n\n")


            stringBuffer.append("\t${elementName}(String content) {\n")
            stringBuffer.append("\t\tcontents.add(new TextElement(content))\n")
            stringBuffer.append("\t}\n\n")

            stringBuffer.append("\tvoid _(String content) {\n")
            stringBuffer.append("\t\tcontents.add(new TextElement(content))\n")
            stringBuffer.append("\t}\n\n")

            stringBuffer.append("\tvoid txt(String content) {\n")
            stringBuffer.append("\t\tcontents.add(new TextElement(content))\n")
            stringBuffer.append("\t}\n\n")

            stringBuffer.append("\tvoid str(String content) {\n")
            stringBuffer.append("\t\tcontents.add(new TextElement(content))\n")
            stringBuffer.append("\t}\n\n")

        }

        containedElements.each { containedElement ->
            String containedElementName = containedElement.elementName
            String methodName = getMethodName(containedElement.elementName, elementName)

            stringBuffer.append("\tvoid $methodName($containedElementName new$containedElementName) {\n")
            stringBuffer.append("\t\tcontents.add(new$containedElementName)\n")
            stringBuffer.append("\t}\n\n")

            stringBuffer.append("\tvoid $methodName(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = $containedElementName) Closure closure) {\n")
            stringBuffer.append("\t\tcontents.add(${containedElementName}.build(closure))\n")
            stringBuffer.append("\t}\n\n")

            stringBuffer.append("\tvoid $methodName(java.util.Map args, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = $containedElementName) Closure closure) {\n")
            stringBuffer.append("\t\tcontents.add(${containedElementName}.build(args, closure))\n")
            stringBuffer.append("\t}\n\n")

            stringBuffer.append("\tvoid $methodName(java.util.Map args) {\n")
            stringBuffer.append("\t\tcontents.add(${containedElementName}.build(args))\n")
            stringBuffer.append("\t}\n\n")

            if(containedElement.allowsText) {
                stringBuffer.append("\tvoid $methodName(String textContent) {\n")
                stringBuffer.append("\t\tcontents.add(new $containedElementName(textContent))\n")
                stringBuffer.append("\t}\n\n")

            }

        }
/*
        stringBuffer.append("\t@Override\n")
        stringBuffer.append("\tdef toXml(MarkupBuilder builder) {\n")
        stringBuffer.append("\t\tbuilder.\"${ditaName}\" (attributeMap()) {\n")

        stringBuffer.append("\t\t}\n\n")
        stringBuffer.append("\t}\n\n")
*/
        stringBuffer.append("\tjava.util.Map attributeMap() {\n")
        stringBuffer.append("\t\tjava.util.Map ret = [:]\n")
        attributeGroups.each { attributeGroupName ->
            stringBuffer.append("\t\tret << ${attributeGroupName}AttributeGroup.super.attributeMap()\n")
        }
        extraAttributes.each {extraAttribute ->
            stringBuffer.append("\t\tret << [\"${extraAttribute.ditaName}\": ${extraAttribute.attributeName}]\n")
        }
        stringBuffer.append("\t\treturn ret\n")
        stringBuffer.append("\t}\n\n")


        stringBuffer.append("}\n")

        return stringBuffer
    }

    String getMethodName(String name, String owner) {
        String methodName = name
        methodName = lowerCaseFirstLetter(methodName)
        if(["abstract", "boolean"].contains(methodName)) {
            methodName = "_" + methodName
        }
        return methodName
    }

    static String lowerCaseFirstLetter(String input) {
        char[] c = input.toCharArray()
        c[0] = Character.toLowerCase(c[0])
        new String(c)
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


}
