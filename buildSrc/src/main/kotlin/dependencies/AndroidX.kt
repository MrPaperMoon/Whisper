@file:Suppress("unused", "PackageDirectoryMismatch")

interface AndroidX {

	enum class Activity : Dependency {
		ActivityCompose {
			override val project: String get() = "activity-compose"
		},
		;

		override val group: String get() = "androidx.activity"
		override val version: String get() = Version.ACTIVITY
	}

	enum class AppCompat : Dependency {
		AppCompat {
			override val project: String get() = "appcompat"
		},
		;

		override val group: String get() = "androidx.appcompat"
		override val version: String get() = Version.APPCOMPAT
	}

	interface Compose : Dependency {

		enum class Ui : Compose {
			Ui {
				override val project: String get() = "ui"
			},
			UiGraphics {
				override val project: String get() = "ui-graphics"
			},
			UiTooling {
				override val project: String get() = "ui-tooling"
			},
			UiToolingPreview {
				override val project: String get() = "ui-tooling-preview"
			},

			UiTestJunit4 {
				override val project: String get() = "ui-test-junit4"
			},
			UiTestManifest {
				override val project: String get() = "ui-test-manifest"
			},
			;

			override val group: String get() = "androidx.compose.ui"
		}

		enum class Material : Compose {
			Material {
				override val project: String get() = "material"
			},
			;

			override val group: String get() = "androidx.compose.material"
		}

		enum class Material3 : Compose {
			Material3 {
				override val project: String get() = "material3"
			},
			;

			override val group: String get() = "androidx.compose.material3"
		}

		override val group: String get() = "androidx.compose"
		override val version: String get() = Version.COMPOSE_BOM

		companion object : Compose {
			override val project: String get() = "compose-bom"
		}
	}

	enum class Core : Dependency {
		CoreKtx {
			override val project: String get() = "core-ktx"
			override val version: String get() = Version.CORE_KTX
		},
		Splashscreen {
			override val project: String get() = "core-splashscreen"
			override val version: String get() = Version.CORE_SPLASH
		},
		;

		override val group: String get() = "androidx.core"
	}

	enum class Lifecycle : Dependency {
		LifecycleRuntimeKtx {
			override val project: String get() = "lifecycle-runtime-ktx"
		},
		;

		override val group: String get() = "androidx.lifecycle"
		override val version: String get() = Version.LIFECYCLE
	}

	enum class Room : Dependency {
		Runtime {
			override val project: String get() = "room-runtime"
		},
		Ktx {
			override val project: String get() = "room-ktx"
		},
		Compiler {
			override val project: String get() = "room-compiler"
		},
		;

		override val group: String get() = "androidx.room"
		override val version: String get() = Version.ROOM
	}

	enum class Hilt: Dependency {
		Compose {
			override val project: String get() = "hilt-navigation-compose"
			override val version: String get() = Version.HILT_COMPOSE
		},
		;

		override val group: String get() = "androidx.hilt"
	}

	enum class Navigation : Dependency {
		Compose {
			override val project: String get() = "navigation-compose"
		},
		;

		override val group: String get() = "androidx.navigation"
		override val version: String get() = Version.NAVIGATION
	}

	interface Test {
		object Ext : Dependency {
			override val group: String
				get() = "androidx.test.ext"
			override val project: String
				get() = "junit"
			override val version: String
				get() = Version.JUNIT_VERSION

		}

		enum class Espresso : Dependency {
			EspressoCore {
				override val project: String get() = "espresso-core"
			},
			;

			override val group: String get() = "androidx.test.espresso"
			override val version: String get() = Version.ESPRESSO_CORE
		}
	}
}