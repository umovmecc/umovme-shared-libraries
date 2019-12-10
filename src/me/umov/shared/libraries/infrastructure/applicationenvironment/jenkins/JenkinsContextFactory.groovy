package me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins

class JenkinsContextFactory {

    static def jenkinsContext

    static def configureJenkinsContext(def jenkinsContext) {
        this.jenkinsContext = jenkinsContext
    }

    static def getJenkinsContext() {
        return jenkinsContext
    }

}
