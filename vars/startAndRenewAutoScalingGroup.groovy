import me.umov.shared.libraries.dependency.util.AwsWithJenkins
import me.umov.shared.libraries.domain.cloud.autoscaling.renew.InstanceRenewerWithStartPort
import me.umov.shared.libraries.domain.exception.DomainException

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance
import static me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins.JenkinsWrapper.error

def call(String groupName, String region) {
    AwsWithJenkins.configure(this, region)

    InstanceRenewerWithStartPort instanceRenewer = getInstance(InstanceRenewerWithStartPort.class)

    try {
        instanceRenewer.startAndRenewInstances(groupName)
    } catch(DomainException e) {
        error(e.message)
    }
}

