package com.citytechinc.cq.groovyconsole.geb

import geb.spock.GebReportingSpec
import spock.lang.Stepwise

@Stepwise
class GroovyConsoleSpec extends GebReportingSpec {

    def "google"() {
        when:
        go "http://www.google.com"

        then:
        title == "Google"
    }

    /*
    def "login"() {
        given:
        to LoginPage

        when:
        username = "admin"
        password = "admin"

        and:
        signIn.click()

        then:
        $("title").text() == "AEM Projects"
    }
    */

    /*
    def "console page has required elements"() {
        when:
        to ConsolePage

        then:

    }
    */
}
