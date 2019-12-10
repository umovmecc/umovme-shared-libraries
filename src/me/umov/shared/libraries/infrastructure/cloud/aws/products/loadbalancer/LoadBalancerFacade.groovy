package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer

import groovy.transform.PackageScope

@PackageScope
interface LoadBalancerFacade {

    String name(String balancerArn)

    List<String> instances(String balancerArn)

    List<String> healthInstances(String balancerArn)

    void registerInstance(String balancerArn, String instanceId)

    void deregisterInstance(String balancerArn, String instanceId)

}
