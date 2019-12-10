package me.umov.shared.libraries.domain.cloud.autoscaling

import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.domain.exception.DomainException

interface AutoScalingGroup {

    String name()

    AutoScalingGroupSize groupSize()

    boolean isEmpty()

    List<Instance> instances()

    void incrementGroupSize(int quantity)

    void updateSize(AutoScalingGroupSize groupSize)

    void terminateInstance(String instanceId, boolean shouldDecrementDesiredCapacity)

    void waitForDesiredCapacityToBeOk() throws DomainException

    void registerInstance(Instance instance)

    void deregisterInstance(Instance instance)

}