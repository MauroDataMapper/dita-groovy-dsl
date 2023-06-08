package uk.ac.ox.softeng.maurodatamapper.dita.helpers

class IdHelper {

    static final String INVALID_CHAR_REGEX = "[^A-Za-z0-9-_]"

    static boolean isValidId(String input) {
        !input.find(INVALID_CHAR_REGEX)
    }

    static String makeValidId(String input) {
        input.replaceAll(INVALID_CHAR_REGEX, "-")
    }



}
