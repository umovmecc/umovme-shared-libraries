package me.umov.shared.libraries.infrastructure.cloud.aws.products.launchtemplate

import static me.umov.shared.libraries.dependency.util.InjectionUtils.getInstance

class LaunchTemplate {

    String id
    String version
    private LaunchTemplateFacade facade

    LaunchTemplate(String id, String version) {
        this.id = id
        this.version = version
        this.facade = getInstance(LaunchTemplateFacade.class)
    }

    String getUserData() {
        return this.facade.getUserData(this.id, this.version)
    }

}
