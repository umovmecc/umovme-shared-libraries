package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer


import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder
import com.amazonaws.services.elasticloadbalancing.model.*
import spock.lang.Specification

class ClassicLoadBalancerFacadeTest extends Specification {

    AmazonElasticLoadBalancing client = Mock()
    DescribeInstanceHealthResult describeInstanceHealthResult = Mock()
    AmazonElasticLoadBalancingClientBuilder clientBuilder = GroovyMock()

    String region = "us-east-2"
    String accessKey = "123"
    String secretAccessKey = "456"
    String balancerArn = "lb-123"

    ClassicLoadBalancerFacade facade = new ClassicLoadBalancerFacade(region: region, accessKey: accessKey, secretAccessKey: secretAccessKey)

    def "setup"() {
        GroovyMock(AmazonElasticLoadBalancingClientBuilder, global: true)

        AmazonElasticLoadBalancingClientBuilder.standard() >> clientBuilder
        clientBuilder.withRegion(region) >> clientBuilder
        clientBuilder.withCredentials(_) >> clientBuilder
        clientBuilder.build() >> client
    }

    def "Should return name"() {
        when:
            String loadBalancerName = facade.name(balancerArn)

        then:
            loadBalancerName == balancerArn
    }

    def "Should return instances ids"() {
        given:
            InstanceState instanceState1 = new InstanceState().withInstanceId("i-123")
            InstanceState instanceState2 = new InstanceState().withInstanceId("i-456")

            List<InstanceState> instanceStates = [instanceState1, instanceState2]
            describeInstanceHealthResult.instanceStates >> instanceStates

            DescribeInstanceHealthRequest request
            client.describeInstanceHealth(_ as DescribeInstanceHealthRequest) >> {
                arguments -> request = arguments.first()
                    return describeInstanceHealthResult
            }

        when:
            List<String> instancesIds = facade.instances(balancerArn)

        then:
            instancesIds.size() == 2
            instancesIds.get(0) == "i-123"
            instancesIds.get(1) == "i-456"
    }

    def "Should return only health instances ids"() {
        given:
            InstanceState instanceState1 = new InstanceState().withInstanceId("i-123").withState("InService").withDescription("N/A")
            InstanceState instanceState2 = new InstanceState().withInstanceId("i-456").withState("OutOfService").withDescription("Instance deregistration currently in progress.")

            List<InstanceState> instanceStates = [instanceState1, instanceState2]
            describeInstanceHealthResult.instanceStates >> instanceStates

            DescribeInstanceHealthRequest request
            client.describeInstanceHealth(_ as DescribeInstanceHealthRequest) >> {
                arguments -> request = arguments.first()
                    return describeInstanceHealthResult
            }

        when:
            List<String> instancesIds = facade.healthInstances(balancerArn)

        then:
            instancesIds.size() == 1
            instancesIds.first() == "i-123"
    }

    def "Should register instance"() {
        given:
            RegisterInstancesWithLoadBalancerRequest request
            client.registerInstancesWithLoadBalancer(_ as RegisterInstancesWithLoadBalancerRequest) >> {
                arguments -> request = arguments.first()
                return
            }

        when:
            facade.registerInstance(balancerArn, "i-123")

        then:
            request.loadBalancerName == balancerArn
            request.instances.first().instanceId == "i-123"
    }

    def "Should deregister instance"() {
        given:
            DeregisterInstancesFromLoadBalancerRequest request
            client.deregisterInstancesFromLoadBalancer(_ as DeregisterInstancesFromLoadBalancerRequest) >> {
                arguments -> request = arguments.first()
                    return
            }

        when:
            facade.deregisterInstance(balancerArn, "i-123")

        then:
            request.loadBalancerName == balancerArn
            request.instances.first().instanceId == "i-123"
    }
}
