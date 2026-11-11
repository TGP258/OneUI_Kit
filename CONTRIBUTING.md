# 贡献指南

## 构建环境

| 项目 | 要求 |
| --- | --- |
| JDK | 17 |
| Android SDK | Platform API 35、Build Tools 35.0.1 |
| Gradle | 使用仓库内置的 Wrapper（8.11.1），无需预装 |

通过 `ANDROID_HOME` 环境变量指定 Android SDK 路径，或在工程根创建 `local.properties` 并写入 `sdk.dir=<SDK 路径>`。后者不纳入版本管理。

## 常用命令

```bash
./gradlew :oneui-kit:assembleDebug
./gradlew :sample:assembleDebug
./gradlew :oneui-kit:publishToMavenLocal
```

AAR 产物位于 `oneui-kit/build/outputs/aar/`，预览应用 APK 位于 `sample/build/outputs/apk/debug/`。

## 变更约束

- 公开 API 的破坏性变更须在更新日志中标注，并同步更新 `docs/components.md`。
- 色值、明度表与绘制常量集中定义于 `MonetTones` 与 `OneUiPalette`；调整固定色值时须补充实测依据与设备信息。
- 控件手势与状态行为变更后，须在 `sample` 中验证浅色、深色以及嵌套滚动场景。
- 提交前至少执行一次 `:oneui-kit:assembleDebug` 与 `:sample:assembleDebug`。
- 不提交构建产物、上游 AAR、签名文件与本机配置。
- 变更上游依赖版本或来源范围时，须同步更新 `docs/upstream.md`、`docs/getting-started.md`、`NOTICE` 与 `THIRD_PARTY_LICENSES.md`。

## 上游组件

本仓库基于 [OneUIProject/oneui-design](https://github.com/OneUIProject/oneui-design) 与 [OneUIProject/oneui-core](https://github.com/OneUIProject/oneui-core) 整理而成，上游两个仓库采用 MIT 许可，本仓库整体亦采用 MIT 许可。版本基线、沿用范围与许可证关系见[与上游项目的关系](docs/upstream.md)。

- 上游组件自身的缺陷与功能请求应提交至对应的上游仓库；本仓库仅维护语义色板、自绘控件与封装层。
- 升级上游制品后，须重新验证主题继承、同名类排除规则、控件外观以及 `sample` 中的原生控件对照卡。
- 新增第三方依赖或调整授权方式时，须在 `NOTICE` 与 `THIRD_PARTY_LICENSES.md` 中补充对应声明。
