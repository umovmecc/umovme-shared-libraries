package me.umov.shared.libraries.infrastructure.cloud.aws.products.launchtemplate

import me.umov.shared.libraries.dependency.util.InjectionUtils
import spock.lang.Specification

class LaunchTemplateTest extends Specification {

    LaunchTemplateFacade facade = Mock()

    LaunchTemplate launchTemplate

    def "setup"() {
        GroovyMock(InjectionUtils, global: true)
        InjectionUtils.getInstance(LaunchTemplateFacade.class) >> facade

        launchTemplate = new LaunchTemplate("lt-123", "1")
    }

    def "Should return user data"() {
        given:
            facade.getUserData("lt-123", "1") >> "userData"

        when:
            String userData = launchTemplate.getUserData()

        then:
            userData == "userData"
    }
}
