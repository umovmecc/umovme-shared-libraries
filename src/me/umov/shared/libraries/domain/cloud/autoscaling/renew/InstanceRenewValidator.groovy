package me.umov.shared.libraries.domain.cloud.autoscaling.renew

import groovy.transform.PackageScope
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupSize
import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.domain.exception.DomainException

@PackageScope
class InstanceRenewValidator {

    void validate(AutoScalingGroupSize groupSize, List<Instance> originalInstances) {
        validateGroupWithSizeZero(groupSize)
        validateScalingIsRunning(groupSize, originalInstances)
    }

    private void validateGroupWithSizeZero(AutoScalingGroupSize groupSize) {
        if (groupSize.allZero()) {
            throw new DomainException("Invalid group size. Nothing will be done.")
        }
    }

    private void validateScalingIsRunning(AutoScalingGroupSize groupSize, List<Instance> originalInstances) {
        if (groupSize.getDesiredCapacity() != originalInstances.size()) {
            throw new DomainException("A scaling is running at the moment. Nothing will be done.")
        }
    }

}
