package me.umov.shared.libraries.domain.cloud.virtualmachine

import com.google.inject.Inject
import me.umov.shared.libraries.domain.printer.Printer

import static me.umov.shared.libraries.domain.utils.Title.buildTitle

class InstanceOperations implements InstanceOperationsPort {

    @Inject
    private InstanceFactory factory

    @Inject
    private Printer printer

    @Override
    void start(String instanceId) {
        this.printer.printlnNewLineBefore(buildTitle("INIT START INSTANCE"))

        factory.getInstance(instanceId).start()

        this.printer.printlnNewLineAfter(buildTitle("END START INSTANCE"))
    }

    @Override
    void stop(String instanceId) {
        this.printer.printlnNewLineBefore(buildTitle("INIT STOP INSTANCE"))

        factory.getInstance(instanceId).stop()

        this.printer.printlnNewLineAfter(buildTitle("END STOP INSTANCE"))
    }

    @Override
    void restart(String instanceId) {
        this.printer.printlnNewLineBefore(buildTitle("INIT RESTART INSTANCE"))

        factory.getInstance(instanceId).restart()

        this.printer.printlnNewLineAfter(buildTitle("END RESTART INSTANCE"))
    }

    @Override
    void terminate(String instanceId) {
        this.printer.printlnNewLineBefore(buildTitle("INIT TERMINATE INSTANCE"))

        factory.getInstance(instanceId).terminate()

        this.printer.printlnNewLineAfter(buildTitle("END TERMINATE INSTANCE"))
    }

}
