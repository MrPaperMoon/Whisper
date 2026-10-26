import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	apply(Android.Library)
	apply(Google.Ksp)
	apply(JetBrains.Kotlin.PluginSerialization)
}

android {
	namespace = "mrpapermoon.whisper.contacts"
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

		freeCompilerArgs.addAll(
			"-opt-in=kotlinx.serialization.InternalSerializationApi",
			"-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
		)
	}
}

dependencies {
	dependency(Google.Hilt.Android)
	ksp(Google.Hilt.Compiler.dependencyNotation)

	implementation(project(":entity"))
	implementation(project(":tools"))

	dependency(AndroidX.Room.Runtime)
	dependency(AndroidX.Room.Ktx)
	ksp(AndroidX.Room.Compiler.dependencyNotation)

	dependency(JetBrains.Kotlinx.Serialization.Protobuf)
	dependency(JetBrains.Kotlinx.Serialization.Core)
}
