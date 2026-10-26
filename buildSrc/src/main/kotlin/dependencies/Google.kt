@file:Suppress("unused", "PackageDirectoryMismatch")

interface Google {

	object Ksp : Plugin {
		override val project: String
			get() = "com.google.devtools.ksp"
		override val version: String
			get() = Version.KSP
	}

	object HiltPlugin : Plugin {
		override val project: String get() = "com.google.dagger.hilt.android"
		override val version: String get() = Version.HILT
	}

	enum class Hilt : Dependency {
		Android {
			override val project: String get() = "hilt-android"
		},
		Compiler {
			override val project: String get() = "hilt-compiler"
		}
		;

		override val group: String get() = "com.google.dagger"
		override val version: String get() = Version.HILT
	}

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