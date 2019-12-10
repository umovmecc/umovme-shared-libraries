package me.umov.shared.libraries.domain.cloud.autoscaling.renew

import me.umov.shared.libraries.domain.exception.DomainException

interface InstanceRenewerPort {

    void renewInstances(String groupName) throws DomainException

}
