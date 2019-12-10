package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest
import com.amazonaws.services.elasticloadbalancing.model.DescribeInstanceHealthRequest
import com.amazonaws.services.elasticloadbalancing.model.DescribeInstanceHealthResult
import com.amazonaws.services.elasticloadbalancing.model.Instance
import com.amazonaws.services.elasticloadbalancing.model.InstanceState
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest
import com.google.inject.Inject
import com.google.inject.name.Named
import groovy.transform.PackageScope

@PackageScope
class ClassicLoadBalancerFacade implements LoadBalancerFacade {

    private static final String HEALTHY_STATE = "InService"
    private static final String WITHOUT_DESCRIPTION = "N/A"

    @Inject
    @Named("region")
    private String region

    @Inject
    @Named("accessKey")
    private String accessKey

    @Inject
    @Named("secretAccessKey")
    private String secretAccessKey

    @Override
    String name(String balancerArn) {
        return balancerArn
    }

    @Override
    List<String> instances(String balancerArn) {
        return instancesStates(balancerArn)
                    .collect{ it.getInstanceId() }
    }

    @Override
    List<String> healthInstances(String balancerArn) {
        instancesStates(balancerArn)
                .findAll { it.getState() == HEALTHY_STATE && it.getDescription() == WITHOUT_DESCRIPTION }
                .collect { it.getInstanceId() }
    }

    private List<InstanceState> instancesStates(String balancerArn) {
        DescribeInstanceHealthRequest request = new DescribeInstanceHealthRequest()
        request.withLoadBalancerName(balancerArn)

        DescribeInstanceHealthResult result = client().describeInstanceHealth(request)

        return result.getInstanceStates()
    }

    @Override
    void registerInstance(String balancerArn, String instanceId) {
        RegisterInstancesWithLoadBalancerRequest request = new RegisterInstancesWithLoadBalancerRequest()
        request.withLoadBalancerName(balancerArn)
        request.withInstances(new Instance().withInstanceId(instanceId))

        client().registerInstancesWithLoadBalancer(request)
    }

    @Override
    void deregisterInstance(String balancerArn, String instanceId) {
        DeregisterInstancesFromLoadBalancerRequest request = new DeregisterInstancesFromLoadBalancerRequest()
        request.withLoadBalancerName(balancerArn)
        request.withInstances(new Instance().withInstanceId(instanceId))

        client().deregisterInstancesFromLoadBalancer(request)
    }

    private AmazonElasticLoadBalancing client() {
        AmazonElasticLoadBalancingClientBuilder.standard()
                .withRegion(this.region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.accessKey, this.secretAccessKey)))
                .build()
    }

}
