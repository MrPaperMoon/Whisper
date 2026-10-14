@file:Suppress("unused", "PackageDirectoryMismatch")

interface Google {

	enum class PlayServices : Dependency {
		PlayServicesNearby {
			override val project: String get() = "play-services-nearby"
			override val version: String get() = Version.PLAY_SERVICES_NEARBY
		},
		PlayServicesTasks {
			override val project: String get() = "play-services-tasks"
			override val version: String get() = Version.PLAY_SERVICES_TASKS
		}
		;

		override val group: String get() = "com.google.android.gms"
	}

	enum class Protobuf : Dependency {
		JavaLite {
			override val project: String get() = "protobuf-javalite"
		},
		KotlinLite {
			override val project: String get() = "protobuf-kotlin-lite"
		},
		;

		override val group: String get() = "com.google.protobuf"
		override val version: String get() = Version.PROTOBUF
	}
}