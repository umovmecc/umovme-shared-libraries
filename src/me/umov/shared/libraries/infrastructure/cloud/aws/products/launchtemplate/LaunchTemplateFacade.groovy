package me.umov.shared.libraries.infrastructure.cloud.aws.products.launchtemplate

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.DescribeLaunchTemplateVersionsRequest
import com.amazonaws.services.ec2.model.DescribeLaunchTemplateVersionsResult
import com.google.inject.Inject
import com.google.inject.name.Named
import groovy.transform.PackageScope

@PackageScope
class LaunchTemplateFacade {

    @Inject
    @Named("region")
    private String region

    @Inject
    @Named("accessKey")
    private String accessKey

    @Inject
    @Named("secretAccessKey")
    private String secretAccessKey

    String getUserData(String launchTemplateId, String version) {
        DescribeLaunchTemplateVersionsRequest request = new DescribeLaunchTemplateVersionsRequest()
        request.withLaunchTemplateId(launchTemplateId).withVersions(version)

        DescribeLaunchTemplateVersionsResult result = client().describeLaunchTemplateVersions(request)
        String userDataBase64 = result.getLaunchTemplateVersions().first().getLaunchTemplateData().getUserData()

        return new String(userDataBase64.decodeBase64())
    }

    AmazonEC2 client() {
        AmazonEC2ClientBuilder.standard()
                .withRegion(this.region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.accessKey, this.secretAccessKey)))
                .build()
    }

}
