package me.umov.shared.libraries.domain.cloud.virtualmachine

interface Instance {

    String id()

    void start()

    void stop()

    void restart()

    void terminate()

    InstanceState getState()

    String subnetId()

    void waitForStatus(InstanceState desiredState)

}
