package me.umov.shared.libraries.dependency

import com.google.inject.AbstractModule
import com.google.inject.Injector
import me.umov.shared.libraries.dependency.applicationenvironment.ApplicationEnvironment
import me.umov.shared.libraries.dependency.applicationenvironment.ApplicationEnvironmentConfiguration
import me.umov.shared.libraries.dependency.cloud.CloudConfiguration
import me.umov.shared.libraries.dependency.cloud.CloudProvider
import me.umov.shared.libraries.dependency.domain.DomainModule
import me.umov.shared.libraries.dependency.util.InjectionUtils

import static com.google.inject.Guice.createInjector

class DependenciesConfiguration {

    static void configureDependencies(Dependency ...dependencies) {
        List<AbstractModule> modules = [new DomainModule()]

        dependencies.each {
            if (it instanceof CloudProvider) {
                modules.add(new CloudConfiguration().buildConfiguration(it))
            }

            if (it instanceof ApplicationEnvironment) {
                modules.add(new ApplicationEnvironmentConfiguration().buildConfiguration(it))
            }
        }

        Injector injector = createInjector(modules)
        InjectionUtils.configure(injector)
    }

}

