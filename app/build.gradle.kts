import java.lang.reflect.Method

plugins {
    // AGP pinned: 8.7.3 works with Gradle 8.8+ and JDK17 (this box: Gradle 8.14.3, JDK17).
    id("com.android.application") version "8.7.3"
}

android {
    namespace = "local.lsposed.globalcapture"
    compileSdk = 35

    defaultConfig {
        applicationId = "local.lsposed.globalcapture"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
        // Pure Hook module: no launcher/service/provider components, DEX + metadata only.
    }

    compileOptions {
        // Module touches only java.*/android.* + Xposed host types (compileOnly stub).
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { buildConfig = false }

    // compile-only: NOT packaged (host runtime FQNs stay authoritative).
    dependencies {
        compileOnly(files("../../build-support/xposed-stub.jar"))
    }
}
