package me.umov.shared.libraries.domain.cloud.autoscaling

class AutoScalingGroupSize {

    Integer minSize
    Integer maxSize
    Integer desiredCapacity

    AutoScalingGroupSize(Integer minSize, Integer maxSize, Integer desiredCapacity) {
        this.minSize = minSize
        this.maxSize = maxSize
        this.desiredCapacity = desiredCapacity
    }

    AutoScalingGroupSize incrementSize(int quantity) {
        int newMinSize = this.minSize + quantity
        int newMaxSize = this.maxSize + quantity
        int newDesiredCapacity = this.desiredCapacity + quantity

        return new AutoScalingGroupSize(newMinSize, newMaxSize, newDesiredCapacity)
    }

    boolean allZero() {
        this.minSize == 0 || this.maxSize == 0 || this.desiredCapacity == 0
    }


    @Override
    String toString() {
        return "minSize = $minSize, maxSize = $maxSize, desiredCapacity = $desiredCapacity"
    }
}
