package me.umov.shared.libraries.domain.service

import com.google.inject.Inject
import me.umov.shared.libraries.domain.utils.Wait
import org.apache.http.HttpStatus

class ServiceStatusVerifier implements ServiceStatusVerifierPort {

    @Inject
    private Wait wait

    void verifyService(String serviceEndpoint) {
        Closure<Boolean> conditionToStop = { -> serviceIsUp(serviceEndpoint) }
        Closure<String> intervalMessage = { -> "Waiting until service to be ok."}
        Closure<String> successMessage = { -> "Service $serviceEndpoint is ok."}
        Closure<String> timeoutMessage = { -> "Timeout: Service $serviceEndpoint is not ok after determined time." }

        wait.until(conditionToStop, intervalMessage, successMessage, timeoutMessage, 15)
    }

    private boolean serviceIsUp(String serviceEndpoint) {
        try {
            HttpURLConnection connection = (HttpURLConnection) serviceEndpoint.toURL().openConnection()

            return connection.responseCode == HttpStatus.SC_OK
        }
        catch(e) {
            return false
        }
    }

}

