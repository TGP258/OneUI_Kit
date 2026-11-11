# 与上游项目的关系

## 来源

本仓库最初基于 [OneUIProject/oneui-design](https://github.com/OneUIProject/oneui-design) 及其配套核心库 [OneUIProject/oneui-core](https://github.com/OneUIProject/oneui-core) 进行优化与整理，随后重构为可独立构建与发布的组件库。上游项目提供 One UI 风格的设计模块与 SESL（Samsung Experience Support Library）组件，包括主题、基础控件与对话框等；本仓库在其组件基线上重新组织色彩系统与交互控件，并补充工程配置、文档与持续集成。

| 上游仓库 | 内容 | 许可证 | 版权声明 |
| --- | --- | --- | --- |
| [oneui-design](https://github.com/OneUIProject/oneui-design) | `OneUITheme`、设计组件、上游示例应用 | MIT | Copyright (c) 2022 Yanndroid & BlackMesa123 |
| [oneui-core](https://github.com/OneUIProject/oneui-core) | SESL 版 AppCompat、Material 组件与基础控件 | MIT | Copyright (c) 2022 Yanndroid & BlackMesa123 |

两个上游仓库的 `LICENSE` 内容一致，全文保留于 [第三方许可证](../THIRD_PARTY_LICENSES.md)。

## 版本基线

| 上游制品 | 锁定版本 | 本仓库用途 |
| --- | --- | --- |
| `io.github.oneuiproject:design` | 1.2.6 | `OneUITheme` 主题基线、对话框与 One UI Toast |
| `io.github.oneuiproject.sesl:material` | 1.5.0 | SESL 版 Material 组件 |
| `io.github.oneuiproject.sesl:appcompat` | 1.4.0 | `SeslSeekBar`、`SeslToggleSwitch`、`SeslSwitchBar` 等控件 |

上游项目支持 Android 6.0（API 23）及以上。本仓库将最低版本设为 API 31，原因是动态取色链路依赖 Android 12 起提供的系统调色板字段与 Monet 强调色。

## 沿用内容

- `OneUITheme` 作为应用主题基线，SESL 控件依赖该主题取得默认样式。
- `SeslSeekBar` 作为 `DragOnlySeekBar` 的基类；触觉反馈使用 SESL 定义的 `HapticFeedbackConstants` 常量。
- 上游 Maven 制品的坐标与同名类排除规则。SESL 与 AndroidX 官方库同包同名，依赖树中需排除官方版本，该约束在上游接入说明中同样存在。
- `sample` 中用于对照展示的 SESL 原生控件与 `dev.oneuiproject.oneui` 包下的对话框、Toast 实现。

## 改造内容

| 范围 | 上游形态 | 本仓库形态 |
| --- | --- | --- |
| 工程结构 | 上游仓库内的 design 模块与示例应用 | 自包含的独立 Gradle 工程，分为 `:oneui-kit` 与 `:sample` |
| 色彩系统 | 上游主题提供的静态资源色 | Kotlin 实现的语义色板，读取三星系统种子色，回落至 AOSP Monet 与固定配色 |
| 控件实现 | 上游 SESL 控件与 XML 资源 | `BoSwitchView` 与 `DragOnlySeekBar` 两个自绘控件，颜色由色板注入 |
| 界面构建 | 上游示例应用以布局资源为主 | `sample` 以代码方式构建界面，覆盖色板、控件状态与嵌套滚动验证 |
| 命名空间 | `dev.oneuiproject.oneui`、`io.github.oneuiproject` | 发布坐标 `dev.pascal.oneuikit:oneui-kit:1.0.0`，包名 `dev.pascal.oneuikit` |
| 最低版本 | API 23 | API 31 |
| 工程配套 | 上游文档站点 | 仓库内 `docs/`、Maven 本地发布配置、GitHub Actions 构建工作流 |

## 移除内容

- 上游 design 模块与示例应用的 Java 源码、布局及图片资源未纳入本仓库。
- 上游 AAR 不随仓库分发。二进制制品的版权与许可证归上游权利人所有，仓库仅声明 Maven 坐标；离线构建时按需自行准备，配置方式见[接入指南](getting-started.md)。

## 许可证关系

上游两个仓库采用 MIT 许可。该许可允许使用、修改与再授权，同时要求在副本或实质性部分中保留版权声明与许可声明；本仓库据此在 [NOTICE](../NOTICE) 与 [第三方许可证](../THIRD_PARTY_LICENSES.md) 中保留上游版权行与 MIT 全文。

本仓库整体采用 [MIT License](../LICENSE)，与上游保持一致。许可文本相同，版权行按权利人分别列示：本仓库新增实现归 The One UI Kit Authors，上游组件归 Yanndroid & BlackMesa123。SESL 组件底层派生自 Android Jetpack 与 Material Components for Android，二者采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)，该条款仅适用于对应组件，不因本仓库采用 MIT 许可而变更。

## 上游同步与问题分流

- 依赖版本集中定义于 `oneui-kit/build.gradle.kts`。升级上游制品后，须重新验证主题继承、控件外观、同名类排除规则与 `sample` 中的原生控件对照卡。
- 上游组件自身的缺陷与功能请求应提交至对应的上游仓库；本仓库仅维护对其封装、色板与自绘控件的实现。
- GitHub 的 Fork 关系由平台在创建仓库时记录，无法通过本地仓库的提交历史补建。若需保留该关系，应先在 GitHub 上 Fork [oneui-design](https://github.com/OneUIProject/oneui-design)，再将整理后的内容推送至 Fork 仓库；以独立仓库发布时，来源与许可证声明由本文件及 [NOTICE](../NOTICE) 承担。

## 参考

- [oneui-design 项目主页](https://github.com/OneUIProject/oneui-design)
- [oneui-design 组件文档](https://oneuiproject.github.io/design/)
- [oneui-core 项目主页](https://github.com/OneUIProject/oneui-core)
- [Android Jetpack](https://github.com/androidx/androidx)
- [Material Components for Android](https://github.com/material-components/material-components-android)
