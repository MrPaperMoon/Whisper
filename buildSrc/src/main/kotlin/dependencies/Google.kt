@file:Suppress("unused", "PackageDirectoryMismatch")

interface Google {

    interface Android {
        enum class Material : Dependency {
            Material {
                override val project: String get() = "material"
            },
            ;

            override val group: String get() = "com.google.android.material"
            override val version: String get() = Version.MATERIAL
        }
    }

    enum class PlayServices : Dependency {
        PlayServicesNearby {
            override val project: String get() = "play-services-nearby"
        },
        ;

        override val group: String get() = "com.google.android.gms"
        override val version: String get() = Version.PLAY_SERVICES
    }
}