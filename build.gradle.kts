// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	notate(Android.Application)
	notate(Android.Library)
	notate(JetBrains.Kotlin.PluginCompose)
	notate(JetBrains.Kotlin.Jvm)
}

allprojects {
	repositories {
		google()
		mavenCentral()
	}
}
