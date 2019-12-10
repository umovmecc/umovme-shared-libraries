package me.umov.shared.libraries.domain.cloud.autoscaling.renew

import me.umov.shared.libraries.dependency.util.InjectionUtils
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupSize
import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.domain.exception.DomainException
import spock.lang.Specification

class InstanceRenewValidatorTest extends Specification {

    InstanceRenewValidator validator = new InstanceRenewValidator()

    def "setup"() {
        GroovyMock(InjectionUtils, global: true)
    }

    def "Should throw exception when min size equals zero"(){
        given:
            AutoScalingGroupSize groupSize = new AutoScalingGroupSize(0, 1, 1)

        when:
            validator.validate(groupSize, [])

        then:
            DomainException e = thrown()
            e.message == "Invalid group size. Nothing will be done."
    }

    def "Should throw exception when max size equals zero"(){
        given:
            AutoScalingGroupSize groupSize = new AutoScalingGroupSize(1, 0, 1)

        when:
            validator.validate(groupSize, [])

        then:
            DomainException e = thrown()
            e.message == "Invalid group size. Nothing will be done."
    }

    def "Should throw exception when desired capacity equals zero"(){
        given:
            AutoScalingGroupSize groupSize = new AutoScalingGroupSize(1, 1, 0)

        when:
            validator.validate(groupSize, [])

        then:
            DomainException e = thrown()
            e.message == "Invalid group size. Nothing will be done."
    }

    def "Should throw exception when a scaling already running"() {
        given:
            Instance instance1 = Mock()
            Instance instance2 = Mock()
            List<Instance> instances = [instance1, instance2]

            AutoScalingGroupSize groupSize = new AutoScalingGroupSize(1, 4, 3)

        when:
            validator.validate(groupSize, instances)

        then:
            DomainException e = thrown()
            e.message == "A scaling is running at the moment. Nothing will be done."
    }

    def "Should not throw exception when all rules are ok"() {
        given:
            Instance instance1 = Mock()
            Instance instance2 = Mock()
            List<Instance> instances = [instance1, instance2]

            AutoScalingGroupSize groupSize = new AutoScalingGroupSize(1, 4, 2)

        when:
            validator.validate(groupSize, instances)

        then:
            noExceptionThrown()
    }
}
