import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	apply(Android.Library)
}

android {
	namespace = "mrpapermoon.whisper.tools"
	compileSdk {
		version = release(Version.COMPILE_SDK)
	}

	defaultConfig {
		minSdk = Version.MIN_SDK
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_11
	}
}

dependencies {
	api(project(":utils"))

	exportDependency(AndroidX.Core.CoreKtx)
	exportDependency(Github.Oshai.KotlinLoggingJvm)
	exportDependency(Github.Slf4j.Slf4jSimple)

	dependency(Google.PlayServices.PlayServicesTasks)
}