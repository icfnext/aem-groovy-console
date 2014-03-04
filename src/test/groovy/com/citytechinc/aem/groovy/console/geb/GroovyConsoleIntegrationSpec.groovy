package com.citytechinc.aem.groovy.console.geb

import com.citytechinc.aem.groovy.console.geb.pages.ConsolePage
import com.citytechinc.aem.groovy.console.geb.pages.LoginPage
import geb.spock.GebReportingSpec
import spock.lang.Stepwise

@Stepwise
class GroovyConsoleIntegrationSpec extends GebReportingSpec {

    def "login"() {
        given:
        to LoginPage

        when:
        username = config.rawConfig.username
        password = config.rawConfig.password

        and:
        signIn.click()

        then:
        title == "AEM Projects"
    }

    def "console page has required elements"() {
        when:
        to ConsolePage

        then:
        title == "CQ Groovy Console"
    }
}
