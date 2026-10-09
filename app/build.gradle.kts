plugins {
	apply(Android.Application)
	apply(JetBrains.Kotlin.PluginCompose)
}

android {
	namespace = "mrpapermoon.whisper"
	compileSdk = Version.COMPILE_SDK

	defaultConfig {
		applicationId = "mrpapermoon.whisper"
		minSdk = Version.MIN_SDK
		targetSdk = Version.TARGET_SDK
		versionCode = 1
		versionName = "1.0"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	buildFeatures {
		compose = true
	}
}

dependencies {
	implementation(project(":mesh"))
	implementation(project(":tools"))

	dependency(AndroidX.Activity.ActivityCompose)
	dependency(AndroidX.Lifecycle.LifecycleRuntimeKtx)

	bom(AndroidX.Compose) {
		attach(AndroidX.Compose.Material3.Material3)
		attach(AndroidX.Compose.Ui.Ui)
		attach(AndroidX.Compose.Ui.UiTooling)
		attach(AndroidX.Compose.Ui.UiToolingPreview)

		debug(AndroidX.Compose.Ui.UiTooling)
		debug(AndroidX.Compose.Ui.UiTestManifest)
	}
}