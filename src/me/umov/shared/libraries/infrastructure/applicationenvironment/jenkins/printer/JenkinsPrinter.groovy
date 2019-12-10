package me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins.printer

import me.umov.shared.libraries.domain.printer.ExternalPrinter
import me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins.JenkinsWrapper

class JenkinsPrinter implements ExternalPrinter {

    void println(String message) {
        JenkinsWrapper.println(message)
    }

}
