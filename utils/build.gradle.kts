import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    apply(JavaLibrary)
    apply(JetBrains.Kotlin.Jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
	exportDependency(JetBrains.Kotlinx.Coroutines.Core)
}
