plugins {
	apply(Android.Application)
	apply(Google.HiltPlugin)
	apply(Google.Ksp)
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

ksp {
	arg("dagger.fastInit", "enabled")
}

dependencies {
	implementation(project(":actors"))
	implementation(project(":entity"))
	implementation(project(":tools"))

	dependency(Google.Hilt.Android)
	ksp(Google.Hilt.Compiler.dependencyNotation)

	dependency(AndroidX.Activity.ActivityCompose)
	dependency(AndroidX.Core.Splashscreen)
	dependency(AndroidX.Hilt.Compose)
	dependency(AndroidX.Lifecycle.LifecycleRuntimeKtx)
	dependency(AndroidX.Navigation.Compose)

	bom(AndroidX.Compose) {
		attach(AndroidX.Compose.Material.Material)
		attach(AndroidX.Compose.Ui.Ui)
		attach(AndroidX.Compose.Ui.UiTooling)
		attach(AndroidX.Compose.Ui.UiToolingPreview)

		debug(AndroidX.Compose.Ui.UiTooling)
		debug(AndroidX.Compose.Ui.UiTestManifest)
	}
}
