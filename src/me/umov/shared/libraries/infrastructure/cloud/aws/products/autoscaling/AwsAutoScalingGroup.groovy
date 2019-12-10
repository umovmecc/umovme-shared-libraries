package me.umov.shared.libraries.infrastructure.cloud.aws.products.autoscaling

import com.amazonaws.services.autoscaling.model.AmazonAutoScalingException
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroup
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

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance

class AwsAutoScalingGroup implements AutoScalingGroup {

    private String name
    private AutoScalingGroupFacade facade
    private BalancerFactory balancerFactory
    private AwsInstanceFactory instanceFactory
    private Printer printer
    private Wait wait

    AwsAutoScalingGroup(String name) {
        this.name = name
        this.facade = getInstance(AutoScalingGroupFacade.class)
        this.instanceFactory = getInstance(AwsInstanceFactory.class)
        this.balancerFactory = getInstance(BalancerFactory.class)
        this.printer = getInstance(Printer.class)
        this.wait = getInstance(Wait.class)
    }

    @Override
    String name() {
        return this.name
    }

    @Override
    List<Instance> instances() {
       return this.facade.instancesId(this.name).collect {
           it -> this.instanceFactory.getInstance(it)
       }
    }

    @Override
    AutoScalingGroupSize groupSize() {
        int minSize = facade.minSize(this.name)
        int maxSize = facade.maxSize(this.name)
        int desiredCapacity = facade.desiredCapacity(this.name)

        return new AutoScalingGroupSize(minSize, maxSize, desiredCapacity)
    }

    @Override
    boolean isEmpty() {
        return groupSize().allZero()
    }

    @Override
    void updateSize(AutoScalingGroupSize groupSize) {
        this.facade.updateSize(this.name, groupSize.minSize, groupSize.maxSize, groupSize.desiredCapacity)
    }

    @Override
    void incrementGroupSize(int quantity) {
        AutoScalingGroupSize newGroupSize = groupSize().incrementSize(quantity)
        updateSize(newGroupSize)
    }

    @Override
    void terminateInstance(String instanceId, boolean shouldDecrementDesiredCapacity) {
        this.printer.println("Terminate instance ${instanceId}")

        try {
            this.facade.terminateInstance(instanceId, shouldDecrementDesiredCapacity)
        } catch (AmazonAutoScalingException e) {
            if (e.getMessage().contains("Instance Id not found")) {
                throw new DomainException("Instance $instanceId already terminated")
            }

            throw e
        }
    }

    @Override
    void registerInstance(Instance instance) {
        balancers().each {
            it.registerInstance(instance)
        }
    }

    @Override
    void deregisterInstance(Instance instance) {
        balancers().each {
            it.deregisterInstance(instance)
        }
    }

    @Override
    void waitForDesiredCapacityToBeOk() throws DomainException {
        Closure<Boolean> conditionToStop = { -> healthInstances() == groupSize().desiredCapacity }
        Closure<String> intervalMessage = { -> "Waiting until autoscaling group size is ok. health instances: ${healthInstances()} desired instances: ${groupSize().desiredCapacity}"}
        Closure<String> successMessage = { -> "Autoscaling group size is ok. health instances: ${healthInstances()} desired instances: ${groupSize().desiredCapacity}"}
        Closure<String> timeoutMessage = { -> "Timeout: Connection draining not over after determined time. Verify load balancer." }

        wait.until(conditionToStop, intervalMessage, successMessage, timeoutMessage)
    }

    LaunchTemplate launchTemplate() {
        String launchTemplateId = this.facade.launchTemplateId(this.name)
        String launchTemplateVersion = this.facade.launchTemplateVersion(this.name)
        return new LaunchTemplate(launchTemplateId, launchTemplateVersion)
    }

    List<Tag> tags() {
        this.facade.tags(this.name)
                .collect { it -> new Tag(it.key, it.value) }
    }

    private List<LoadBalancer> balancers() {
        List<String> loadBalancersNames = this.facade.loadBalancersNames(this.name)
        List<String> targetGroupsNames = this.facade.targetGroupsNames(this.name)

        return this.balancerFactory.buildBalancers(loadBalancersNames, targetGroupsNames)
    }

    private int healthInstances() {
        LoadBalancer balancerWithLessHealthInstances = balancers().min { it.healthInstances().size() }
        return balancerWithLessHealthInstances.healthInstances().size()
    }

}

