import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	apply(Android.Library)
}

android {
	namespace = "mrpapermoon.whisper.nearby"
	compileSdk {
		version = release(Version.COMPILE_SDK)
	}

	defaultConfig {
		minSdk = Version.MIN_SDK
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11

		isCoreLibraryDesugaringEnabled = true
	}
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_11
	}
}

dependencies {
	implementation(project(":utils"))

	loggingDependency()
	dependency(Google.PlayServices.PlayServicesNearby)
	dependency(JetBrains.Kotlinx.Coroutines.Core)

	coreLibraryDesugaring(Android.Tools.DesugarJdkLibs.dependencyNotation)
}