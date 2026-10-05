interface Dependency {
	val group: String
	val project: String
	val version: String

	val scheme: String
		get() = "$group:$project"

	val dependencyNotation: String
		get() = "$scheme:$version"
}