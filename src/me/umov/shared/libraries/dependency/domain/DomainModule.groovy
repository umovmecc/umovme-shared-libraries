package me.umov.shared.libraries.dependency.domain

import com.cloudbees.groovy.cps.NonCPS
import com.google.inject.AbstractModule
import me.umov.shared.libraries.domain.cloud.autoscaling.renew.InstanceRenewer
import me.umov.shared.libraries.domain.cloud.autoscaling.renew.InstanceRenewerPort
import me.umov.shared.libraries.domain.cloud.autoscaling.renew.InstanceRenewerWithStart
import me.umov.shared.libraries.domain.cloud.autoscaling.renew.InstanceRenewerWithStartPort
import me.umov.shared.libraries.domain.cloud.autoscaling.size.AutoScalingSizeUpdater
import me.umov.shared.libraries.domain.cloud.autoscaling.size.AutoScalingSizeUpdaterPort
import me.umov.shared.libraries.domain.cloud.virtualmachine.InstanceOperations
import me.umov.shared.libraries.domain.cloud.virtualmachine.InstanceOperationsPort
import me.umov.shared.libraries.domain.printer.Printer
import me.umov.shared.libraries.domain.printer.PrinterPort
import me.umov.shared.libraries.domain.service.ServiceStatusVerifier
import me.umov.shared.libraries.domain.service.ServiceStatusVerifierPort

class DomainModule extends AbstractModule {

    @NonCPS
    @Override
    protected void configure() {
        bind(InstanceRenewerPort.class).to(InstanceRenewer.class)
        bind(InstanceRenewerWithStartPort.class).to(InstanceRenewerWithStart.class)
        bind(AutoScalingSizeUpdaterPort.class).to(AutoScalingSizeUpdater.class)
        bind(InstanceOperationsPort.class).to(InstanceOperations.class)
        bind(ServiceStatusVerifierPort.class).to(ServiceStatusVerifier.class)
        bind(PrinterPort.class).to(Printer.class)
    }

}
