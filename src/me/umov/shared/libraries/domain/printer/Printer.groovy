package me.umov.shared.libraries.domain.printer

import com.google.inject.Inject

class Printer implements PrinterPort {

    @Inject
    private ExternalPrinter externalPrinter

    void println(String message) {
        externalPrinter.println(message)
    }

    void printlnNewLineBefore(String message) {
        lineBreaker()
        externalPrinter.println(message)
    }

    void printlnNewLineAfter(String message) {
        externalPrinter.println(message)
        lineBreaker()
    }

    void printlnNewLineBeforeAndAfter(String message) {
        printlnNewLineBefore(message)
        lineBreaker()
    }

    void lineBreaker() {
        externalPrinter.println("")
    }

}
