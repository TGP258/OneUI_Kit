# 第三方许可证

本文件记录本仓库所依赖或源自的上游软件及其许可证。本仓库整体采用 [MIT License](LICENSE)，与上游项目保持一致；下列条款分别适用于各自对应的上游组件。

## OneUIProject / oneui-design 与 oneui-core

| 项目 | 仓库 | 许可证 | 版权声明 |
| --- | --- | --- | --- |
| oneui-design | https://github.com/OneUIProject/oneui-design | MIT | Copyright (c) 2022 Yanndroid & BlackMesa123 |
| oneui-core（SESL） | https://github.com/OneUIProject/oneui-core | MIT | Copyright (c) 2022 Yanndroid & BlackMesa123 |

对应构建制品：

| 坐标 | 来源 |
| --- | --- |
| `io.github.oneuiproject:design:1.2.6` | oneui-design |
| `io.github.oneuiproject.sesl:material:1.5.0` | oneui-core |
| `io.github.oneuiproject.sesl:appcompat:1.4.0` | oneui-core |

上述两个上游仓库的 `LICENSE` 内容与本仓库采用的 MIT 许可文本一致，版权行归属上游权利人。依据 MIT 条款，此处保留其版权声明与许可全文。

```text
MIT License

Copyright (c) 2022 Yanndroid & BlackMesa123

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Android Jetpack 与 Material Components for Android

oneui-core 中的 SESL 组件派生自 [Android Jetpack](https://github.com/androidx/androidx) 与 [Material Components for Android](https://github.com/material-components/material-components-android)，其上游代码采用 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)。相应版权归各自权利人所有，许可证全文以官方文本为准。本仓库未在源码中重新分发这些组件，仅在构建时通过 Maven 坐标解析。

## 商标

One UI 是 Samsung Electronics Co., Ltd. 的商标。本仓库为非官方项目，与三星电子无隶属、授权或认可关系。
