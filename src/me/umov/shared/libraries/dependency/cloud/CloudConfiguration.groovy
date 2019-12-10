package me.umov.shared.libraries.dependency.cloud

import com.google.inject.AbstractModule
import me.umov.shared.libraries.dependency.cloud.aws.AwsCloudProvider
import me.umov.shared.libraries.dependency.cloud.aws.AwsModule

class CloudConfiguration {

    AbstractModule buildConfiguration(CloudProvider cloudProvider) {
        if (cloudProvider instanceof AwsCloudProvider) {
            return new AwsModule(cloudProvider.region(), cloudProvider.keys)
        }

        throw new IllegalArgumentException("Cloud provider is invalid")
    }

}
