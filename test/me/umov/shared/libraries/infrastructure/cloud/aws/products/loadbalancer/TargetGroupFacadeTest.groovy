package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer

import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder
import com.amazonaws.services.elasticloadbalancingv2.model.DeregisterTargetsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsResult
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetHealthRequest
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetHealthResult
import com.amazonaws.services.elasticloadbalancingv2.model.RegisterTargetsRequest
import com.amazonaws.services.elasticloadbalancingv2.model.TargetDescription
import com.amazonaws.services.elasticloadbalancingv2.model.TargetHealth
import com.amazonaws.services.elasticloadbalancingv2.model.TargetHealthDescription
import com.amazonaws.services.elasticloadbalancingv2.model.TargetGroup
import spock.lang.Specification

class TargetGroupFacadeTest extends Specification {

    AmazonElasticLoadBalancing client = Mock()
    AmazonElasticLoadBalancingClientBuilder clientBuilder = GroovyMock()
    DescribeTargetGroupsResult describeTargetGroupsResult = Mock()
    DescribeTargetHealthResult describeTargetHealthResult = Mock()

    String balancerArn = "arn-tg-test"
    String region = "us-east-2"
    String accessKey = "123"
    String secretAccessKey = "456"

    TargetGroupFacade facade = new TargetGroupFacade(region: region, accessKey: accessKey, secretAccessKey: secretAccessKey)

    def "setup"() {
        GroovyMock(AmazonElasticLoadBalancingClientBuilder, global: true)

        AmazonElasticLoadBalancingClientBuilder.standard() >> clientBuilder
        clientBuilder.withRegion(region) >> clientBuilder
        clientBuilder.withCredentials(_) >> clientBuilder
        clientBuilder.build() >> client
    }

    def "Should return name"() {
        given:
            TargetGroup targetGroup = new TargetGroup().withTargetGroupName("tg-name")
            describeTargetGroupsResult.targetGroups >> [targetGroup]

            DescribeTargetGroupsRequest request
            client.describeTargetGroups(_ as DescribeTargetGroupsRequest) >> {
                arguments -> request = arguments.first()
                return describeTargetGroupsResult
            }

        when:
            String targetGroupName = facade.name(balancerArn)

        then:
            targetGroupName == "tg-name"
            request.targetGroupArns.first() == balancerArn
    }

    def "Should return instances ids"() {
        given:
            TargetDescription targetDescription1 = new TargetDescription().withId("i-123")
            TargetHealthDescription healthDescription1 = new TargetHealthDescription().withTarget(targetDescription1)

            TargetDescription targetDescription2 = new TargetDescription().withId("i-456")
            TargetHealthDescription healthDescription2 = new TargetHealthDescription().withTarget(targetDescription2)

            List<TargetHealthDescription> healthDescriptions = [healthDescription1, healthDescription2]
            describeTargetHealthResult.targetHealthDescriptions >> healthDescriptions

            DescribeTargetHealthRequest request
            client.describeTargetHealth(_ as DescribeTargetHealthRequest) >> {
                arguments -> request = arguments.first()
                    return describeTargetHealthResult
            }

        when:
            List<String> instancesIds = facade.instances(balancerArn)

        then:
            instancesIds.size() == 2
            instancesIds.get(0) == "i-123"
            instancesIds.get(1) == "i-456"

            request.targetGroupArn == balancerArn
    }

    def "Should return only health instances ids"() {
        given:
            TargetHealth health1 = new TargetHealth().withState("unhealthy")
            TargetDescription targetDescription1 = new TargetDescription().withId("i-123")
            TargetHealthDescription healthDescription1 = new TargetHealthDescription().withTargetHealth(health1).withTarget(targetDescription1)

            TargetHealth health2 = new TargetHealth().withState("healthy")
            TargetDescription targetDescription2 = new TargetDescription().withId("i-456")
            TargetHealthDescription healthDescription2 = new TargetHealthDescription().withTargetHealth(health2).withTarget(targetDescription2)

            List<TargetHealthDescription> healthDescriptions = [healthDescription1, healthDescription2]
            describeTargetHealthResult.targetHealthDescriptions >> healthDescriptions

            DescribeTargetHealthRequest request = new DescribeTargetHealthRequest().withTargetGroupArn(balancerArn)
            client.describeTargetHealth(request) >> describeTargetHealthResult

        when:
            List<String> instancesIds = facade.healthInstances(balancerArn)

        then:
            instancesIds.size() == 1
            instancesIds.first() == "i-456"
    }

    def "Should register instance"() {
        given:
            RegisterTargetsRequest request
            client.registerTargets(_ as RegisterTargetsRequest) >> {
                arguments -> request = arguments.first()
                return
            }

        when:
            facade.registerInstance(balancerArn, "i-123")

        then:
            request.getTargetGroupArn() == balancerArn
            request.getTargets().first().id == "i-123"
    }

    def "Should deregister instance"() {
        given:
            DeregisterTargetsRequest request
            client.deregisterTargets(_ as DeregisterTargetsRequest) >> {
                arguments -> request = arguments.first()
                    return
            }

        when:
            facade.deregisterInstance(balancerArn, "i-123")

        then:
            request.getTargetGroupArn() == balancerArn
            request.getTargets().first().id == "i-123"
    }

}
