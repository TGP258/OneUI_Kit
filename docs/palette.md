# 色彩系统

## 数据来源

用户在「壁纸和风格 → 调色板」中完成选择后，系统将种子色写入 `Settings.Secure` 的 `theme_customization_overlay_packages`：

```bash
adb shell settings get secure theme_customization_overlay_packages
```

```json
{
  "_applied_timestamp": 1793715880726,
  "android.theme.customization.accent_color": "ffa5aac2",
  "android.theme.customization.system_palette": "ffa5aac2",
  "android.theme.customization.color_source": "preset",
  "android.theme.customization.theme_style": "TONAL_SPOT",
  "android.theme.customization.color_index": "0"
}
```

| 字段 | 含义 |
| --- | --- |
| `accent_color` | 种子色，ARGB 格式（`ff` 加 RGB）；系统设置中开关与滑杆的实际颜色 |
| `system_palette` | 与 `accent_color` 基本同值，用于兼容 |
| `color_source` | `preset` 表示预设色板，`wallpaper` 表示壁纸取色 |
| `theme_style` | 色调方案标识，例如 `TONAL_SPOT` |
| `_applied_timestamp` | 应用时间戳，每次更换调色板都会变化，用于判断配置是否更新 |

读取该设置不需要特殊权限。`MonetTones.seedOf()` 负责解析，解析异常时按回落链处理，不向上抛出异常。

## 与 AOSP 色位的区别

`system_accent1_*` 属于 AOSP Monet 链路，与三星调色板不同源。同一次实测数据如下：

| 色位 | 实测值 |
| --- | --- |
| `accent_color`（三星选中值） | `#A5AAC2`，蓝灰 |
| `system_accent1_500`（AOSP 计算值） | `#7C6CB0`，紫 |

两者色相不同，使用 AOSP 色位无法复现系统外观。此外，三星中性色的编号方向与 AOSP 相反：`system_neutral1_10` 实测为 `#FFFBFF`（接近白），`system_neutral1_900` 为 `#221A10`（接近黑）。按 AOSP 惯例将低编号理解为深色，会得到整体偏白的表面色。

因此本库的取值原则为：强调色只使用 `accent_color`，中性表面色由种子色色相与显式明度表计算，不读取 `system_neutral*` 色位。

## 取值优先级

`MonetTones.seedOf()` 内置如下回落链，调用方无需判断设备类型：

```
accent_color → system_accent1_500 → 0（由 OneUiPalette 使用固定配色）
```

| 设备 | 种子色 | 效果 |
| --- | --- | --- |
| 三星 One UI 4 及以上 | `accent_color` | 与系统设置一致 |
| Pixel / AOSP 12 及以上 | `system_accent1_500` | 强调色接近系统，表面色按本库明度表生成 |
| 其他厂商 ROM | 视厂商实现而定 | 可能回落至固定配色 |
| Android 11 及以下 | 不可用 | 固定配色；如需跟随壁纸，需另行实现壁纸量化 |

One UI 的表面色并非由色阶推导，而是接近黑或接近白且彩度极低。AOSP Monet 会将整页染上种子色调，因此两者观感不等价。若需逐像素对齐特定 ROM，应按下列明度表针对该版本重新采样标定。

## 生成公式

种子色仅提供色相，明度与彩度由显式参数指定：

```kotlin
tone(seed, L, C) = Lab(L, C·cos h, C·sin h) → sRGB   // h 为种子色的 LAB 色相角
```

更换调色板时明度结构保持不变，仅色相随种子变化，与系统行为一致。

## 明度表

`MonetTones.from()` 使用的 L\* / C\* 参数：

| 角色 | 浅色 L\* / C\* | 深色 L\* / C\* |
| --- | --- | --- |
| `background` 页面底 | 95.6 / 1.4 | 0.8 / 0.8 |
| `card` 卡片 | 99.0 / 0.4 | 12.0 / 3.0 |
| `cardAlt` 次表面 | 92.0 / 1.6 | 17.0 / 3.2 |
| `divider` 分割线 | 88.0 / 1.2 | 24.0 / 2.4 |
| `text` 主文字 | 9.0 / 1.2 | 95.0 / 1.0 |
| `subText` 次文字 | 47.0 / 2.6 | 72.0 / 4.0 |
| `track` 关闭态轨道 | 86.0 / 1.6 | 28.0 / 2.6 |
| `segmentTrack` 分段轨道 | 92.5 / 1.2 | 18.0 / 2.0 |
| `segmentThumb` 分段选中块 | 99.5 / 0.2 | 29.0 / 2.6 |
| `chip` 图标底板 | 91.0 / 10 | 26.0 / 14 |
| `onChip` 图标色 | 34.0 / 20 | 88.0 / 24 |

## 实测基准

以暖色种子 `#D3A07C` 对照系统「设置 / 壁纸和风格」截图采样，结果如下：

| 项目 | 系统实测 |
| --- | --- |
| 浅色页面底 | `#F4F0EF` |
| 浅色卡片 | `#FCFCFC`，比底色亮 8 至 10 级，以此区分层次 |
| 浅色开关（开启态） | `#D2A07B`，与种子色一致，未做压暗处理 |
| 深色页面底 | `#010101` |
| 深色卡片 | `#221A17` |

标定过程中，页面底 L\* 取 94.8 时得到 `#F2EFED`，较系统值暗 2 级，因此调整为 95.6。冷色种子 `#A5AAC2` 下当前输出为页面底 `#F1F2F4`、卡片 `#FBFCFC`、开关 `#A5AAC2`，明度结构一致。

## 强调色分级

| 字段 | 取值规则 | 适用场景 |
| --- | --- | --- |
| `accent` | 种子色原值 | 开关轨道、滑杆已走段与圆环、选中态 |
| `accentDeep` | 浅色为 L40（彩度限制在 14 至 22）；深色下若种子过暗则提升至 L72 | 强调色文字、实心按钮填充 |
| `onAccent` | `accentDeep` 亮度大于 0.5 时取 L10 深色，否则取白色 | 实心按钮文字，保证对比度 |

浅色模式下种子亮度通常较高（`#D3A07C` 约为 0.66），直接搭配白色文字对比度不足，因此文字与按钮使用 `accentDeep`；开关与滑杆仍使用种子原色，与系统保持一致。

## 实现约束

表面色不使用颜色线性混合生成。早期实现采用 `mix(n50, n10, 0.45)` 计算页面底，混合比例不具备明度语义，且无法抑制三星色位本身的高亮度，导致底色与卡片接近同色。当前实现改为显式指定 L\*，切换种子色时层次保持稳定。`MonetTones.mix()` 仅作为通用颜色混合工具保留，不参与语义色板生成。

## 核验方法

```bash
adb shell settings get secure theme_customization_overlay_packages
```

```bash
python -c "
from PIL import Image
im = Image.open('shot.png').convert('RGB')
px = im.load()
print('#%02X%02X%02X' % px[30, 1900], '#%02X%02X%02X' % px[540, 700])"
```

采样位置分别为页面底角落与卡片中部。判定标准：卡片比页面底亮 8 至 10 级、开启态开关轨道等于种子色、深色页面底接近 `#010101`。
