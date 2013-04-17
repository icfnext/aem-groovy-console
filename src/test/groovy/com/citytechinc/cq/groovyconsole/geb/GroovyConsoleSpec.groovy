package com.citytechinc.cq.groovyconsole.geb

import com.citytechinc.cq.groovyconsole.geb.pages.LoginPage
import geb.spock.GebReportingSpec
import spock.lang.Stepwise

@Stepwise
class GroovyConsoleSpec extends GebReportingSpec {

    def "login"() {
        given:
        to LoginPage

        when:
        username = "admin"
        password = "admin"

        and:
        signIn.click()

        then:
        title == "AEM Projects"
    }

    /*
    def "console page has required elements"() {
        when:
        to ConsolePage

        then:

    }
    */
}
