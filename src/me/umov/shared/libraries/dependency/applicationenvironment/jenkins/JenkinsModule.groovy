package me.umov.shared.libraries.dependency.applicationenvironment.jenkins

import com.cloudbees.groovy.cps.NonCPS
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import me.umov.shared.libraries.domain.printer.ExternalPrinter
import me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins.printer.JenkinsPrinter

class JenkinsModule extends AbstractModule {

    @NonCPS
    @Override
    protected void configure() {
        bind(ExternalPrinter.class).to(JenkinsPrinter.class)

        bindConstant().annotatedWith(Names.named("aws-credentials-id")).to("http://repo.umov.me/artifactory/umovme-releases/me/umov");
    }

}
