import me.umov.shared.libraries.dependency.util.AwsWithJenkins
import me.umov.shared.libraries.domain.cloud.virtualmachine.InstanceOperationsPort

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance

def call(String instanceId, String region) {
    AwsWithJenkins.configure(this, region)

    InstanceOperationsPort instanceOperations = getInstance(InstanceOperationsPort.class)

    instanceOperations.restart(instanceId)
}