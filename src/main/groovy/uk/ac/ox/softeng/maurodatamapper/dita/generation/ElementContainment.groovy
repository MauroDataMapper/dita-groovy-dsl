package uk.ac.ox.softeng.maurodatamapper.dita.generation

class ElementContainment {

    DitaElementSpecification containedBy
    DitaElementSpecification containedElement
    String containedElementString
    boolean allowMany
    boolean mustOccur

    String containedElementName() {
        return containedElement.elementName
    }

    String containedElementVariableName() {
        String containedElementVariableName = containedElementName()
        containedElementVariableName = lowerCaseFirstLetter(containedElementVariableName)
        if(["abstract"].contains(containedElementVariableName)) {
            containedElementVariableName = containedBy.elementName + containedElementVariableName
        }
        if(allowMany) {
            return containedElementVariableName + "s"
        } else {
            return containedElementVariableName
        }
    }

    static String lowerCaseFirstLetter(String input) {
        char[] c = input.toCharArray()
        c[0] = Character.toLowerCase(c[0])
        new String(c)
    }


}
