package me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins

import spock.lang.Specification

class JenkinsWrapperTest extends Specification {

    FakeJenkinsContext jenkins = Mock()

    JenkinsWrapper wrapper = new JenkinsWrapper()

    def "setup"() {
        GroovyMock(JenkinsContextFactory, global: true)
    }

    def "Should call input from Jenkins"() {
        given:
            JenkinsContextFactory.getJenkinsContext() >> jenkins

        when:
            wrapper.input("test")

        then:
            1 * jenkins.input("test")
    }

    def "Should call error from Jenkins"() {
        given:
            JenkinsContextFactory.getJenkinsContext() >> jenkins

        when:
            wrapper.error("test")

        then:
            1 * jenkins.error("test")
    }

    def "Should call orintln from Jenkins"() {
        given:
            JenkinsContextFactory.getJenkinsContext() >> jenkins

        when:
            wrapper.println("test")

        then:
            1 * jenkins.println("test")
    }

}



