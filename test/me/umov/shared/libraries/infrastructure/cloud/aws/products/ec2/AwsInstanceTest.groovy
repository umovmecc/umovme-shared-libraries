package me.umov.shared.libraries.infrastructure.cloud.aws.products.ec2

import me.umov.shared.libraries.dependency.util.InjectionUtils
import me.umov.shared.libraries.domain.cloud.virtualmachine.InstanceState
import me.umov.shared.libraries.domain.printer.Printer
import me.umov.shared.libraries.domain.utils.Wait
import spock.lang.Specification

import static me.umov.shared.libraries.domain.cloud.virtualmachine.InstanceState.RUNNING

class AwsInstanceTest extends Specification {

    EC2Facade facade = Mock()
    Wait wait = Mock()
    Printer printer = Mock()
    String instanceId = "i-123"

    AwsInstance instance

    def "setup"() {
        GroovyMock(InjectionUtils, global: true)
        InjectionUtils.getInstance(EC2Facade.class) >> facade
        InjectionUtils.getInstance(Wait.class) >> wait
        InjectionUtils.getInstance(Printer.class) >> printer

        instance = new AwsInstance(instanceId)
    }

    def "Should start instance and wait until instance is RUNNING"() {
        given:
            facade.state(instanceId) >> "running"
            facade.status(instanceId) >> "passed"

            Closure<Boolean> conditionToStop
            wait.until(_ as Closure, null, _ as Closure, _ as Closure) >> {
                arguments -> conditionToStop = arguments.first()
            }

        when:
            instance.start()

        then:
            1 * facade.start(instanceId)
            1 * printer.println("Starting instance $instanceId...")
            conditionToStop.call()
    }

    def "Should stop instance and wait until instance is STOPPED"() {
        given:
            facade.state(instanceId) >> "stopped"
            facade.status(instanceId) >> null

            Closure<Boolean> conditionToStop
            wait.until(_ as Closure, null, _ as Closure, _ as Closure) >> {
                arguments -> conditionToStop = arguments.first()
            }

        when:
            instance.stop()

        then:
            1 * facade.stop(instanceId)
            1 * printer.println("Stopping instance $instanceId...")
            conditionToStop.call()
    }

    def "Should restart instance when instance is RUNNING"() {
        given:
            facade.state(instanceId) >> "running"
            facade.status(instanceId) >> "passed"

            Closure<Boolean> conditionToStop
            wait.until(_ as Closure, null, _ as Closure, _ as Closure) >> {
                arguments -> conditionToStop = arguments.first()
            }

        when:
            instance.restart()

        then:
            1 * facade.stop(instanceId)
    }

    def "Should restart instance when instance is not RUNNING"() {
        given:
            facade.state(instanceId) >> "stopped"
            facade.status(instanceId) >> null

            Closure<Boolean> conditionToStop
            wait.until(_ as Closure, null, _ as Closure, _ as Closure) >> {
                arguments -> conditionToStop = arguments.first()
            }

        when:
            instance.restart()

        then:
            1 * facade.start(instanceId)
    }

    def "Should terminate instance and wait until instance is TERMINATED"() {
        given:
            facade.state(instanceId) >> "terminated"
            facade.status(instanceId) >> null

            Closure<Boolean> conditionToStop
            wait.until(_ as Closure, null, _ as Closure, _ as Closure) >> {
                arguments -> conditionToStop = arguments.first()
            }

        when:
            instance.terminate()

        then:
            1 * facade.terminate(instanceId)
            1 * printer.println("Terminating instance $instanceId...")
            conditionToStop.call()
    }

    def "Should return state"() {
        given:
            facade.state(instanceId) >> "running"
            facade.status(instanceId) >> "passed"

        when:
            InstanceState instanceState = instance.getState()

        then:
            instanceState == RUNNING
    }

    def "Should return null when state not returning"() {
        given:
            facade.state(instanceId) >> null

        when:
            InstanceState instanceState = instance.getState()

        then:
            instanceState == null
    }

    def "Should return subnet id"() {
        given:
            facade.subnetId(instanceId) >> "subnet-123"

        when:
            String subnetId = instance.subnetId()

        then:
            subnetId == "subnet-123"
    }

    def "Should wait instance until to be in desired status"() {
        given:
            facade.state(instanceId) >> "pending"
            facade.status(instanceId) >> null

            Closure<Boolean> conditionToStop
            Closure<String> successMessage
            Closure<String> timeoutMessage
            wait.until(_ as Closure, null, _ as Closure, _ as Closure) >> {
                arguments ->
                    conditionToStop = arguments[0]
                    successMessage = arguments[2]
                    timeoutMessage = arguments[3]
            }

        when:
            instance.waitForStatus(RUNNING)

        then:
            !conditionToStop.call()
            successMessage.call() == "Instance $instanceId state is RUNNING."
            timeoutMessage.call() == "Timeout: Instance could not be in the state RUNNING."
    }

    def "Should stop waiting when instance in desired status"() {
        given:
            facade.state(instanceId) >> "running"
            facade.status(instanceId) >> "passed"

            Closure<Boolean> conditionToStop
            wait.until(_ as Closure, null, _ as Closure, _ as Closure) >> {
                arguments -> conditionToStop = arguments.first()
            }

        when:
            instance.waitForStatus(RUNNING)

        then:
            conditionToStop.call()
    }

}
