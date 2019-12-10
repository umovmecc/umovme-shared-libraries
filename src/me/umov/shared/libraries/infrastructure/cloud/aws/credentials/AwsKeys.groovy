package me.umov.shared.libraries.infrastructure.cloud.aws.credentials

import com.cloudbees.groovy.cps.NonCPS

class AwsKeys {

    private String accessKey
    private String secretAccessKey

    AwsKeys(String accessKey, String secretAccessKey) {
        this.accessKey = accessKey
        this.secretAccessKey = secretAccessKey
    }

    @NonCPS
    String getAccessKey() {
        return accessKey
    }

    @NonCPS
    String getSecretAccessKey() {
        return secretAccessKey
    }

}
