@file:Suppress("unused", "PackageDirectoryMismatch")

interface JetBrains {
    enum class Kotlin : Plugin {
        PluginCompose {
            override val project: String
                get() = "org.jetbrains.kotlin.plugin.compose"
        },
        Jvm {
            override val project: String
                get() = "org.jetbrains.kotlin.jvm"
        },
        PluginSerialization {
            override val project: String
                get() = "org.jetbrains.kotlin.plugin.serialization"
        },
        ;
        override val version: String
            get() = Version.KOTLIN
    }

    interface Kotlinx {
        enum class Coroutines : Dependency {
            Core {
                override val project: String get() = "kotlinx-coroutines-core"
            },
            Android {
                override val project: String get() = "kotlinx-coroutines-android"
            },
            ;

            override val group: String get() = "org.jetbrains.kotlinx"
            override val version: String get() = Version.COROUTINES
        }

        enum class Serialization : Dependency {
            Protobuf {
                override val project: String get() = "kotlinx-serialization-protobuf"
            },
            Json {
                override val project: String get() = "kotlinx-serialization-json"
            },
            Core {
                override val project: String get() = "kotlinx-serialization-core"
            },
            ;

            override val group: String get() = "org.jetbrains.kotlinx"
            override val version: String get() = Version.KOTLINX_SERIALIZATION
        }
    }
}