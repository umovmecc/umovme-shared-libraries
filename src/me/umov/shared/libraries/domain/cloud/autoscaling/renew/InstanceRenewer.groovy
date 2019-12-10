package me.umov.shared.libraries.domain.cloud.autoscaling.renew

import com.google.inject.Inject
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroup
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupFactory
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupSize
import me.umov.shared.libraries.domain.cloud.virtualmachine.Instance
import me.umov.shared.libraries.domain.exception.DomainException
import me.umov.shared.libraries.domain.printer.Printer

import static me.umov.shared.libraries.domain.utils.Title.buildTitle

class InstanceRenewer implements InstanceRenewerPort {

    @Inject
    private AutoScalingGroupFactory factory

    @Inject
    private InstanceRenewValidator validator

    @Inject
    private Printer printer

    @Override
    void renewInstances(String groupName) throws DomainException {
        AutoScalingGroup autoScalingGroup = factory.getInstance(groupName)

        AutoScalingGroupSize originalGroupSize = autoScalingGroup.groupSize()
        List<Instance> originalInstances = autoScalingGroup.instances()
        validator.validate(originalGroupSize, originalInstances)

        logInitProcess(groupName, originalInstances)

        incrementGroupSizeByOne(autoScalingGroup)
        terminateOriginalInstances(autoScalingGroup, originalInstances)
        returnToOriginalGroupSize(autoScalingGroup, originalGroupSize)

        logEndProcess()
    }

    private void incrementGroupSizeByOne(AutoScalingGroup autoScalingGroup) {
        autoScalingGroup.incrementGroupSize(1)
        autoScalingGroup.waitForDesiredCapacityToBeOk()
    }

    private List<Instance> terminateOriginalInstances(AutoScalingGroup autoScalingGroup, List<Instance> originalInstances) {
        originalInstances.eachWithIndex { it, index ->
            this.printer.println(buildTitle("Initing step ${index+1} of ${originalInstances.size()} - Replace instance ${it.id()}"))
            boolean isTheLastInstance = it == originalInstances.last()

            if (isTheLastInstance) {
                AutoScalingGroupSize actualSize = autoScalingGroup.groupSize()
                AutoScalingGroupSize newSize = new AutoScalingGroupSize(actualSize.minSize - 1, actualSize.maxSize, actualSize.desiredCapacity)
                autoScalingGroup.updateSize(newSize)
            }

            terminateInstance(autoScalingGroup, it, isTheLastInstance)

            if (!isTheLastInstance) {
                autoScalingGroup.waitForDesiredCapacityToBeOk()
            }
        }
    }

    private void terminateInstance(AutoScalingGroup autoScalingGroup, Instance it, boolean isTheLastInstance) {
        try {
            autoScalingGroup.terminateInstance(it.id(), isTheLastInstance)
        } catch (DomainException e) {
            this.printer.println(e.getMessage())
        }
    }

    private returnToOriginalGroupSize(AutoScalingGroup autoScalingGroup, AutoScalingGroupSize originalGroupSize) {
        autoScalingGroup.updateSize(originalGroupSize)
        autoScalingGroup.waitForDesiredCapacityToBeOk()
    }

    private void logInitProcess(String groupName, List<Instance> originalInstances) {
        String instances = splitInstanceList(originalInstances)

        this.printer.printlnNewLineBefore(buildTitle("INIT RENEW AUTOSCALING GROUP INSTANCES"))
        this.printer.println("Starting renew instances of autoscaling group ${groupName}")
        this.printer.printlnNewLineAfter("There are ${originalInstances.size()} instance(s) to be replaced: ${instances}")
        this.printer.println(buildTitle("Incrementing group size by one"))
    }

    private void logEndProcess() {
        this.printer.println("Renew process finished with success")
        this.printer.printlnNewLineBeforeAndAfter(buildTitle("END RENEW AUTOSCALING GROUP INSTANCES"))
    }

    //this is necessary because Jenkins don't print all elements when use list.toString or list.join
    private String splitInstanceList(List<Instance> instances) {
        String result = ""

        instances.each {
            result += it.id()
            if (!(it == instances.last())) {
                result += ", "
            }
        }

        return result
    }

}
