package me.umov.shared.libraries.domain.cloud.autoscaling

interface AutoScalingGroupFactory {

    AutoScalingGroup getInstance(String groupName)

}
