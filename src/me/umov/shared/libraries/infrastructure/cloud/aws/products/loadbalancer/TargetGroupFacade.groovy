package me.umov.shared.libraries.infrastructure.cloud.aws.products.loadbalancer

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder
import com.amazonaws.services.elasticloadbalancingv2.model.*
import com.google.inject.Inject
import com.google.inject.name.Named
import groovy.transform.PackageScope

@PackageScope
class TargetGroupFacade implements LoadBalancerFacade {

    private static final String HEALTHY_STATE = "healthy"

    @Inject
    @Named("region")
    private String region

    @Inject
    @Named("accessKey")
    private String accessKey

    @Inject
    @Named("secretAccessKey")
    private String secretAccessKey

    String name(String balancerArn) {
        DescribeTargetGroupsRequest request = new DescribeTargetGroupsRequest()
        request.withTargetGroupArns(balancerArn)

        DescribeTargetGroupsResult result = client().describeTargetGroups(request)

        return result.targetGroups.first().targetGroupName
    }

    List<String> instances(String balancerArn) {
        return targetHealthDescriptions(balancerArn)
                .collect { it.getTarget().getId() }
    }

    List<String> healthInstances(String balancerArn) {
        return targetHealthDescriptions(balancerArn)
                .findAll { it.getTargetHealth().getState() == HEALTHY_STATE }
                .collect { it.getTarget().getId() }
    }

    private List<TargetHealthDescription> targetHealthDescriptions(String balancerArn) {
        DescribeTargetHealthRequest request = new DescribeTargetHealthRequest().withTargetGroupArn(balancerArn)
        DescribeTargetHealthResult result = client().describeTargetHealth(request)

        return result.getTargetHealthDescriptions()
    }

    void registerInstance(String balancerArn, String instanceId) {
        TargetDescription targetDescription = new TargetDescription().withId(instanceId)
        RegisterTargetsRequest request = new RegisterTargetsRequest().withTargetGroupArn(balancerArn).withTargets(targetDescription)

        client().registerTargets(request)
    }

    void deregisterInstance(String balancerArn, String instanceId) {
        TargetDescription targetDescription = new TargetDescription().withId(instanceId)
        DeregisterTargetsRequest request = new DeregisterTargetsRequest().withTargetGroupArn(balancerArn).withTargets(targetDescription)

        client().deregisterTargets(request)
    }

    private AmazonElasticLoadBalancing client() {
        AmazonElasticLoadBalancingClientBuilder.standard()
                .withRegion(this.region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.accessKey, this.secretAccessKey)))
                .build()
    }

}
