import me.umov.shared.libraries.dependency.util.AwsWithJenkins
import me.umov.shared.libraries.domain.printer.PrinterPort
import me.umov.shared.libraries.domain.utils.Title

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance
import static me.umov.shared.libraries.domain.utils.Title.buildTitle

def call(String title) {
    AwsWithJenkins.configure(this, "")

    PrinterPort printer = getInstance(PrinterPort.class)

    printer.printlnNewLineBeforeAndAfter(buildTitle(title))
}