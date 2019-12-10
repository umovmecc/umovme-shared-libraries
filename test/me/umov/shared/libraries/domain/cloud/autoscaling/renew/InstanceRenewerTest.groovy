package me.umov.shared.libraries.domain.cloud.autoscaling.renew

import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroup
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupFactory
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupSize
import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.domain.exception.DomainException
import me.umov.shared.libraries.domain.printer.Printer
import spock.lang.Specification

class InstanceRenewerTest extends Specification {

    AutoScalingGroupFactory factory = Mock()
    InstanceRenewValidator validator = Mock()
    Printer printer = Mock()
    AutoScalingGroup autoScalingGroup = Mock()
    String groupName = "group-test"

    InstanceRenewer instanceRenewer = new InstanceRenewer(factory: factory, validator: validator, printer: printer)

    def "Should terminate existent instances one by one and finish with same size"() {
        given:
            Instance instance1 = Mock() { id() >> "i-123" }
            Instance instance2 = Mock() { id() >> "i-456" }
            Instance instance3 = Mock() { id() >> "i-789" }
            List<Instance> instances = [instance1, instance2, instance3]
            autoScalingGroup.instances() >> instances

            AutoScalingGroupSize groupSize = new AutoScalingGroupSize(1, 5, 3)
            autoScalingGroup.groupSize() >> groupSize

            factory.getInstance(groupName) >> autoScalingGroup

        when:
            instanceRenewer.renewInstances("group-test")

        then:
            1 * validator.validate(groupSize, instances)
            1 * autoScalingGroup.incrementGroupSize(1)

            1 * autoScalingGroup.terminateInstance("i-123", false)
            1 * autoScalingGroup.terminateInstance("i-456", false)
            1 * autoScalingGroup.terminateInstance("i-789", true)

            1 * autoScalingGroup.updateSize(groupSize)
            4 * autoScalingGroup.waitForDesiredCapacityToBeOk()
    }

    def "Should not throw error only log message when instance to terminate do not exists anymore"() {
        given:
            Instance instance1 = Mock() { id() >> "i-123" }
            Instance instance2 = Mock() { id() >> "i-456" }
            Instance instance3 = Mock() { id() >> "i-789" }
            List<Instance> instances = [instance1, instance2, instance3]
            autoScalingGroup.instances() >> instances

            AutoScalingGroupSize groupSize = new AutoScalingGroupSize(1, 5, 3)
            autoScalingGroup.groupSize() >> groupSize

            factory.getInstance(groupName) >> autoScalingGroup

            autoScalingGroup.terminateInstance("i-456", false) >> { throw new DomainException("Instance i-456 already terminated") }

        when:
            instanceRenewer.renewInstances("group-test")

        then:
            printer.println("Instance i-456 already terminated")
    }

    def "Should log steps of process"() {
        given:
            Instance instance1 = Mock() { id() >> "i-123" }
            Instance instance2 = Mock() { id() >> "i-456" }
            Instance instance3 = Mock() { id() >> "i-789" }
            List<Instance> instances = [instance1, instance2, instance3]
            autoScalingGroup.instances() >> instances

            AutoScalingGroupSize groupSize = new AutoScalingGroupSize(1, 5, 3)
            autoScalingGroup.groupSize() >> groupSize

            factory.getInstance(groupName) >> autoScalingGroup

        when:
            instanceRenewer.renewInstances("group-test")

        then:
            1 * printer.printlnNewLineBefore("---------------------- INIT RENEW AUTOSCALING GROUP INSTANCES ----------------------")
            1 * printer.println("Starting renew instances of autoscaling group ${groupName}")
            1 * printer.printlnNewLineAfter("There are 3 instance(s) to be replaced: i-123, i-456, i-789")
            1 * printer.println("-------------------------- Incrementing group size by one --------------------------")

            1 * printer.println("------------------- Initing step 1 of 3 - Replace instance i-123 -------------------")
            1 * printer.println("------------------- Initing step 2 of 3 - Replace instance i-456 -------------------")
            1 * printer.println("------------------- Initing step 3 of 3 - Replace instance i-789 -------------------")

            1 * printer.println("Renew process finished with success")
            1 * printer.printlnNewLineBeforeAndAfter("---------------------- END RENEW AUTOSCALING GROUP INSTANCES -----------------------")
    }

    def "Should not init process when validator throw an error"() {
        given:
            factory.getInstance(groupName) >> autoScalingGroup
            validator.validate(null, null) >> { throw new DomainException("Error") }

        when:
            instanceRenewer.renewInstances("group-test")

        then:
            0 * printer.println("------------------------ RENEW AUTOSCALING GROUP INSTANCES -------------------------")
            thrown(DomainException.class)
    }

}
