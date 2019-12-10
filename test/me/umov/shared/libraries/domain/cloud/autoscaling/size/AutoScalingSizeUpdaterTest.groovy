package me.umov.shared.libraries.domain.cloud.autoscaling.size

import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroup
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupFactory
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupSize
import me.umov.shared.libraries.domain.printer.Printer
import spock.lang.Specification

import static me.umov.shared.libraries.domain.utils.Title.buildTitle

class AutoScalingSizeUpdaterTest extends Specification {

    AutoScalingGroupFactory factory = Mock()
    Printer printer = Mock()
    AutoScalingGroup autoScalingGroup = Mock()

    AutoScalingSizeUpdaterPort autoScalingSizeUpdater = new AutoScalingSizeUpdater(factory: factory, printer: printer)

    def "Should update size of Auto Scaling Group"() {
        given:
            AutoScalingGroupSize groupSize = new AutoScalingGroupSize(1, 3, 2)
            factory.getInstance("group-test") >> autoScalingGroup

        when:
            autoScalingSizeUpdater.updateAutoScalingGroupSize("group-test", groupSize)

        then:
            1 * printer.printlnNewLineBefore(buildTitle("INIT SET AUTO SCALING GROUP SIZE"))
            1 * autoScalingGroup.updateSize(groupSize)
            1 * printer.printlnNewLineAfter(buildTitle("END SET AUTO SCALING GROUP SIZE"))
    }

    def "Should update size of Auto Scaling Group and wait instances to be healthy"() {
        given:
            AutoScalingGroupSize groupSize = new AutoScalingGroupSize(1, 3, 2)
            factory.getInstance("group-test") >> autoScalingGroup

        when:
            autoScalingSizeUpdater.updateAutoScalingGroupSizeAndWait("group-test", groupSize)

        then:
            1 * printer.printlnNewLineBefore(buildTitle("INIT SET AUTO SCALING GROUP SIZE"))
            1 * autoScalingGroup.updateSize(groupSize)
            1 * autoScalingGroup.waitForDesiredCapacityToBeOk()
            1 * printer.printlnNewLineAfter(buildTitle("END SET AUTO SCALING GROUP SIZE"))
    }
}