package me.umov.shared.libraries.infrastructure.applicationenvironment.jenkins

class FakeJenkinsContext {
    //This class is required because it is not possible to know the type of the Jenkins context
    //and one type is needed to create Mock

    void input(String message) {}
    void error(String message) {}
    void println(String message) {}

}