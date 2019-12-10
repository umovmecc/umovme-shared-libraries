package me.umov.shared.libraries.domain.utils


import spock.lang.Specification

class SleepTest extends Specification {

    Sleep sleep = new Sleep()

    def "setup"() {
        GroovyMock(Thread, global: true)
    }

    def "Should sleep x seconds"() {
        when:
            sleep.sleep(10)

        then:
            1 * Thread.sleep(10000)
    }

}
