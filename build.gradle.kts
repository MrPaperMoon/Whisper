// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	notate(Android.Application)
	notate(Android.Library)
	notate(Google.HiltPlugin)
	notate(Google.Ksp)
	notate(JetBrains.Kotlin.Jvm)
	notate(JetBrains.Kotlin.PluginCompose)
	notate(JetBrains.Kotlin.PluginSerialization)
}

allprojects {
	repositories {
		google()
		mavenCentral()
	}
}
