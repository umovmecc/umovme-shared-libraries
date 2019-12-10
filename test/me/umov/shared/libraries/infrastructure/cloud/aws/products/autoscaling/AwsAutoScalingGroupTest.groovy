package me.umov.shared.libraries.infrastructure.cloud.aws.products.autoscaling

import com.amazonaws.services.autoscaling.model.AmazonAutoScalingException
import me.umov.shared.libraries.dependency.util.InjectionUtils
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupSize
import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.domain.exception.DomainException
import me.umov.shared.libraries.domain.printer.Printer
import me.umov.shared.libraries.domain.utils.Wait
import me.umov.shared.libraries.infrastructure.cloud.aws.products.ec2.AwsInstanceFactory
import me.umov.shared.libraries.infrastructure.cloud.aws.products.global.Tag
import me.umov.shared.libraries.infrastructure.cloud.aws.products.launchtemplate.LaunchTemplate
import me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer.BalancerFactory
import me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer.LoadBalancer
import spock.lang.Specification

class AwsAutoScalingGroupTest extends Specification {

    AutoScalingGroupFacade facade = Mock()
    BalancerFactory balancerFactory = Mock()
    AwsInstanceFactory instanceFactory = Mock()
    Printer printer = Mock()
    Wait wait = Mock()

    String groupName = "as-test"
    AwsAutoScalingGroup group

    def "setup"() {
        GroovyMock(InjectionUtils, global: true)
        InjectionUtils.getInstance(AutoScalingGroupFacade.class) >> facade
        InjectionUtils.getInstance(AwsInstanceFactory.class) >> instanceFactory
        InjectionUtils.getInstance(BalancerFactory.class) >> balancerFactory
        InjectionUtils.getInstance(Printer.class) >> printer
        InjectionUtils.getInstance(Wait.class) >> wait

        group = new AwsAutoScalingGroup(groupName)
    }

    def "Should list instances"() {
        given:
            String id1 = "i-123"
            String id2 = "i-456"
            Instance instance1 = Mock() { id() >> id1 }
            Instance instance2 = Mock() { id() >> id2 }

            facade.instancesId(groupName) >> [id1, id2]
            instanceFactory.getInstance(id1) >> instance1
            instanceFactory.getInstance(id2) >> instance2

        when:
            List<Instance> instances = group.instances()

        then:
            instances.size() == 2

            instances.get(0).id() == id1
            instances.get(1).id() == id2
    }

    def "Should return size"() {
        given:
            facade.minSize(groupName) >> 1
            facade.maxSize(groupName) >> 4
            facade.desiredCapacity(groupName) >> 2

        when:
            AutoScalingGroupSize size = group.groupSize()

        then:
            size.minSize == 1
            size.maxSize == 4
            size.desiredCapacity == 2
    }

    def "Should update size"() {
        given:
            AutoScalingGroupSize size = new AutoScalingGroupSize(1, 4, 2)

        when:
            group.updateSize(size)

        then:
            1 * facade.updateSize(groupName, 1, 4, 2)
    }

    def "Should increment size"() {
        given:
            facade.minSize(groupName) >> 1
            facade.maxSize(groupName) >> 4
            facade.desiredCapacity(groupName) >> 2

        when:
            group.incrementGroupSize(2)

        then:
            1 * facade.updateSize(groupName, 3, 6, 4)
    }

    def "Should terminate instance"() {
        when:
            group.terminateInstance("i-123", false)

        then:
            1 * printer.println("Terminate instance i-123")
            1 * facade.terminateInstance("i-123", false)
    }

    def "Should throw domain exception when try terminate instance and instance not found"() {
        given:
            facade.terminateInstance("i-123", false) >> { throw new AmazonAutoScalingException("Instance Id not found") }

        when:
            group.terminateInstance("i-123", false)

        then:
            DomainException e = thrown()
            e.message == "Instance i-123 already terminated"
    }

    def "Should re throw exception when is one not known"() {
        given:
            facade.terminateInstance("i-123", false) >> { throw new AmazonAutoScalingException() }

        when:
            group.terminateInstance("i-123", false)

        then:
            AmazonAutoScalingException e = thrown()
    }

