package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer

import groovy.transform.PackageScope

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance

@PackageScope
class TargetGroup extends LoadBalancer {

    TargetGroup(String targetGroupName) {
       super(targetGroupName, getInstance(TargetGroupFacade.class))
    }

}
