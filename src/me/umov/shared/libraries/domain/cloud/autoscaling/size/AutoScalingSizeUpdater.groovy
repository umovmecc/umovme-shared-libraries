package me.umov.shared.libraries.domain.cloud.autoscaling.size

import com.google.inject.Inject
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroup
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupFactory
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupSize
import me.umov.shared.libraries.domain.printer.Printer

import static me.umov.shared.libraries.domain.utils.Title.buildTitle

class AutoScalingSizeUpdater implements AutoScalingSizeUpdaterPort {

    public static final String TITLE = "SET AUTO SCALING GROUP SIZE"

    @Inject
    private AutoScalingGroupFactory factory

    @Inject
    private Printer printer

    void updateAutoScalingGroupSize(String groupName, AutoScalingGroupSize newGroupSize) {
        updateAutoScalingGroupSizeWithWait(groupName, newGroupSize, false)
    }

    void updateAutoScalingGroupSizeAndWait(String groupName, AutoScalingGroupSize newGroupSize) {
        updateAutoScalingGroupSizeWithWait(groupName, newGroupSize, true)
    }

    private void updateAutoScalingGroupSizeWithWait(String groupName, AutoScalingGroupSize newGroupSize, boolean waitInstanceHealth) {
        printer.printlnNewLineBefore(buildTitle("INIT " + TITLE))
        printer.printlnNewLineBeforeAndAfter("Changing the size of Auto Scaling Group to: ${newGroupSize.toString()}")

        AutoScalingGroup group = factory.getInstance(groupName)
        group.updateSize(newGroupSize)

        if (waitInstanceHealth) {
            group.waitForDesiredCapacityToBeOk()
        }

        printer.printlnNewLineAfter(buildTitle("END " + TITLE))
    }

}
