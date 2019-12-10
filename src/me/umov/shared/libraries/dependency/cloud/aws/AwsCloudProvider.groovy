package me.umov.shared.libraries.dependency.cloud.aws

import me.umov.shared.libraries.dependency.cloud.CloudProvider
import me.umov.shared.libraries.infrastructure.cloud.aws.credentials.AwsKeys

class AwsCloudProvider implements CloudProvider {

    private String region
    private AwsKeys keys

    AwsCloudProvider(String region, AwsKeys keys) {
        this.keys = keys
        this.region = region
    }

    @Override
    String region() {
        return this.region
    }

    AwsKeys getKeys() {
        return keys
    }

}
