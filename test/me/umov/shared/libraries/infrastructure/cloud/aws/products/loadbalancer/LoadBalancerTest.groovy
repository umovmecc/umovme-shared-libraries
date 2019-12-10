package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer

import me.umov.shared.libraries.dependency.util.InjectionUtils
import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.domain.printer.Printer
import me.umov.shared.libraries.domain.utils.Wait
import spock.lang.Specification

class LoadBalancerTest extends Specification {

    LoadBalancerFacade facade = Mock()
    Wait wait = Mock()
    Printer printer = Mock()
    String balancerArn = "lb-test"

    LoadBalancer loadBalancer

    def "setup"() {
        GroovyMock(InjectionUtils, global: true)
        InjectionUtils.getInstance(Wait.class) >> wait
        InjectionUtils.getInstance(Printer.class) >> printer
        InjectionUtils.getInstance(ClassicLoadBalancerFacade.class) >> facade

        loadBalancer = new ClassicLoadBalancer(balancerArn)
    }

    def "Should list instances"() {
        given:
            facade.instances(balancerArn) >> ["i-123", "i-456"]

        when:
            List<String> instancesIds = loadBalancer.instances()

        then:
            instancesIds.size() == 2
            instancesIds.get(0) == "i-123"
            instancesIds.get(1) == "i-456"
    }

    def "Should list health instances"() {
        given:
            facade.healthInstances(balancerArn) >> ["i-123", "i-456"]

        when:
            List<String> instancesIds = loadBalancer.healthInstances()

        then:
            instancesIds.size() == 2
            instancesIds.get(0) == "i-123"
            instancesIds.get(1) == "i-456"
    }

    def "Should register instance"() {
        given:
            facade.name(balancerArn) >> "lb-test"

        when:
            Instance instance = Mock() { id() >> "i-123" }
            loadBalancer.registerInstance(instance)

        then:
            1 * printer.println("Registering instance i-123 in $balancerArn")
            1 * facade.registerInstance(balancerArn, "i-123")
    }

    def "Should deregister instance"() {
        given:
            Instance instance = Mock() { id() >> "i-123" }
            facade.name(balancerArn) >> "lb-test"

        when:
            loadBalancer.deregisterInstance(instance)

        then:
            1 * printer.println("Deregistering instance i-123 from $balancerArn")
            1 * facade.deregisterInstance(balancerArn, "i-123")
    }

    def "Should wait until connection draining is completed before deregister instance"(){
        given:
            Instance instance = Mock() { id() >> "i-123" }
            facade.name(balancerArn) >> "lb-test"
            facade.instances(balancerArn) >> ["i-123", "i-456"]

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
            loadBalancer.deregisterInstance(instance)

        then:
            !conditionToStop.call()
            intervalMessage.call() == "Instance i-123 has in-flight requests. Waiting for connection draining."
            successMessage.call() == "Instance i-123 deregistered with success."
            timeoutMessage.call() == "Timeout: The instance i-123 can't be removed from lb-test."
    }

    def "Should stop waiting when instance is no longer in the load balancer"(){
        given:
            Instance instance = Mock() { id() >> "i-123" }
            facade.name(balancerArn) >> "lb-test"
            facade.instances(balancerArn) >> ["i-456"]

            Closure<Boolean> conditionToStop
            wait.until(_ as Closure, _ as Closure, _ as Closure, _ as Closure) >> {
                arguments -> conditionToStop = arguments.first()
            }

        when:
            loadBalancer.deregisterInstance(instance)

        then:
            conditionToStop.call()
    }

}
