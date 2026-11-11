plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "dev.pascal.oneuikit.sample"
    compileSdk = 35
    buildToolsVersion = "35.0.1"

    defaultConfig {
        applicationId = "dev.pascal.oneuikit.sample"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources.excludes += setOf(
            "/META-INF/{AL2.0,LGPL2.1}",
            "META-INF/LICENSE*",
        )
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // SESL 分支与 AndroidX 官方库同包同名，需排除官方版本以避免重复类
    configurations.configureEach {
        exclude(group = "androidx.appcompat", module = "appcompat")
        exclude(group = "androidx.core", module = "core")
        exclude(group = "androidx.core", module = "core-ktx")
        exclude(group = "androidx.media", module = "media")
        exclude(group = "com.google.android.material", module = "material")
    }

    // 库通过 api 暴露 SESL 依赖，sample 无需重复声明
    implementation(project(":oneui-kit"))
}
