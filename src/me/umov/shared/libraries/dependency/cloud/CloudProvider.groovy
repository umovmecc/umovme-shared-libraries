package me.umov.shared.libraries.dependency.cloud

import me.umov.shared.libraries.dependency.Dependency

interface CloudProvider extends Dependency {

    String region()

}
