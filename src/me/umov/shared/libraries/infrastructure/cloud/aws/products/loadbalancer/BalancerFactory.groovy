package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer

import me.umov.shared.libraries.domain.exception.DomainException

class BalancerFactory {

    List<LoadBalancer> buildBalancers(List<String> loadBalancerNames, List<String> targetGroupARNs) {
        List<LoadBalancer> balancers = new ArrayList<>()

        loadBalancerNames.each { balancers.add(new ClassicLoadBalancer(it)) }
        targetGroupARNs.each{ balancers.add(new TargetGroup(it))}

        if (balancers.isEmpty()) {
            throw new DomainException("Load balancer not found")
        }

        return balancers
    }

}

