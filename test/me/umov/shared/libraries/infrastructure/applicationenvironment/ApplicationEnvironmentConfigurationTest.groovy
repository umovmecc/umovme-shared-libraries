package me.umov.shared.libraries.infrastructure.applicationenvironment

import com.google.inject.AbstractModule
import me.umov.shared.libraries.dependency.applicationenvironment.ApplicationEnvironmentConfiguration
import me.umov.shared.libraries.dependency.applicationenvironment.jenkins.JenkinsApplicationEnvironment
import me.umov.shared.libraries.dependency.applicationenvironment.jenkins.JenkinsModule
import spock.lang.Specification

class ApplicationEnvironmentConfigurationTest extends Specification {

    ApplicationEnvironmentConfiguration configuration = new ApplicationEnvironmentConfiguration()

    def "Should return Jenkins module when application environment is Jenkins"() {
        when:
            AbstractModule module = configuration.buildConfiguration(new JenkinsApplicationEnvironment())

        then:
            module instanceof JenkinsModule
    }

    def "Should throw exception when application environment is unknow"() {
        when:
            configuration.buildConfiguration(null)

        then:
            IllegalArgumentException e = thrown()
            e.message == "Application environment is invalid"
    }
}
