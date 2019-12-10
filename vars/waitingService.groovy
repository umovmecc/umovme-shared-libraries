import me.umov.shared.libraries.dependency.util.AwsWithJenkins
import me.umov.shared.libraries.domain.service.ServiceStatusVerifierPort

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance

def call(String serviceEndpoint) {
    AwsWithJenkins.configure(this, "")

    ServiceStatusVerifierPort serviceStatusVerifier = getInstance(ServiceStatusVerifierPort.class)

    serviceStatusVerifier.verifyService(serviceEndpoint)
}