package me.umov.shared.libraries.infrastructure.cloud.aws.products.autoscaling

import me.umov.shared.libraries.dependency.util.InjectionUtils
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroup
import spock.lang.Specification

class AwsAutoScalingGroupFactoryTest extends Specification {

    AwsAutoScalingGroupFactory factory = new AwsAutoScalingGroupFactory()
    String groupName = "as-test"

    def "setup"() {
        GroovyMock(InjectionUtils, global: true)
    }

    def "Should create and return an AwsAutoScalingGroup instance as AutoScalingGroup"() {
        when:
            AutoScalingGroup group = factory.getInstance(groupName)

        then:
            group.name() == groupName
    }

    def "Should create and return an AwsAutoScalingGroup instance as AwsAutoScalingGroup"() {
        when:
            AwsAutoScalingGroup group = factory.getAwsInstance(groupName)

        then:
            group.name() == groupName
    }

}
