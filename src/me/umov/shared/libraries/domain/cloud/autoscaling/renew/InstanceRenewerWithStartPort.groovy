package me.umov.shared.libraries.domain.cloud.autoscaling.renew

interface InstanceRenewerWithStartPort {

    void startAndRenewInstances(String groupName)

}
