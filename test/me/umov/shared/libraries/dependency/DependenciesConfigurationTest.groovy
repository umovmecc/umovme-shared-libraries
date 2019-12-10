package me.umov.shared.libraries.dependency

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import me.umov.shared.libraries.dependency.applicationenvironment.jenkins.JenkinsApplicationEnvironment
import me.umov.shared.libraries.dependency.cloud.aws.AwsCloudProvider
import me.umov.shared.libraries.infrastructure.cloud.aws.credentials.AwsKeys
import me.umov.shared.libraries.dependency.util.InjectionUtils
import spock.lang.Specification

class DependenciesConfigurationTest extends Specification {

    Injector injector = Mock()

    DependenciesConfiguration configuration = new DependenciesConfiguration()

    def "setup"() {
        GroovyMock(Guice.class, global: true)
        GroovyMock(InjectionUtils.class, global: true)
    }

    def "Should configure dependencies for Cloud Provider and Application Environment"() {
        given:
            AwsKeys keys = new AwsKeys("123", "456")

            List<AbstractModule> modules
            Guice.createInjector(_ as List) >> {
                arguments -> modules = arguments.first()
                return injector
            }

        when:
            configuration.configureDependencies(new AwsCloudProvider("us-east-2", keys), new JenkinsApplicationEnvironment())

        then:
            1 * InjectionUtils.configure(injector)
            modules.size() == 3
    }
}
