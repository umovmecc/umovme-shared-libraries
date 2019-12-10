package me.umov.shared.libraries.infrastructure.cloud.aws.products.ec2

import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.domain.cloud.virtualmachine.InstanceState
import me.umov.shared.libraries.domain.printer.Printer
import me.umov.shared.libraries.domain.utils.Wait

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance
import static me.umov.shared.libraries.domain.cloud.virtualmachine.InstanceState.*

class AwsInstance implements Instance {

    private String id
    private EC2Facade facade
    private Wait wait
    private Printer printer

    AwsInstance(String id) {
        this.id = id
        this.facade = getInstance(EC2Facade.class)
        this.wait = getInstance(Wait.class)
        this.printer = getInstance(Printer.class)
    }

    String id() {
        return this.id
    }

    void start() {
        this.facade.start(this.id)

        this.printer.println("Starting instance ${this.id}...")

        waitForStatus(RUNNING)
    }

    void stop() {
        this.facade.stop(this.id)

        this.printer.println("Stopping instance ${this.id}...")

        waitForStatus(STOPPED)
    }

    void restart() {
        InstanceState status = getState()

        if(RUNNING == status) {
            stop()
        }

        start()
    }

    void terminate() {
        this.facade.terminate(this.id)

        this.printer.println("Terminating instance ${this.id}...")

        waitForStatus(TERMINATED)
    }

    InstanceState getState() {
        String state = this.facade.state(this.id)

        if (state == null) {
            return null
        }

        String status = this.facade.status(this.id)

        return getInstance(state, status)
    }

    String subnetId() {
        this.facade.subnetId(this.id)
    }

    void waitForStatus(InstanceState desiredState) {
        Closure<Boolean> conditionToStop = { -> getState() == desiredState }
        Closure<String> successMessage = { -> "Instance ${this.id} state is $desiredState." }
        Closure<String> timeoutMessage = { -> "Timeout: Instance could not be in the state $desiredState." }

        this.printer.println("Waiting until the instance ${this.id} state is $desiredState.")
        this.wait.until(conditionToStop, null, successMessage, timeoutMessage)
    }

    String toString() {
        return this.id
    }

}

