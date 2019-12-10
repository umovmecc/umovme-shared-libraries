import me.umov.shared.libraries.dependency.util.AwsWithJenkins
import me.umov.shared.libraries.domain.cloud.autoscaling.renew.InstanceRenewerPort
import me.umov.shared.libraries.domain.exception.DomainException

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance
import static me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins.JenkinsWrapper.error

def call(String groupName, String region) {
    AwsWithJenkins.configure(this, region)

    InstanceRenewerPort instanceRenewer = getInstance(InstanceRenewerPort.class)

    try {
        instanceRenewer.renewInstances(groupName)
    } catch(DomainException e) {
        error(e.message)
    }
}

