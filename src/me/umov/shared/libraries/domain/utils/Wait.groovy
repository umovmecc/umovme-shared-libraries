package me.umov.shared.libraries.domain.utils

import com.google.inject.Inject
import me.umov.shared.libraries.domain.exception.DomainException
import me.umov.shared.libraries.domain.printer.Printer

class Wait {

    @Inject
    private Sleep sleepFunction

    @Inject
    private Printer printer

    void until(Closure<Boolean> conditionToStop, Closure<String> intervalMessage, Closure<String> successMessage, Closure<String> timeoutMessage, int sleepSeconds = 5, int timeoutSeconds = 600) {
        int count = 0
        boolean shouldStop = false
        BigDecimal timeout = timeoutSeconds / sleepSeconds

        while (!shouldStop && count < timeout) {
            sleepFunction.sleep(sleepSeconds)

            shouldStop = conditionToStop.call()
            count++

            boolean isTimeToLog = (!shouldStop && count == 1) || count % sleepSeconds == 0

            if (isTimeToLog && intervalMessage != null) {
                this.printer.println(intervalMessage.call())
            }
        }

        if (shouldStop) {
            this.printer.printlnNewLineAfter(successMessage.call())
        } else {
            throw new DomainException(timeoutMessage.call())
        }
    }

}
