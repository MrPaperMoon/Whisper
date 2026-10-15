@file:Suppress("unused", "PackageDirectoryMismatch")

object Junit : Dependency {
	override val group: String
		get() = "junit"
	override val project: String
		get() = "junit"
	override val version: String
		get() = Version.JUNIT_VERSION
}

object JavaLibrary : Plugin {
	override val project: String
		get() = "java-library"
	override val version: String
		get() = throw IllegalStateException("Java library has no version")
}

object Ksp : Plugin {
	override val project: String
		get() = "com.google.devtools.ksp"
	override val version: String
		get() = Version.KSP
}

object Kapt : Plugin {
	override val project: String
		get() = "kotlin-kapt"
	override val version: String
		get() = throw IllegalStateException("Kapt has no version")
}