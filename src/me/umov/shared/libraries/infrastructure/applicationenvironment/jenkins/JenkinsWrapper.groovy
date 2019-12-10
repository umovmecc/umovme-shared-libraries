package me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins

import static JenkinsContextFactory.jenkinsContext

class JenkinsWrapper {

    static void input(String message) {
        getJenkinsContext().input(message)
    }

    static void error(String message) {
        getJenkinsContext().error(message)
    }

    static void println(String message) {
        getJenkinsContext().println(message)
    }

    static def sh(String command, boolean hideCommand = false) {
        String hideCommandSh = hideCommand ? "#!/bin/sh -e\n" : ""
        getJenkinsContext().sh (script: "$hideCommandSh $command", returnStatus: true)
    }

}
