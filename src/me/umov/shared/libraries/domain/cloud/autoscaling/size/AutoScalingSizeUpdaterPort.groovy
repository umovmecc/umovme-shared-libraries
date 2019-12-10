package me.umov.shared.libraries.domain.cloud.autoscaling.size

import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupSize

interface AutoScalingSizeUpdaterPort {

    void updateAutoScalingGroupSize(String groupName, AutoScalingGroupSize newGroupSize)

    void updateAutoScalingGroupSizeAndWait(String groupName, AutoScalingGroupSize newGroupSize)

}
