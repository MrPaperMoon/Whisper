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