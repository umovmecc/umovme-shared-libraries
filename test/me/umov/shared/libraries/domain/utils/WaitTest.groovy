package me.umov.shared.libraries.domain.utils

import me.umov.shared.libraries.domain.exception.DomainException
import me.umov.shared.libraries.domain.printer.Printer
import spock.lang.Specification

class WaitTest extends Specification {

    Sleep sleepFunction = Mock()
    Printer printer = Mock()
    int counter = 0

    Wait wait = new Wait(sleepFunction: sleepFunction, printer: printer)

    def "Should sleep until conditional evaluates to true"() {
        given:
            Closure<Boolean> conditionToStop = { -> getNewCounter() == 5 }
            Closure<String> intervalMessage = { -> ""}
            Closure<String> successMessage = { -> ""}
            Closure<String> timeoutMessage = { -> "" }

        when:
            wait.until(conditionToStop, intervalMessage, successMessage, timeoutMessage)

        then:
            5 * sleepFunction.sleep(5)
    }

    def "Should print interval message when counter equals 1"() {
        given:
            Closure<Boolean> conditionToStop = { -> getNewCounter() == 2 }
            Closure<String> intervalMessage = { -> "Interval message. Counter: ${this.counter}"}
            Closure<String> successMessage = { -> ""}
            Closure<String> timeoutMessage = { -> "" }

        when:
            wait.until(conditionToStop, intervalMessage, successMessage, timeoutMessage)

        then:
            1 * printer.println("Interval message. Counter: 1")
    }

    def "Should print interval message each X loops where X is the sleep time"() {
        given:
            Closure<Boolean> conditionToStop = { -> getNewCounter() == 11 }
            Closure<String> intervalMessage = { -> "Interval message. Counter: ${this.counter}"}
            Closure<String> successMessage = { -> ""}
            Closure<String> timeoutMessage = { -> "" }

        when:
            wait.until(conditionToStop, intervalMessage, successMessage, timeoutMessage, 5)

        then:
            1 * printer.println("Interval message. Counter: 1")
            1 * printer.println("Interval message. Counter: 5")
            1 * printer.println("Interval message. Counter: 10")
    }

    def "Should print timeout message when conditional don't evaluates to true in determined time"() {
        given:
            Closure<Boolean> conditionToStop = { -> getNewCounter() == 13 }
            Closure<String> intervalMessage = { -> ""}
            Closure<String> successMessage = { -> ""}
            Closure<String> timeoutMessage = { -> "Timeout after 60 seconds" }

        when:
            wait.until(conditionToStop, intervalMessage, successMessage, timeoutMessage, 5, 60)

        then:
            DomainException e = thrown()
            e.message == "Timeout after 60 seconds"
    }

    int getNewCounter() {
        return ++this.counter
    }
}
