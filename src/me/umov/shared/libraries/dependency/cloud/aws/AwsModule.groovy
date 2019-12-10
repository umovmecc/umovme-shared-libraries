package me.umov.shared.libraries.dependency.cloud.aws

import com.cloudbees.groovy.cps.NonCPS
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupFactory
import me.umov.shared.libraries.domain.cloud.virtualmachine.InstanceFactory
import me.umov.shared.libraries.infrastructure.cloud.aws.credentials.AwsKeys
import me.umov.shared.libraries.infrastructure.cloud.aws.products.autoscaling.AwsAutoScalingGroupFactory
import me.umov.shared.libraries.infrastructure.cloud.aws.products.ec2.AwsInstanceFactory

class AwsModule extends AbstractModule {

    private String region
    private AwsKeys keys

    AwsModule(String region, AwsKeys keys) {
        this.region = region
        this.keys = keys
    }

    @NonCPS
    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("region")).to(region)
        bindConstant().annotatedWith(Names.named("accessKey")).to(keys.accessKey)
        bindConstant().annotatedWith(Names.named("secretAccessKey")).to(keys.secretAccessKey)

        bind(AutoScalingGroupFactory.class).to(AwsAutoScalingGroupFactory.class)
        bind(InstanceFactory.class).to(AwsInstanceFactory.class)
    }

}
