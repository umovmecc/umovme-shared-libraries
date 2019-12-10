package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer

import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.domain.printer.Printer
import me.umov.shared.libraries.domain.utils.Wait

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance

abstract class LoadBalancer {

    String balancerArn
    protected LoadBalancerFacade facade
    protected Wait wait
    protected Printer printer

    LoadBalancer(String balancerArn, LoadBalancerFacade facade) {
        this.balancerArn = balancerArn
        this.facade = facade
        this.wait = getInstance(Wait.class)
        this.printer = getInstance(Printer.class)
    }

    List<String> instances() {
        def instances = this.facade.instances(this.balancerArn)
        return instances
    }

    List<String> healthInstances() {
        return this.facade.healthInstances(this.balancerArn)
    }

    void registerInstance(Instance instance) {
        printer.println("Registering instance ${instance.id()} in ${name()}")

        this.facade.registerInstance(this.balancerArn, instance.id())
    }

    void deregisterInstance(Instance instance) {
        printer.println("Deregistering instance ${instance.id()} from ${name()}")

        this.facade.deregisterInstance(this.balancerArn, instance.id())

        waitForConnectionDraining(instance)
    }

    void waitForConnectionDraining(Instance instance) {
        Closure<Boolean> conditionToStop = { -> !instances().contains(instance.id()) }
        Closure<String> intervalMessage = { -> "Instance ${instance.id()} has in-flight requests. Waiting for connection draining."}
        Closure<String> successMessage = { -> "Instance ${instance.id()} deregistered with success."}
        Closure<String> timeoutMessage = { -> "Timeout: The instance ${instance.id()} can't be removed from ${this.balancerArn}." }

        wait.until(conditionToStop, intervalMessage, successMessage, timeoutMessage)
    }

    protected String name() {
        return this.facade.name(this.balancerArn)
    }

}
