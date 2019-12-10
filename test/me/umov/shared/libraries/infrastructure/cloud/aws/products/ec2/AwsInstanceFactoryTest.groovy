package me.umov.shared.libraries.infrastructure.cloud.aws.products.ec2

import me.umov.shared.libraries.dependency.util.InjectionUtils
import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import spock.lang.Specification

class AwsInstanceFactoryTest extends Specification {

    AwsInstanceFactory factory = new AwsInstanceFactory()

    def "setup"() {
        GroovyMock(InjectionUtils, global: true)
    }

    def "Should create instance"() {
        when:
            Instance instance = factory.getInstance("i-123")

        then:
            instance.id() == "i-123"
    }

}
