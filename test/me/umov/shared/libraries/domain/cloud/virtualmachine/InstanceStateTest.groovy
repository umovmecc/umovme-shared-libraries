package me.umov.shared.libraries.domain.cloud.virtualmachine

import spock.lang.Specification

import static me.umov.shared.libraries.domain.cloud.virtualmachine.InstanceState.*

class InstanceStateTest extends Specification {

    def "Should return RUNNING state"() {
        expect:
            getInstance("running", "passed") == RUNNING
    }

    def "Should return STOPPED state"() {
        expect:
            getInstance("stopped", null) == STOPPED
    }

    def "Should return TERMINATED state"() {
        expect:
            getInstance("terminated", null) == TERMINATED
    }

    def "Should return PENDING state"() {
        expect:
            getInstance("pending", null) == PENDING
    }

    def "Should return SHUTTING_DOWN state"() {
        expect:
            getInstance("shutting-down", null) == SHUTTING_DOWN
    }

    def "Should return STOPPING state"() {
        expect:
            getInstance("stopping", null) == STOPPING
    }

}
