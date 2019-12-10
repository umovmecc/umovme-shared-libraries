package me.umov.shared.libraries.infrastructure.cloud.aws.products.autoscaling

import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroup
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupFactory

class AwsAutoScalingGroupFactory implements AutoScalingGroupFactory {

    @Override
    AutoScalingGroup getInstance(String groupName) {
        return new AwsAutoScalingGroup(groupName)
    }

    AwsAutoScalingGroup getAwsInstance(String groupName) {
        return new AwsAutoScalingGroup(groupName)
    }

}
