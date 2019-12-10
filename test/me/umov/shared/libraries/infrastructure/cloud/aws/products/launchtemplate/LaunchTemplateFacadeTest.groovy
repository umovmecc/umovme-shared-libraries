package me.umov.shared.libraries.infrastructure.cloud.aws.products.launchtemplate

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.DescribeLaunchTemplateVersionsRequest
import com.amazonaws.services.ec2.model.DescribeLaunchTemplateVersionsResult
import com.amazonaws.services.ec2.model.LaunchTemplateVersion
import com.amazonaws.services.ec2.model.ResponseLaunchTemplateData
import spock.lang.Specification

class LaunchTemplateFacadeTest extends Specification {

    AmazonEC2 client = Mock()
    AmazonEC2ClientBuilder clientBuilder = GroovyMock()
    DescribeLaunchTemplateVersionsResult describeLaunchTemplateVersionsResult = Mock()

    String region = "us-east-2"
    String accessKey = "123"
    String secretAccessKey = "456"

    LaunchTemplateFacade facade = new LaunchTemplateFacade(region: region, accessKey: accessKey, secretAccessKey: secretAccessKey)

    def "setup"() {
        GroovyMock(AmazonEC2ClientBuilder, global: true)

        AmazonEC2ClientBuilder.standard() >> clientBuilder
        clientBuilder.withRegion(region) >> clientBuilder
        clientBuilder.withCredentials(_) >> clientBuilder
        clientBuilder.build() >> client
    }

    def "Should return user data"() {
        given:
            String userDataBase64 = "dXNlckRhdGE="
            ResponseLaunchTemplateData templateData = new ResponseLaunchTemplateData().withUserData(userDataBase64)
            LaunchTemplateVersion launchTemplateVersion = new LaunchTemplateVersion().withLaunchTemplateData(templateData)
            describeLaunchTemplateVersionsResult.getLaunchTemplateVersions() >> [launchTemplateVersion]

            DescribeLaunchTemplateVersionsRequest request
            client.describeLaunchTemplateVersions(_ as DescribeLaunchTemplateVersionsRequest) >> {
                arguments -> request = arguments.first()
                return describeLaunchTemplateVersionsResult
            }

        when:
            String userData = facade.getUserData("lt-123", "1")

        then:
            userData == "userData"

            request.launchTemplateId == "lt-123"
            request.versions.first() == "1"
    }
}
