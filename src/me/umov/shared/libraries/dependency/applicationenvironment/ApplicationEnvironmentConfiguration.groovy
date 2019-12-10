package me.umov.shared.libraries.dependency.applicationenvironment

import com.google.inject.AbstractModule
import me.umov.shared.libraries.dependency.applicationenvironment.jenkins.JenkinsApplicationEnvironment
import me.umov.shared.libraries.dependency.applicationenvironment.jenkins.JenkinsModule

class ApplicationEnvironmentConfiguration {

    AbstractModule buildConfiguration(ApplicationEnvironment applicationEnvironment) {
        if (applicationEnvironment instanceof JenkinsApplicationEnvironment) {
            return new JenkinsModule()
        }

        throw new IllegalArgumentException("Application environment is invalid")
    }

}