    def "Should register instance in all balancers"() {
        given:
            Instance instance = Mock()

            List<String> loadBalancerNames = ["lb-1", "lb-2"]
            facade.loadBalancersNames(groupName) >> loadBalancerNames

            List<String> targetGroupsNames = ["tg-1", "tg-2"]
            facade.targetGroupsNames(groupName) >> targetGroupsNames

            LoadBalancer lb1 = Mock()
            LoadBalancer lb2 = Mock()
            LoadBalancer tg1 = Mock()
            LoadBalancer tg2 = Mock()
            List<LoadBalancer> balancers = [lb1, lb2, tg1, tg2]
            balancerFactory.buildBalancers(loadBalancerNames, targetGroupsNames) >> balancers

        when:
            group.registerInstance(instance)

        then:
            1 * lb1.registerInstance(instance)
            1 * lb2.registerInstance(instance)

            1 * tg1.registerInstance(instance)
            1 * tg2.registerInstance(instance)
    }

    def "Should deregister instance in all balancers"() {
        given:
            Instance instance = Mock()

            List<String> loadBalancerNames = ["lb-1", "lb-2"]
            facade.loadBalancersNames(groupName) >> loadBalancerNames

            List<String> targetGroupsNames = ["tg-1", "tg-2"]
            facade.targetGroupsNames(groupName) >> targetGroupsNames

            LoadBalancer lb1 = Mock()
            LoadBalancer lb2 = Mock()
            LoadBalancer tg1 = Mock()
            LoadBalancer tg2 = Mock()
            List<LoadBalancer> balancers = [lb1, lb2, tg1, tg2]
            balancerFactory.buildBalancers(loadBalancerNames, targetGroupsNames) >> balancers

        when:
            group.deregisterInstance(instance)

        then:
            1 * lb1.deregisterInstance(instance)
            1 * lb2.deregisterInstance(instance)

            1 * tg1.deregisterInstance(instance)
            1 * tg2.deregisterInstance(instance)
    }

    def "Should wait until desired capacity to be ok"() {
        given:
            facade.desiredCapacity(groupName) >> 2

            LoadBalancer lb1 = Mock() { healthInstances() >> ["i-123"] }
            LoadBalancer lb2 = Mock() { healthInstances() >> ["i-123", "i-456"] }
            balancerFactory.buildBalancers(_, _) >> [lb1, lb2]

            Closure<Boolean> conditionToStop
            Closure<String> intervalMessage
            Closure<String> successMessage
            Closure<String> timeoutMessage
            wait.until(_ as Closure, _ as Closure, _ as Closure, _ as Closure) >> {
                arguments ->
                    conditionToStop = arguments[0]
                    intervalMessage = arguments[1]
                    successMessage = arguments[2]
                    timeoutMessage = arguments[3]
            }

        when:
            group.waitForDesiredCapacityToBeOk()

        then:
            !conditionToStop.call()
            intervalMessage.call() == "Waiting until autoscaling group size is ok. health instances: 1 desired instances: 2"
            successMessage.call() == "Autoscaling group size is ok. health instances: 1 desired instances: 2"
            timeoutMessage.call() == "Timeout: Connection draining not over after determined time. Verify load balancer."
    }

    def "Should stop when waiting for desired capacity and desired capacity equals to actual health instances"() {
        given:
            facade.desiredCapacity(groupName) >> 2

            LoadBalancer lb1 = Mock() { healthInstances() >> ["i-123", "i-456"] }
            LoadBalancer lb2 = Mock() { healthInstances() >> ["i-123", "i-456"] }
            balancerFactory.buildBalancers(_, _) >> [lb1, lb2]

            Closure<Boolean> conditionToStop
            wait.until(_ as Closure, _ as Closure, _ as Closure, _ as Closure) >> {
                arguments ->
                    conditionToStop = arguments[0]
            }

        when:
            group.waitForDesiredCapacityToBeOk()

        then:
            conditionToStop.call()
    }

    def "Should return Launch Template"() {
        given:
            facade.launchTemplateId(groupName) >> "lt-123"
            facade.launchTemplateVersion(groupName) >> "1"

        when:
            LaunchTemplate launchTemplate = group.launchTemplate()

        then:
            launchTemplate.id == "lt-123"
            launchTemplate.version == "1"
    }

    def "Should return tags"() {
        given:
            facade.tags(groupName) >> Map.of("name", "i-123", "prod", "true")

        when:
            List<Tag> tags = group.tags()

        then:
            tags.size() == 2

            tags.count {
                t -> (t.name == "prod" && t.value == "true") ||
                     (t.name == "name" && t.value == "i-123")
            } == 2
    }


}
