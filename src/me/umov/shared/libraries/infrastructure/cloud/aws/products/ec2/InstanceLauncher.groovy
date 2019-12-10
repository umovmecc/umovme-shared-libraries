package me.umov.shared.libraries.infrastructure.cloud.aws.products.ec2


import com.google.inject.Inject
import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.infrastructure.cloud.aws.products.global.Tag
import me.umov.shared.libraries.infrastructure.cloud.aws.products.launchtemplate.LaunchTemplate

class InstanceLauncher {

    @Inject
    private EC2Facade facade

    @Inject
    private AwsInstanceFactory instanceFactory

    Instance launchInstance(String subnetId, LaunchTemplate launchTemplate, String userData, List<Tag> tags) {
        Map<String, String> tagsMap = tags.collectEntries{[(it.name):  it.value]}
        String instanceId = this.facade.launchInstance(subnetId, launchTemplate.getId(), launchTemplate.getVersion(), userData, tagsMap)

        return this.instanceFactory.getInstance(instanceId)
    }

}
