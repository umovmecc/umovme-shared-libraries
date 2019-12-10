package me.umov.shared.libraries.infrastructure.cloud.aws.products.ec2

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.*
import com.google.inject.Inject
import com.google.inject.name.Named
import groovy.transform.PackageScope

@PackageScope
class EC2Facade {

    @Inject
    @Named("region")
    private String region

    @Inject
    @Named("accessKey")
    private String accessKey

    @Inject
    @Named("secretAccessKey")
    private String secretAccessKey

    void start(String instanceId) {
        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instanceId)
        client().startInstances(request)
    }

    void stop(String instanceId) {
        StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceId)
        client().stopInstances(request)
    }

    void terminate(String instanceId) {
        TerminateInstancesRequest request = new TerminateInstancesRequest().withInstanceIds(instanceId)
        client().terminateInstances(request)
    }

    String state(String instanceId) {
        InstanceStatus instanceStatus = instanceStatus(instanceId)

        if (instanceStatus == null) {
            return null
        }

        return instanceStatus.getInstanceState().getName()
    }

    String status(String instanceId) {
        InstanceStatus instanceStatus = instanceStatus(instanceId)

        if (instanceStatus == null) {
            return null
        }

        List<InstanceStatusDetails> details = instanceStatus.getInstanceStatus().getDetails()

        return details.isEmpty() ? null : details.get(0).getStatus()
    }

    String subnetId(String instanceId) {
        DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instanceId)
        DescribeInstancesResult result = client().describeInstances(request)

        return result.reservations.first().instances.first().getNetworkInterfaces().first().getSubnetId()
    }

    String launchInstance(String subnetId, String launchTemplateId, String launchTemplateVersion, String userData, Map<String, String> tags) {
        LaunchTemplateSpecification launchTemplate = buildLaunchTemplate(launchTemplateId, launchTemplateVersion)
        InstanceNetworkInterfaceSpecification networkInterface = buildNetworkInterface(subnetId)
        TagSpecification tagSpecification = buildTagSpecification(tags)

        RunInstancesRequest request = new RunInstancesRequest()
                .withLaunchTemplate(launchTemplate)
                .withMinCount(1).withMaxCount(1)
                .withUserData(userData)
                .withNetworkInterfaces(networkInterface)
                .withTagSpecifications(tagSpecification)

        RunInstancesResult result = client().runInstances(request)
        String instanceId = result.getReservation().getInstances().first().getInstanceId()

        return instanceId
    }

    private LaunchTemplateSpecification buildLaunchTemplate(String launchTemplateId, String launchTemplateVersion) {
        LaunchTemplateSpecification launchTemplate = new LaunchTemplateSpecification()
        launchTemplate.withLaunchTemplateId(launchTemplateId).withVersion(launchTemplateVersion)
        return launchTemplate
    }

    private InstanceNetworkInterfaceSpecification buildNetworkInterface(String subnetId) {
        InstanceNetworkInterfaceSpecification network = new InstanceNetworkInterfaceSpecification()
        network.withDeviceIndex(0)
        network.withSubnetId(subnetId)
        network.withAssociatePublicIpAddress(true)

        return network
    }

    private TagSpecification buildTagSpecification(Map<String, String> tags) {
        List<Tag> awsTags = tags.collect { new Tag().withKey(it.key).withValue(it.value) }
        return new TagSpecification().withResourceType(ResourceType.Instance).withTags(awsTags)
    }

    private InstanceStatus instanceStatus(String instanceId) {
        DescribeInstanceStatusRequest request = new DescribeInstanceStatusRequest().withInstanceIds(instanceId).withIncludeAllInstances(true)
        DescribeInstanceStatusResult result = client().describeInstanceStatus(request)

        if (result.getInstanceStatuses().isEmpty()) {
            return null
        }

        return result.getInstanceStatuses().get(0)
    }

    AmazonEC2 client() {
        AmazonEC2ClientBuilder.standard()
                .withRegion(this.region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.accessKey, this.secretAccessKey)))
                .build()
    }

}
