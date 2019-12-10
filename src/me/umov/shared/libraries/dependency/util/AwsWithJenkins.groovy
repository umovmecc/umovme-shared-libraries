package me.umov.shared.libraries.dependency.util

import me.umov.shared.libraries.dependency.applicationenvironment.jenkins.JenkinsApplicationEnvironment
import me.umov.shared.libraries.dependency.cloud.aws.AwsCloudProvider
import me.umov.shared.libraries.infrastructure.cloud.aws.credentials.AwsKeys

import static me.umov.shared.libraries.dependency.DependenciesConfiguration.configureDependencies
import static me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins.JenkinsContextFactory.configureJenkinsContext
import static me.umov.shared.libraries.infrastructure.cloud.aws.credentials.AwsCredentials.getCredentials

class AwsWithJenkins {

    static void configure(def jenkinsContext, String region) {
        configureJenkinsContext(jenkinsContext)

        AwsKeys awsKeys = getCredentials()
        configureDependencies(new AwsCloudProvider(region, awsKeys), new JenkinsApplicationEnvironment())
    }

}
