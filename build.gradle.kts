// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	notate(Android.Application)
	notate(Android.Library)
	notate(JetBrains.Kotlin.PluginCompose)
	notate(JetBrains.Kotlin.Jvm)
	notate(JetBrains.Kotlin.PluginSerialization)
	notate(Ksp)
}

allprojects {
	repositories {
		google()
		mavenCentral()
	}
}
