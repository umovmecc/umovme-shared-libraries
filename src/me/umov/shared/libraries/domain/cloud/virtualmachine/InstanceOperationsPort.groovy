package me.umov.shared.libraries.domain.cloud.virtualmachine

interface InstanceOperationsPort {

    void start(String instanceId)

    void stop(String instanceId)

    void restart(String instanceId)

    void terminate(String instanceId)

}
