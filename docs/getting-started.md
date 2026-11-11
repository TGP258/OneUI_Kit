# 接入指南

## 依赖配置

本库为独立工程，发布制品坐标为 `dev.pascal.oneuikit:oneui-kit:1.0.0`。消费方声明该坐标后即可使用：

```kotlin
dependencies {
    implementation("dev.pascal.oneuikit:oneui-kit:1.0.0")
}
```

库模块通过 `api` 暴露下列组件依赖，消费方无需重复声明。三个坐标均来自 [OneUIProject](https://github.com/OneUIProject) 的 oneui-design 与 oneui-core（SESL）项目，采用 MIT 许可；版权与许可证声明见 [NOTICE](../NOTICE) 与[第三方许可证](../THIRD_PARTY_LICENSES.md)。

| 坐标 | 用途 |
| --- | --- |
| `io.github.oneuiproject:design:1.2.6` | `OneUITheme` 与设计组件 |
| `io.github.oneuiproject.sesl:material:1.5.0` | SESL 版 Material 组件 |
| `io.github.oneuiproject.sesl:appcompat:1.4.0` | `SeslSeekBar`、`SeslToggleSwitch` 等 SESL 控件 |

SESL 分支提供与 AndroidX 同包同名的类。若依赖树中同时存在官方 AndroidX 组件，将产生重复类冲突，需在消费方模块中排除：

```kotlin
configurations.configureEach {
    exclude(group = "androidx.appcompat", module = "appcompat")
    exclude(group = "androidx.core", module = "core")
    exclude(group = "androidx.core", module = "core-ktx")
    exclude(group = "androidx.media", module = "media")
    exclude(group = "com.google.android.material", module = "material")
}
```

## 接入方式

### 仓库制品

在本工程根执行发布任务，将 AAR 与 POM 写入本地 Maven 仓库：

```bash
./gradlew :oneui-kit:publishToMavenLocal
```

消费方在仓库列表中加入 `mavenLocal()`：

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
```

发布至远程 Maven 仓库时，将 `mavenLocal()` 替换为对应仓库地址，并在发布脚本中配置仓库凭据。

### 复合构建

源码联调可在消费方 `settings.gradle.kts` 中引入本工程：

```kotlin
includeBuild("../OneUIKit")
```

保持 `implementation("dev.pascal.oneuikit:oneui-kit:1.0.0")` 不变，Gradle 会按 `group:artifactId:version` 将坐标依赖自动替换为被引入工程中的 `:oneui-kit` 模块。此方式无需预先发布制品，且库的修改可在消费方构建中即时生效。

### 源码模块

将 `oneui-kit/` 目录复制到消费方工程，在 `settings.gradle.kts` 中注册模块：

```kotlin
include(":oneui-kit")
```

消费方需声明 Android Library 插件，并将依赖改为工程内模块引用：

```kotlin
plugins {
    id("com.android.library") version "8.7.3" apply false
}
```

```kotlin
dependencies {
    implementation(project(":oneui-kit"))
}
```

此方式会在消费方工程内形成库的源码副本，后续需自行同步上游变更。

### 离线依赖准备

本仓库不分发上游 AAR。上游制品的版权与许可证归上游权利人所有，仅用于本地构建，不得随本仓库再分发。离线环境可先取得下列制品并放置于 `oneui-kit/libs/`，该目录不纳入版本管理：

| 坐标 | 文件名 |
| --- | --- |
| `io.github.oneuiproject:design:1.2.6` | `design-1.2.6.aar` |
| `io.github.oneuiproject.sesl:material:1.5.0` | `material-1.5.0.aar` |
| `io.github.oneuiproject.sesl:appcompat:1.4.0` | `appcompat-1.4.0.aar` |

随后将 `oneui-kit/build.gradle.kts` 中的 Maven 坐标替换为文件依赖：

```kotlin
implementation(files("libs/design-1.2.6.aar"))
implementation(files("libs/material-1.5.0.aar"))
implementation(files("libs/appcompat-1.4.0.aar"))
```

文件依赖不解析传递依赖，需自行补齐 `androidx.core` 等相关组件。恢复在线构建时应改回 Maven 坐标。

## 主题

应用主题必须继承 `OneUITheme`，否则 SESL 控件无法取得默认样式：

```xml
<style name="Theme.Example" parent="OneUITheme">
    <item name="windowNoTitle">true</item>
    <item name="windowActionBar">false</item>
</style>
```

`OneUITheme` 的父级为浅色 AppCompat 主题。深色界面由本库色板驱动，SESL 原生控件与对话框仍按主题基线呈现。

## 构造色板

```kotlin
// 跟随系统夜间模式，并读取系统调色板
val palette = OneUiPalette(context)

// 显式指定深浅模式
val lightPalette = OneUiPalette(context, forceDark = false)
val darkPalette = OneUiPalette(context, forceDark = true)

// 关闭动态取色，使用内置固定配色
val fixedPalette = OneUiPalette(context, useMonet = false)
```

`OneUiPalette` 是纯值对象，构造完成后色值不再变化。切换外观或调色板时，需重新构造色板并重建使用该色板的视图。

## 色板变化刷新

更换系统调色板不一定触发 `onConfigurationChanged`。建议在构造色板时保存 `MonetTones.rawSetting(context)` 返回的原始字符串，并在 `onResume()` 中比对：

```kotlin
private lateinit var paletteKey: String

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    paletteKey = MonetTones.rawSetting(this).orEmpty()
    setContentView(buildScreen(OneUiPalette(this)))
}

override fun onResume() {
    super.onResume()
    if (MonetTones.rawSetting(this).orEmpty() != paletteKey) recreate()
}
```

原始字符串包含 `_applied_timestamp`，每次应用调色板都会更新，可作为变更标识。

## 设备差异

| 设备类型 | 种子色来源 | 表面色表现 |
| --- | --- | --- |
| 三星 One UI 4 及以上（Android 12+） | `accent_color` | 与系统设置一致 |
| Pixel 及 AOSP 12 及以上 | `system_accent1_500` | 强调色接近系统，底色按本库明度表生成 |
| 其他厂商 ROM | 多数提供 `system_*` 色位，取值可能为固定值或厂商方案 | 可能回落至固定配色 |
| Android 11 及以下 | 无对应色位 | 固定配色 |

## 构建与验证

本工程包含 Library 模块与 `sample` 预览应用：

```bash
./gradlew :oneui-kit:assembleDebug
./gradlew :sample:assembleDebug
./gradlew :oneui-kit:publishToMavenLocal
```

AAR 产物位于 `oneui-kit/build/outputs/aar/`，预览应用 APK 位于 `sample/build/outputs/apk/debug/`。构建前需通过 `ANDROID_HOME` 环境变量或工程根的 `local.properties`（`sdk.dir`）指定 Android SDK 路径，该文件不纳入版本管理。若 Gradle 发行版未缓存，可使用本机 Gradle 8.11.1 并附加 `--offline`：

```bash
gradle :oneui-kit:assembleDebug :sample:assembleDebug --offline
```

`sample` 应用覆盖语义色板、控件状态、深浅外观与 SESL 原生控件对照，可用于构建后的功能核验。
