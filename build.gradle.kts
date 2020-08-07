import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackOutput.Target.COMMONJS

plugins {
  id("org.jetbrains.kotlin.js") version "1.3.72"
}

group = "uk.co.iterator"
version = "1.0-SNAPSHOT"

repositories {
  maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
  mavenCentral()
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib-js"))
  implementation("com.benasher44:uuid:0.1.1")
  implementation("org.jetbrains:kotlin-react:16.13.1-pre.110-kotlin-1.3.72")
  implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.110-kotlin-1.3.72")
  implementation(npm("react", "16.13.1"))
  implementation(npm("react-dom", "16.13.1"))
  implementation("org.jetbrains:kotlin-styled:1.0.0-pre.110-kotlin-1.3.72")
  implementation(npm("styled-components"))
  implementation(npm("inline-style-prefixer"))
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