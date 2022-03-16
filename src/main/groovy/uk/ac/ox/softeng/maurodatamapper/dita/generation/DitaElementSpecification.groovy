package uk.ac.ox.softeng.maurodatamapper.dita.generation

class DitaElementSpecification {

    String elementName
    String ditaName

    String description

    List<String> packagePath = []
    List<String> attributeGroups = []
    boolean isTopLevel = false
    String docTypeDecl
    String fileSuffix

    boolean allowsText = false
    List<ElementContainment> contains = []

    void writeClassFile(String basePath) {
        StringBuffer stringBuffer = createElementFile()
        String filePath = basePath + "/elements/" + packagePath.join("/") + "/"

        DslGenerator.writeFile(filePath + elementName + ".groovy", stringBuffer)
    }

    void writeClassFileAsString() {
        StringBuffer stringBuffer = createElementFile()
        System.err.println(stringBuffer.toString())
    }


    StringBuffer createElementFile() {
        StringBuffer stringBuffer = new StringBuffer("")
        String packageName = "uk.ac.ox.softeng.maurodatamapper.dita.elements." + packagePath.join(".")
        stringBuffer.append("package ${packageName}")
        stringBuffer.append("\n\n")

        attributeGroups.each { attributeGroupName ->
            stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.attributes.${attributeGroupName}AttributeGroup\n")
        }
        if(isTopLevel) {
            stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.meta.TopLevelDitaElement\n")
        } else {
            stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.meta.DitaElement\n")
        }
        contains.each {elementContainment ->
            stringBuffer.append("import uk.ac.ox.softeng.maurodatamapper.dita.elements.")
            elementContainment.containedElement.packagePath.each {
                stringBuffer.append(it.toLowerCase())
                stringBuffer.append(".")
            }
            stringBuffer.append(elementContainment.containedElementName())
            stringBuffer.append("\n")
        }
        stringBuffer.append("\n")
        stringBuffer.append("import groovy.xml.MarkupBuilder")

        stringBuffer.append("\n\n")

        stringBuffer.append("/* " + description)
        stringBuffer.append("\n*/\n\n")

        stringBuffer.append("class ${elementName} implements ")
        if(isTopLevel) {
            stringBuffer.append("TopLevelDitaElement")
        } else {
            stringBuffer.append("DitaElement")
        }
        attributeGroups.each { attributeGroupName ->
            stringBuffer.append(", ${attributeGroupName}AttributeGroup")
        }
        stringBuffer.append(" {\n\n")
        if(docTypeDecl) {
            stringBuffer.append("\tString doctypeDecl = \"\"\"${docTypeDecl}\"\"\"\n\n")
        }

        if(allowsText) {
            stringBuffer.append("\tString textContent = \"\"\n\n")
        }

        contains.findAll { it.containedElement }.each { containElement ->
            String type = containElement.containedElementName()
            String name = containElement.containedElementVariableName()
            if(containElement.allowMany) {
                stringBuffer.append("\tList<${type}> ${name} = []\n")
            } else {
                stringBuffer.append("\t${type} ${name}\n")
            }
        }
        stringBuffer.append("\n")

        stringBuffer.append("\tstatic ${elementName} build(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ${elementName}) Closure closure) {\n")
        stringBuffer.append("\t\tnew ${elementName}().tap(closure)\n")
        stringBuffer.append("\t}\n\n")

        if(allowsText) {
            stringBuffer.append("\tstatic ${elementName} build(String textContent, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ${elementName}) Closure closure) {\n")
            stringBuffer.append("\t\tnew ${elementName}(textContent).tap(closure)\n")
            stringBuffer.append("\t}\n\n")

            stringBuffer.append("\t${elementName}(String textContent) {\n")
            stringBuffer.append("\t\tthis.textContent = textContent")
            stringBuffer.append("\t}\n\n")
        }

        stringBuffer.append("\t@Override\n")
        stringBuffer.append("\tdef toXml(MarkupBuilder builder) {\n")
        stringBuffer.append("\t\tbuilder.\"${ditaName}\" (attributeMap()) {\n")
        if(allowsText) {
            stringBuffer.append("\t\t\tmkp.yield(textContent)\n")
        }
        contains.findAll { it.containedElement}.each { containElement ->
            stringBuffer.append("\t\t\tif(${containElement.containedElementVariableName()}) {\n")
            if(containElement.allowMany) {
                stringBuffer.append("\t\t\t\t${containElement.containedElementVariableName()}.each {${containElement.containedElementName()} ->\n")
                stringBuffer.append("\t\t\t\t\t${containElement.containedElementName()}.toXml(builder)\n")
                stringBuffer.append("\t\t\t\t}\n")

            } else {
                stringBuffer.append("\t\t\t\t${containElement.containedElementVariableName()}.toXml(builder)\n")
            }

            stringBuffer.append("\t\t\t}\n")
        }
        stringBuffer.append("\t\t}\n\n")
        stringBuffer.append("\t}\n\n")

        stringBuffer.append("\tjava.util.Map attributeMap() {\n")
        stringBuffer.append("\t\tjava.util.Map ret = [:]\n")
        attributeGroups.each { attributeGroupName ->
            stringBuffer.append("\t\tret << ${attributeGroupName}AttributeGroup.super.attributeMap()\n")
        }
        stringBuffer.append("\t\treturn ret\n")
        stringBuffer.append("\t}\n\n")


        contains.findAll { it.allowMany }.each { containElement ->
            if(containElement.containedElement != this) {
                stringBuffer.append("\tvoid ${containElement.containedElementVariableName()}(${containElement.containedElementName()} ${containElement.containedElementVariableName()}) {\n")
                stringBuffer.append("\t\tthis.${containElement.containedElementVariableName()}.add(${containElement.containedElementVariableName()})\n")
                stringBuffer.append("\t}\n")
                stringBuffer.append("\n\n")
//                stringBuffer.append("\tvoid ${containElement.containedElementVariableName()}(${containElement.containedElementName()}... ${containElement.containedElementVariableName()}) {\n")
//                stringBuffer.append("\t\tthis.${containElement.containedElementVariableName()}.addAll(${containElement.containedElementVariableName()})\n")
//                stringBuffer.append("\t}\n")
//                stringBuffer.append("\n\n")
                if (containElement.containedElement.allowsText) {
                    stringBuffer.append("\tvoid ${containElement.containedElementVariableName()}(List<String> ${containElement.containedElementVariableName()}) {\n")
                    stringBuffer.append("\t\tthis.${containElement.containedElementVariableName()}.addAll(${containElement.containedElementVariableName()}.collect{new ${containElement.containedElementName()}(it)} )\n")
                    stringBuffer.append("\t}\n")
                    stringBuffer.append("\n\n")
                    stringBuffer.append("\tvoid ${containElement.containedElementVariableName()}(String ${containElement.containedElementVariableName()}) {\n")
                    stringBuffer.append("\t\tthis.${containElement.containedElementVariableName()}.add(new ${containElement.containedElementName()}(${containElement.containedElementVariableName()}) )\n")
                    stringBuffer.append("\t}\n")
                    stringBuffer.append("\n\n")
//                    stringBuffer.append("\tvoid ${containElement.containedElementVariableName()}(String... ${containElement.containedElementVariableName()}) {\n")
//                    stringBuffer.append("\t\tthis.${containElement.containedElementVariableName()}.addAll(${containElement.containedElementVariableName()}.collect{new ${containElement.containedElementName()}(it) {}})\n")
//                    stringBuffer.append("\t}\n")
//                    stringBuffer.append("\n\n")
                } else {
                    stringBuffer.append("\tvoid ${containElement.containedElementVariableName()}(List<${containElement.containedElementName()}> ${containElement.containedElementVariableName()}) {\n")
                    stringBuffer.append("\t\tthis.${containElement.containedElementVariableName()}.addAll(${containElement.containedElementVariableName()})\n")
                    stringBuffer.append("\t}\n")
                    stringBuffer.append("\n\n")
                }
            }

        }
        contains.findAll {!it.allowMany }.each { containElement ->
            if (containElement.containedElement != this) {
                stringBuffer.append("\tvoid ${containElement.containedElementVariableName()}(${containElement.containedElementName()} ${containElement.containedElementVariableName()}) {\n")
                stringBuffer.append("\t\tthis.${containElement.containedElementVariableName()} = ${containElement.containedElementVariableName()}\n")
                stringBuffer.append("\t}\n")
                stringBuffer.append("\n\n")
                if (containElement.containedElement.allowsText) {
                    stringBuffer.append("\tvoid ${containElement.containedElementVariableName()}(String ${containElement.containedElementVariableName()}) {\n")
                    stringBuffer.append("\t\tthis.${containElement.containedElementVariableName()} = new ${containElement.containedElementName()}(${containElement.containedElementVariableName()})\n")
                    stringBuffer.append("\t}\n")
                    stringBuffer.append("\n\n")
                }
            }
        }
        if(isTopLevel && fileSuffix) {
            stringBuffer.append("\tString getFileSuffix() {\n")
            stringBuffer.append("\t\t\".dita\"\n")
            stringBuffer.append("\t}\n\n")
        }

        stringBuffer.append("}\n")

        return stringBuffer
    }


}
