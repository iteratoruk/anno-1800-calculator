import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput.Target.COMMONJS

plugins {
  id("org.jetbrains.kotlin.js") version "1.3.72"
}

group = "uk.co.iterator"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib-js"))
  implementation("com.benasher44:uuid:0.1.1")
  testImplementation(kotlin("test-js"))
}

kotlin.target.browser {
  webpackTask {
    output.libraryTarget = COMMONJS
  }
  testTask {
    useKarma {
      useFirefox()
    }
  }
}