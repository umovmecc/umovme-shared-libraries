import me.umov.shared.libraries.dependency.util.AwsWithJenkins
import me.umov.shared.libraries.domain.cloud.autoscaling.AutoScalingGroupSize
import me.umov.shared.libraries.domain.cloud.autoscaling.size.AutoScalingSizeUpdaterPort

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance

def call(String groupName, String region, Integer minSize, Integer maxSize, Integer desiredCapacity) {
    AwsWithJenkins.configure(this, region)

    AutoScalingGroupSize groupSize = new AutoScalingGroupSize(minSize, maxSize, desiredCapacity)

    AutoScalingSizeUpdaterPort autoScalingSizeUpdater = getInstance(AutoScalingSizeUpdaterPort.class)
    autoScalingSizeUpdater.updateAutoScalingGroupSize(groupName, groupSize)
}