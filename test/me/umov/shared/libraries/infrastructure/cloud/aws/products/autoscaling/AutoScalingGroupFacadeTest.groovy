package me.umov.shared.libraries.infrastructure.cloud.aws.products.autoscaling

import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder
import com.amazonaws.services.autoscaling.model.AutoScalingGroup
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult
import com.amazonaws.services.autoscaling.model.Instance
import com.amazonaws.services.autoscaling.model.LaunchTemplate
import com.amazonaws.services.autoscaling.model.LaunchTemplateSpecification
import com.amazonaws.services.autoscaling.model.MixedInstancesPolicy
import com.amazonaws.services.autoscaling.model.TagDescription
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest
import com.amazonaws.services.autoscaling.model.TerminateInstanceInAutoScalingGroupResult
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupRequest
import com.amazonaws.services.autoscaling.model.UpdateAutoScalingGroupResult
import me.umov.shared.libraries.domain.exception.DomainException
import spock.lang.Specification

class AutoScalingGroupFacadeTest extends Specification {

    AmazonAutoScaling client = Mock()
    AutoScalingGroup awsAutoScalingGroup = Mock()
    DescribeAutoScalingGroupsResult describeAutoScalingGroupsResult = Mock()
    AmazonAutoScalingClientBuilder clientBuilder = GroovyMock()

    String region = "us-east-2"
    String accessKey = "123"
    String secretAccessKey = "456"
    String groupName = "as-test"

    AutoScalingGroupFacade facade = new AutoScalingGroupFacade(region: region, accessKey: accessKey, secretAccessKey: secretAccessKey)

    def "setup"() {
        GroovyMock(AmazonAutoScalingClientBuilder, global: true)

        AmazonAutoScalingClientBuilder.standard() >> clientBuilder
        clientBuilder.withRegion(region) >> clientBuilder
        clientBuilder.withCredentials(_) >> clientBuilder
        clientBuilder.build() >> client
    }

    def "Should find AutoScalingGroup correctly"() {
        given:
            awsAutoScalingGroup.minSize >> 2
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]

            DescribeAutoScalingGroupsRequest request
            client.describeAutoScalingGroups(_ as DescribeAutoScalingGroupsRequest) >> {
                arguments -> request = arguments[0]
                return describeAutoScalingGroupsResult
            }

        when:
            facade.minSize(groupName)

