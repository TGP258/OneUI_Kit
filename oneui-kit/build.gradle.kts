import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

group = "dev.pascal.oneuikit"
version = "1.0.0"

android {
    namespace = "dev.pascal.oneuikit"
    compileSdk = 35
    buildToolsVersion = "35.0.1"

    defaultConfig {
        minSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    publishing {
        singleVariant("release")
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

    // 公开 API 中出现 SeslSeekBar、OneUITheme 等类型，因此以 api 暴露上游依赖
    api("io.github.oneuiproject:design:1.2.6")
    api("io.github.oneuiproject.sesl:material:1.5.0")
    api("io.github.oneuiproject.sesl:appcompat:1.4.0")

    // 离线构建可替换为本地 AAR 文件依赖，详见 docs/getting-started.md
}

afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class) {
                from(components["release"])
                artifactId = "oneui-kit"
                pom {
                    name.set("One UI Kit")
                    description.set("One UI styled view components and semantic palette for Android")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                            distribution.set("repo")
                        }
                    }
                }
            }
        }
    }
}
