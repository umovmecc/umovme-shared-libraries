package me.umov.shared.libraries.infrastructure.cloud.aws.products.ec2

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult
import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.InstanceNetworkInterface
import com.amazonaws.services.ec2.model.InstanceState
import com.amazonaws.services.ec2.model.InstanceStatus
import com.amazonaws.services.ec2.model.InstanceStatusDetails
import com.amazonaws.services.ec2.model.InstanceStatusSummary
import com.amazonaws.services.ec2.model.Reservation
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.ec2.model.RunInstancesResult
import com.amazonaws.services.ec2.model.StartInstancesRequest
import com.amazonaws.services.ec2.model.StopInstancesRequest
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder
import spock.lang.Specification

class EC2FacadeTest extends Specification {

    AmazonEC2 client = Mock()
    AmazonEC2ClientBuilder clientBuilder = GroovyMock()
    DescribeInstanceStatusResult describeInstanceStatusResult = Mock()
    DescribeInstancesResult describeInstancesResult = Mock()
    RunInstancesResult runInstancesResult = Mock()

    String region = "us-east-2"
    String accessKey = "123"
    String secretAccessKey = "456"

    EC2Facade facade = new EC2Facade(region: region, accessKey: accessKey, secretAccessKey: secretAccessKey)

    def "setup"() {
        GroovyMock(AmazonEC2ClientBuilder, global: true)

        AmazonEC2ClientBuilder.standard() >> clientBuilder
        clientBuilder.withRegion(region) >> clientBuilder
        clientBuilder.withCredentials(_) >> clientBuilder
        clientBuilder.build() >> client
    }

    def "Should start instance"() {
        given:
            StartInstancesRequest request
            client.startInstances(_ as StartInstancesRequest) >> {
                arguments -> request = arguments.first()
                return
            }

        when:
            facade.start("i-123")

        then:
            request.instanceIds.size() == 1
            request.instanceIds.first() == "i-123"
    }

    def "Should stop instance"() {
        given:
            StopInstancesRequest request
            client.stopInstances(_ as StopInstancesRequest) >> {
                arguments -> request = arguments.first()
                return
            }

        when:
            facade.stop("i-123")

        then:
            request.instanceIds.size() == 1
            request.instanceIds.first() == "i-123"
    }

    def "Should terminate instance"() {
        given:
            TerminateInstancesRequest request
            client.terminateInstances(_ as TerminateInstancesRequest) >> {
                arguments -> request = arguments.first()
                return
            }

        when:
            facade.terminate("i-123")

        then:
            request.instanceIds.size() == 1
            request.instanceIds.first() == "i-123"
    }

    def "Should return state"() {
        given:
            InstanceState instanceState = new InstanceState().withName("RUNNING")
            InstanceStatus instanceStatus = new InstanceStatus().withInstanceState(instanceState)
            describeInstanceStatusResult.instanceStatuses >> [instanceStatus]

            DescribeInstanceStatusRequest request
            client.describeInstanceStatus(_ as DescribeInstanceStatusRequest) >> {
                arguments -> request = arguments.first()
                return describeInstanceStatusResult
            }

        when:
            String state = facade.state("i-123")

        then:
            state == "RUNNING"

            request.instanceIds.size() == 1
            request.instanceIds.first() == "i-123"
            request.includeAllInstances
    }

    def "Should return state null when do not have instance state"() {
        given:
            describeInstanceStatusResult.instanceStatuses >> []

            client.describeInstanceStatus(_ as DescribeInstanceStatusRequest) >> describeInstanceStatusResult

        when:
            String state = facade.state("i-123")

        then:
            state == null
    }

    def "Should return status"() {
        given:
            InstanceStatusDetails instanceStatusDetails = new InstanceStatusDetails().withStatus("passed")
            InstanceStatusSummary systemStatus = new InstanceStatusSummary().withDetails(instanceStatusDetails)
            InstanceStatus instanceStatus = new InstanceStatus().withInstanceStatus(systemStatus)
            describeInstanceStatusResult.instanceStatuses >> [instanceStatus]

            DescribeInstanceStatusRequest request
            client.describeInstanceStatus(_ as DescribeInstanceStatusRequest) >> {
                arguments -> request = arguments.first()
                return describeInstanceStatusResult
            }

        when:
            String status = facade.status("i-123")

        then:
            status == "passed"

            request.instanceIds.size() == 1
            request.instanceIds.first() == "i-123"
            request.includeAllInstances
    }

    def "Should return status null when do not have instance status"() {
        given:
            describeInstanceStatusResult.instanceStatuses >> []

            client.describeInstanceStatus(_ as DescribeInstanceStatusRequest) >> describeInstanceStatusResult

        when:
            String status = facade.status("i-123")

        then:
            status == null
    }

    def "Should return status null when do not have details"() {
        given:
            InstanceStatusSummary systemStatus = new InstanceStatusSummary()
            InstanceStatus instanceStatus = new InstanceStatus().withInstanceStatus(systemStatus)
            describeInstanceStatusResult.instanceStatuses >> [instanceStatus]

            client.describeInstanceStatus(_ as DescribeInstanceStatusRequest) >> describeInstanceStatusResult

        when:
            String status = facade.status("i-123")

        then:
            status == null
    }

    def "Should return subnet id"() {
        given:
            InstanceNetworkInterface networkInterface = new InstanceNetworkInterface().withSubnetId("subnet-123")
            Instance instance = new Instance().withNetworkInterfaces(networkInterface)
            Reservation reservation = new Reservation().withInstances(instance)
            describeInstancesResult.reservations >> [reservation]

            DescribeInstancesRequest request
            client.describeInstances(_ as DescribeInstancesRequest) >> {
                arguments -> request = arguments.first()
                return describeInstancesResult
            }

        when:
            String subnetId = facade.subnetId("i-123")

        then:
            subnetId == "subnet-123"

            request.instanceIds.size() == 1
            request.instanceIds.first() == "i-123"
    }

    def "Should launch instance"() {
        given:
            Instance instance = new Instance().withInstanceId("i-123")
            Reservation reservation = new Reservation().withInstances(instance)
            runInstancesResult.getReservation() >> reservation

            RunInstancesRequest request
            client.runInstances(_ as RunInstancesRequest) >> {
                arguments -> request = arguments.first()
                return runInstancesResult
            }

        when:
            Map<String, String> tags = ["name": "test", "env": "prod"]
            String instanceId = facade.launchInstance("subnet-123", "lt-123", "1", "userData", tags)

        then:
            instanceId == "i-123"

            request.launchTemplate.launchTemplateId == "lt-123"
            request.launchTemplate.version == "1"

            request.networkInterfaces.first().deviceIndex == 0
            request.networkInterfaces.first().subnetId == "subnet-123"
            request.networkInterfaces.first().associatePublicIpAddress

            request.tagSpecifications.first().tags.get(0).key == "name"
            request.tagSpecifications.first().tags.get(0).value == "test"
            request.tagSpecifications.first().tags.get(1).key == "env"
            request.tagSpecifications.first().tags.get(1).value == "prod"
    }

}
