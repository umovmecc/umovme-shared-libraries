package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer

import groovy.transform.PackageScope

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance

@PackageScope
class ClassicLoadBalancer extends LoadBalancer {

    ClassicLoadBalancer(String name) {
        super(name, getInstance(ClassicLoadBalancerFacade.class))
    }

}
