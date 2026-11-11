# 更新日志

本项目采用[语义化版本](https://semver.org/lang/zh-CN/)记录公开变更。

## [1.0.0] - 2026-11-11

首个公开版本。本版本以 [OneUIProject/oneui-design](https://github.com/OneUIProject/oneui-design) 及其配套核心库 oneui-core 的组件为基线整理与重构，沿用的上游制品版本为 `design:1.2.6`、`sesl:material:1.5.0` 与 `sesl:appcompat:1.4.0`；来源与改造范围见[与上游项目的关系](docs/upstream.md)。

### 新增

- 语义色板 `OneUiPalette` 与种子色解析工具 `MonetTones`，支持三星系统调色板、AOSP Monet 强调色与固定配色回落。
- 自绘控件 `BoSwitchView` 与 `DragOnlySeekBar`，以及控件工厂 `OneUiWidgets`。
- `sample` 组件预览应用，覆盖语义色板、开关状态、滑杆、分段控件、按钮、设置项、输入框、进度条、对话框与 SESL 原生控件对照。
- 接入指南、色彩系统说明与控件参考文档。
- 上游来源与许可证声明：`NOTICE`、`THIRD_PARTY_LICENSES.md` 与 `docs/upstream.md`。
- 仓库整体采用 MIT 许可，与上游项目保持一致；上游版权声明与许可全文另行保留。
- Gradle Wrapper、Maven 本地发布配置与持续集成工作流。
