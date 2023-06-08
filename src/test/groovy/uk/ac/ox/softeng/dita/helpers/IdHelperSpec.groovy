package uk.ac.ox.softeng.dita.helpers

import spock.lang.Specification
import uk.ac.ox.softeng.maurodatamapper.dita.helpers.HtmlHelper
import uk.ac.ox.softeng.maurodatamapper.dita.helpers.IdHelper

class IdHelperSpec extends Specification{

    def setup() {
    }

    def cleanup() {

    }


    void "Test a number of examples"() {

        when:

        Map<String, String> examples = [
                "abc123_789xyz_":"abc123_789xyz_",
                "abc 123":"abc-123"
                ]

        then:
            examples.every {input, expectedOutput ->
                IdHelper.makeValidId(input) == expectedOutput
            }

    }

}
