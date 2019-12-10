package me.umov.shared.libraries.infrastructure.cloud.aws.products.autoscaling

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder
import com.amazonaws.services.autoscaling.model.*
import com.google.inject.Inject
import com.google.inject.name.Named
import groovy.transform.PackageScope
import me.umov.shared.libraries.domain.exception.DomainException

@PackageScope
class AutoScalingGroupFacade {

    @Inject
    @Named("region")
    private String region

    @Inject
    @Named("accessKey")
    private String accessKey

    @Inject
    @Named("secretAccessKey")
    private String secretAccessKey

    int minSize(String groupName) {
        return getAutoScalingGroup(groupName).getMinSize()
    }

    int maxSize(String groupName) {
        return getAutoScalingGroup(groupName).getMaxSize()
    }

    int desiredCapacity(String groupName) {
        return getAutoScalingGroup(groupName).getDesiredCapacity()
    }

    void updateSize(String groupName, int minSize, int maxSize, int desiredCapacity) {
        UpdateAutoScalingGroupRequest request = new UpdateAutoScalingGroupRequest().withAutoScalingGroupName(groupName)
        request.setMinSize(minSize)
        request.setMaxSize(maxSize)
        request.setDesiredCapacity(desiredCapacity)

        client().updateAutoScalingGroup(request)
    }

    void terminateInstance(String instanceId, boolean shouldDecrementDesiredCapacity) {
        TerminateInstanceInAutoScalingGroupRequest request = new TerminateInstanceInAutoScalingGroupRequest()
                .withInstanceId(instanceId).withShouldDecrementDesiredCapacity(shouldDecrementDesiredCapacity)

        client().terminateInstanceInAutoScalingGroup(request)
    }

    List<String> instancesId(String groupName) {
        getAutoScalingGroup(groupName)
                .getInstances()
                .collect{ it -> it.getInstanceId() }
    }

    List<String> loadBalancersNames(String groupName) {
        return getAutoScalingGroup(groupName).getLoadBalancerNames()
    }

    List<String> targetGroupsNames(String groupName) {
        return getAutoScalingGroup(groupName).getTargetGroupARNs()
    }

    String launchTemplateId(String groupName) {
        return launchTemplate(groupName).getLaunchTemplateId()
    }

    String launchTemplateVersion(String groupName) {
        return launchTemplate(groupName).getVersion()
    }

    private LaunchTemplateSpecification launchTemplate(String groupName) {
        LaunchTemplateSpecification launchTemplateDefault = getAutoScalingGroup(groupName).getLaunchTemplate()
        if (launchTemplateDefault != null) {
            return launchTemplateDefault
        }

        LaunchTemplateSpecification launchTemplateFromPolicy = getAutoScalingGroup(groupName)?.getMixedInstancesPolicy()?.getLaunchTemplate()?.getLaunchTemplateSpecification()
        if (launchTemplateFromPolicy != null) {
            return launchTemplateFromPolicy
        }

        throw new DomainException("Not found Launch Template on Auto Scaling Group")
    }


    Map<String, String> tags(String groupName, boolean propagatedAtLaunch = true) {
        return getAutoScalingGroup(groupName)
                .getTags()
                .findAll { it -> it.propagateAtLaunch == propagatedAtLaunch }
                .collectEntries { [(it.key): it.value] }
    }

    private AutoScalingGroup getAutoScalingGroup(String groupName) {
        DescribeAutoScalingGroupsRequest request = new DescribeAutoScalingGroupsRequest().withAutoScalingGroupNames(groupName)
        DescribeAutoScalingGroupsResult result = client().describeAutoScalingGroups(request)

        List<AutoScalingGroup> groups = result.getAutoScalingGroups()

        if (groups.isEmpty()) {
            throw new DomainException("AutoScaling Group $groupName not found")
        }

        return groups.first()
    }

    private AmazonAutoScaling client() {
        return AmazonAutoScalingClientBuilder.standard()
                .withRegion(this.region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.accessKey, this.secretAccessKey)))
                .build()
    }

}
