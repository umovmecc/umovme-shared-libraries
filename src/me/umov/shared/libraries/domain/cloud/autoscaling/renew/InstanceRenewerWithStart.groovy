package me.umov.shared.libraries.domain.cloud.autoscaling.renew

import com.google.inject.Inject
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroup
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupFactory
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupSize
import me.umov.shared.libraries.domain.cloud.autoscaling.size.AutoScalingSizeUpdater

class InstanceRenewerWithStart implements InstanceRenewerWithStartPort {

    @Inject
    private AutoScalingGroupFactory factory

    @Inject
    private AutoScalingSizeUpdater autoScalingSizeUpdater

    @Inject
    private InstanceRenewer instanceRenewer

    @Override
    void startAndRenewInstances(String groupName) {
        AutoScalingGroup autoScalingGroup = factory.getInstance(groupName)

        if (autoScalingGroup.isEmpty()) {
            AutoScalingGroupSize size = new AutoScalingGroupSize(1, 1, 1)
            autoScalingSizeUpdater.updateAutoScalingGroupSize(groupName, size)
        } else {
            instanceRenewer.renewInstances(groupName)
        }
    }
}
