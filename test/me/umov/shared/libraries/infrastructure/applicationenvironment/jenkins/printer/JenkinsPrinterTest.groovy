package me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins.printer

import me.umov.shared.libraries.domain.printer.ExternalPrinter
import me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins.JenkinsWrapper
import spock.lang.Specification

class JenkinsPrinterTest extends Specification {

    ExternalPrinter printer = new JenkinsPrinter()

    def "setup"() {
        GroovyMock(JenkinsWrapper, global: true)
    }

    def "Should print with new line"() {
        when:
            printer.println("test")

        then:
            1 * JenkinsWrapper.println("test")
    }

}
