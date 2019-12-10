package me.umov.shared.libraries.infrastructure.cloud.aws.products.ec2

import me.umov.shared.libraries.dependency.util.InjectionUtils
import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.infrastructure.cloud.aws.products.global.Tag
import me.umov.shared.libraries.infrastructure.cloud.aws.products.launchtemplate.LaunchTemplate
import spock.lang.Specification

class InstanceLauncherTest extends Specification {

    EC2Facade facade = Mock()

    AwsInstanceFactory instanceFactory = Mock()

    InstanceLauncher instanceLauncher

    def "setup"() {
        GroovyMock(InjectionUtils, global: true)
        instanceLauncher = new InstanceLauncher(facade: facade, instanceFactory: instanceFactory)
    }

    def "Should launch new instance with parameters"() {
        given:
            LaunchTemplate launchTemplate = new LaunchTemplate("lt-123", "1")
            Instance instance = Mock() { id() >> "i-123" }
            Map<String, String> expectedTags = ["name": "test", "env": "prod"]
            facade.launchInstance("subnet-123", "lt-123", "1", "userData", expectedTags) >> "i-123"
            instanceFactory.getInstance("i-123") >> instance
            List<Tag> tags = [new Tag("name", "test"), new Tag("env", "prod")]

        when:
            Instance launchedInstance = instanceLauncher.launchInstance("subnet-123", launchTemplate, "userData", tags)

        then:
            launchedInstance.id() == "i-123"
    }
}