        then:
            request.getAutoScalingGroupNames().first() == groupName
    }

    def "Should throw exception when AutoScalingGroup is not found"() {
        given:
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> []
            client.describeAutoScalingGroups(_ as DescribeAutoScalingGroupsRequest) >> describeAutoScalingGroupsResult

        when:
            facade.minSize(groupName)

        then:
            DomainException e = thrown()
            e.message == "AutoScaling Group $groupName not found"
    }

    def "Should return min size"() {
        given:
            awsAutoScalingGroup.minSize >> 2
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            int minSize = facade.minSize(groupName)

        then:
            minSize == 2
    }

    def "Should return max size"() {
        given:
            awsAutoScalingGroup.maxSize >> 5
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            int maxSize = facade.maxSize(groupName)

        then:
            maxSize == 5
    }

    def "Should return desired capacity"() {
        given:
            awsAutoScalingGroup.desiredCapacity >> 3
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            int desiredCapacity = facade.desiredCapacity(groupName)

        then:
            desiredCapacity == 3
    }

    def "Should update size"() {
        given:
            int minSize = 1
            int maxSize = 5
            int desiredCapacity = 3

            UpdateAutoScalingGroupRequest request
            UpdateAutoScalingGroupResult result = Mock()
            client.updateAutoScalingGroup(_ as UpdateAutoScalingGroupRequest) >> {
                arguments -> request = arguments[0]
                return result
            }

        when:
            facade.updateSize(groupName, minSize, maxSize, desiredCapacity)

        then:
            request.getAutoScalingGroupName() == groupName
            request.minSize == minSize
            request.maxSize == maxSize
            request.desiredCapacity == desiredCapacity
    }

    def "Should terminate instance"() {
        given:
            String instanceId = "i-123"

            TerminateInstanceInAutoScalingGroupRequest request
            TerminateInstanceInAutoScalingGroupResult result = Mock()
            client.terminateInstanceInAutoScalingGroup(_ as TerminateInstanceInAutoScalingGroupRequest) >> {
                arguments -> request = arguments[0]
                return result
            }

        when:
            facade.terminateInstance(instanceId, false)

        then:
            request.getInstanceId() == instanceId
    }

    def "Should return instance ids from group"() {
        given:
            Instance instance1 = new Instance().withInstanceId("i-123")
            Instance instance2 = new Instance().withInstanceId("i-456")
            Instance instance3 = new Instance().withInstanceId("i-789")

            awsAutoScalingGroup.instances >> [instance1, instance2, instance3]
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            List<String> instanceIds = facade.instancesId(groupName)

        then:
            instanceIds.size() == 3
            instanceIds.get(0) == "i-123"
            instanceIds.get(1) == "i-456"
            instanceIds.get(2) == "i-789"
    }

    def "Should return load balancer names"() {
        given:
            awsAutoScalingGroup.loadBalancerNames >> ["lb-1", "lb-2"]
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            List<String> loadBalancersNames = facade.loadBalancersNames(groupName)

        then:
            loadBalancersNames.size() == 2
            loadBalancersNames.get(0) == "lb-1"
            loadBalancersNames.get(1) == "lb-2"
    }

    def "Should return target group names"() {
        given:
            awsAutoScalingGroup.targetGroupARNs >> ["tg-1", "tg-2"]
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            List<String> targetGroupsNames = facade.targetGroupsNames(groupName)

        then:
            targetGroupsNames.size() == 2
            targetGroupsNames.get(0) == "tg-1"
            targetGroupsNames.get(1) == "tg-2"
    }

    def "Should return launch template id"() {
        given:
            LaunchTemplateSpecification launchTemplateSpecification = Mock()
            launchTemplateSpecification.launchTemplateId >> "lt-123"

            awsAutoScalingGroup.launchTemplate >> launchTemplateSpecification
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            String launchTemplateId = facade.launchTemplateId(groupName)

        then:
            launchTemplateId == "lt-123"
    }

    def "Should return launch template version"() {
        given:
            LaunchTemplateSpecification launchTemplateSpecification = Mock()
            launchTemplateSpecification.version >> "1"

            awsAutoScalingGroup.launchTemplate >> launchTemplateSpecification
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            String launchTemplateVersion = facade.launchTemplateVersion(groupName)

        then:
            launchTemplateVersion == "1"
    }

    def "Should return use launch template from mixed instances policy when use mixed instances policy"() {
        given:
            LaunchTemplateSpecification launchTemplateSpecification = Mock()
            launchTemplateSpecification.launchTemplateId >> "lt-123"

            LaunchTemplate launchTemplate = Mock()
            launchTemplate.launchTemplateSpecification >> launchTemplateSpecification

            MixedInstancesPolicy mixedInstancesPolicy = Mock()
            mixedInstancesPolicy.launchTemplate >> launchTemplate

            awsAutoScalingGroup.launchTemplate >> null
            awsAutoScalingGroup.mixedInstancesPolicy >> mixedInstancesPolicy
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            String launchTemplateId = facade.launchTemplateId(groupName)

        then:
            launchTemplateId == "lt-123"
    }

    def "Should throw exception when not found any launch template"() {
        given:
            awsAutoScalingGroup.launchTemplate >> null
            awsAutoScalingGroup.mixedInstancesPolicy >> null
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            facade.launchTemplateId(groupName)

        then:
            DomainException e = thrown()
            e.message == "Not found Launch Template on Auto Scaling Group"
    }

    def "Should return tags with propagated at launch"() {
        given:
            TagDescription tag1 = new TagDescription().withKey("tag1").withValue("value1").withPropagateAtLaunch(true)
            TagDescription tag2 = new TagDescription().withKey("tag2").withValue("value2").withPropagateAtLaunch(false)
            TagDescription tag3 = new TagDescription().withKey("tag3").withValue("value3").withPropagateAtLaunch(true)

            awsAutoScalingGroup.tags >> [tag1, tag2, tag3]
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            Map<String, String> tags = facade.tags(groupName)

        then:
            tags.size() == 2
            tags.get("tag1") == "value1"
            tags.get("tag3") == "value3"
    }

    def "Should return tags without propagated at launch"() {
        given:
            TagDescription tag1 = new TagDescription().withKey("tag1").withValue("value1").withPropagateAtLaunch(true)
            TagDescription tag2 = new TagDescription().withKey("tag2").withValue("value2").withPropagateAtLaunch(false)
            TagDescription tag3 = new TagDescription().withKey("tag3").withValue("value3").withPropagateAtLaunch(true)

            awsAutoScalingGroup.tags >> [tag1, tag2, tag3]
            describeAutoScalingGroupsResult.getAutoScalingGroups() >> [awsAutoScalingGroup]
            client.describeAutoScalingGroups(_) >> describeAutoScalingGroupsResult

        when:
            Map<String, String> tags = facade.tags(groupName, false)

        then:
            tags.size() == 1
            tags.get("tag2") == "value2"
    }
}
