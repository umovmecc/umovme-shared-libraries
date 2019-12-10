package me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins

import spock.lang.Specification

class JenkinsContextFactoryTest extends Specification {

    def "Should return null when context is not initialized"() {
        expect:
            JenkinsContextFactory.getJenkinsContext() == null
    }

    def "Should return context after context is initialized"() {
        given:
            FakeJenkinsContext fakeJenkinsContext = new FakeJenkinsContext()
            JenkinsContextFactory.configureJenkinsContext(fakeJenkinsContext)

        when:
            def jenkinsContext = JenkinsContextFactory.getJenkinsContext()

        then:
            jenkinsContext == fakeJenkinsContext
    }

    def "Should return the same context for consective calls after context is initialized"() {
        given:
            FakeJenkinsContext fakeJenkinsContext = new FakeJenkinsContext()
            JenkinsContextFactory.configureJenkinsContext(fakeJenkinsContext)

        when:
            def jenkinsContext = JenkinsContextFactory.getJenkinsContext()
            def anotherJenkinsContext = JenkinsContextFactory.getJenkinsContext()

        then:
            jenkinsContext == anotherJenkinsContext
    }

}
