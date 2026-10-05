@file:Suppress("unused", "PackageDirectoryMismatch")

interface Github {
	enum class Oshai : Dependency {
		KotlinLoggingJvm {
			override val project: String get() = "kotlin-logging-jvm"
			override val version: String get() = Version.KOTLIN_LOGGING_JVM
		},
		;

		override val group: String get() = "io.github.oshai"
	}

	enum class Slf4j : Dependency {
		Slf4jSimple {
			override val project: String get() = "slf4j-simple"
			override val version: String get() = Version.SLF4J_SIMPLE
		},
		;

		override val group: String get() = "org.slf4j"
	}
}