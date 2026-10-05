@file:Suppress("unused", "PackageDirectoryMismatch")

interface Android {
    object Application : Plugin {
        override val project: String
            get() = "com.android.application"
        override val version: String
            get() = Version.AGP
    }

    object Library : Plugin {
        override val project: String
            get() = "com.android.library"
        override val version: String
            get() = Version.AGP
    }

    enum class Tools : Dependency {
        DesugarJdkLibs {
            override val project: String get() = "desugar_jdk_libs"
        },
        ;

        override val group: String get() = "com.android.tools"
        override val version: String get() = Version.DESUGAR_JDK_LIBS
    }
}