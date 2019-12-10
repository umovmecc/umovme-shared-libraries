package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer

import me.umov.shared.libraries.dependency.util.InjectionUtils
import me.umov.shared.libraries.domain.exception.DomainException
import spock.lang.Specification

class BalancerFactoryTest extends Specification {

    BalancerFactory factory = new BalancerFactory()

    def "setup"() {
        GroovyMock(InjectionUtils, global: true)
    }

    def "Should return LB instance when Auto Scaling Group has LB"() {
        given:
            List<String> loadBalancerNames = ["lb-123", "lb-456"]
            List<String> targetGroupNames = []

        when:
            List<LoadBalancer> balancers = factory.buildBalancers(loadBalancerNames, targetGroupNames)

        then:
            balancers.size() == 2

            balancers.get(0) instanceof ClassicLoadBalancer
            balancers.get(1) instanceof ClassicLoadBalancer

            balancers.get(0).balancerArn == "lb-123"
            balancers.get(1).balancerArn == "lb-456"
    }

    def "Should return ALB instance when Auto Scaling Group has ALB"() {
        given:
            List<String> loadBalancerNames = []
            List<String> targetGroupNames = ["alb-123", "alb-456"]

        when:
            List<LoadBalancer> balancers = factory.buildBalancers(loadBalancerNames, targetGroupNames)

        then:
            balancers.size() == 2

            balancers.get(0) instanceof TargetGroup
            balancers.get(1) instanceof TargetGroup

            balancers.get(0).balancerArn == "alb-123"
            balancers.get(1).balancerArn == "alb-456"
    }

    def "Should throw exception when Auto Scaling Group do not have any load balancer"() {
        given:
            List<String> loadBalancerNames = []
            List<String> targetGroupNames = []

        when:
            factory.buildBalancers(loadBalancerNames, targetGroupNames)

        then:
            DomainException e = thrown()
            e.message == "Load balancer not found"
    }

}
