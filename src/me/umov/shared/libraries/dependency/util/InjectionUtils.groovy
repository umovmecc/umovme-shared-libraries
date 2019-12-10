package me.umov.shared.libraries.dependency.util

import com.cloudbees.groovy.cps.NonCPS
import com.google.inject.Injector

class InjectionUtils {

    private static Injector thisInjector

    static void configure(Injector injector) {
        thisInjector = injector
    }

    @NonCPS
    static <T> T getInstance(Class<T> clazz) {
        return thisInjector.getInstance(clazz)
    }

}
