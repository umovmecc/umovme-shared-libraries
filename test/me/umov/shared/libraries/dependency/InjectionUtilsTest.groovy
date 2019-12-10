package me.umov.shared.libraries.dependency

import com.google.inject.Injector
import me.umov.shared.libraries.dependency.util.InjectionUtils
import spock.lang.Specification

class InjectionUtilsTest extends Specification {

    Injector injector = Mock()

    def "Should configure injection and get instance"() {
        given:
            Object objectFromPool = new Object()
            injector.getInstance(Object.class) >> objectFromPool

        when:
            InjectionUtils.configure(injector)

        then:
            InjectionUtils.getInstance(Object.class) == objectFromPool
    }
}
