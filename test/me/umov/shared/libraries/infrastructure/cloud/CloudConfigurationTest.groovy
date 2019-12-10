package me.umov.shared.libraries.infrastructure.cloud

import com.google.inject.AbstractModule
import me.umov.shared.libraries.dependency.cloud.CloudConfiguration
import me.umov.shared.libraries.dependency.cloud.aws.AwsCloudProvider
import me.umov.shared.libraries.infrastructure.cloud.aws.credentials.AwsKeys
import me.umov.shared.libraries.dependency.cloud.aws.AwsModule
import spock.lang.Specification

class CloudConfigurationTest extends Specification {

    CloudConfiguration configuration = new CloudConfiguration()

    def "Should return Aws module when application environment is AWS"() {
        given:
            AwsKeys keys = new AwsKeys("123", "456")

        when:
            AbstractModule module = configuration.buildConfiguration(new AwsCloudProvider("us-east-2", keys))

        then:
            module instanceof AwsModule
    }

    def "Should throw exception when application environment is unknow"() {
        when:
            configuration.buildConfiguration(null)

        then:
            IllegalArgumentException e = thrown()
            e.message == "Cloud provider is invalid"
    }

}
