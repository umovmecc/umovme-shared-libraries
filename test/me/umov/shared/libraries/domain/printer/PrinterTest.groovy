package me.umov.shared.libraries.domain.printer

import spock.lang.Specification

class PrinterTest extends Specification {

    ExternalPrinter externalPrinter = Mock()

    Printer printer = new Printer(externalPrinter: externalPrinter)

    def "Should call println"() {
        when:
            printer.println("test")

        then:
            1 * externalPrinter.println("test")
    }

    def "Should call println and print new line before text"() {
        when:
            printer.printlnNewLineBefore("test")

        then:
            1 * externalPrinter.println("")
            1 * externalPrinter.println("test")
    }

    def "Should call println and print new line after text"() {
        when:
            printer.printlnNewLineAfter("test")

        then:
            1 * externalPrinter.println("test")
            1 * externalPrinter.println("")
    }

    def "Should call println and print new line before and after text"() {
        when:
            printer.printlnNewLineBeforeAndAfter("test")

        then:
            2 * externalPrinter.println("")
            1 * externalPrinter.println("test")
    }

    def "Should print empty string to line breaker"() {
        when:
            printer.lineBreaker()

        then:
            1 * externalPrinter.println("")
    }
}
