package me.umov.shared.libraries.domain.cloud.virtualmachine


import me.umov.shared.libraries.domain.printer.Printer
import spock.lang.Specification

import static me.umov.shared.libraries.domain.utils.Title.buildTitle

class InstanceOperationsTest extends Specification {

    InstanceFactory factory = Mock()
    Printer printer = Mock()
    Instance instance = Mock()

    InstanceOperationsPort instanceOperations = new InstanceOperations(factory: factory, printer: printer)

    def "Should start instance"() {
        given:
            factory.getInstance("i-123") >> instance

        when:
            instanceOperations.start("i-123")

        then:
            1 * printer.printlnNewLineBefore(buildTitle("INIT START INSTANCE"))
            1 * instance.start()
            1 * printer.printlnNewLineAfter(buildTitle("END START INSTANCE"))
    }

    def "Should stop instance"() {
        given:
            factory.getInstance("i-123") >> instance

        when:
            instanceOperations.stop("i-123")

        then:
            1 * printer.printlnNewLineBefore(buildTitle("INIT STOP INSTANCE"))
            1 * instance.stop()
            1 * printer.printlnNewLineAfter(buildTitle("END STOP INSTANCE"))
    }

    def "Should restart instance"() {
        given:
            factory.getInstance("i-123") >> instance

        when:
            instanceOperations.restart("i-123")

        then:
            1 * printer.printlnNewLineBefore(buildTitle("INIT RESTART INSTANCE"))
            1 * instance.restart()
            1 * printer.printlnNewLineAfter(buildTitle("END RESTART INSTANCE"))
    }

    def "Should terminate instance"() {
        given:
            factory.getInstance("i-123") >> instance

        when:
            instanceOperations.terminate("i-123")

        then:
            1 * printer.printlnNewLineBefore(buildTitle("INIT TERMINATE INSTANCE"))
            1 * instance.terminate()
            1 * printer.printlnNewLineAfter(buildTitle("END TERMINATE INSTANCE"))
    }
}
