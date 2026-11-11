# 控件参考

## BoSwitchView

One UI 9 形态开关。组件库自带的 `SeslToggleSwitch` 为 One UI 5 之前的形态：轨道较细、圆钮大于轨道并向外溢出。本控件采用胶囊轨道与内嵌圆钮，全部绘制在 `onDraw()` 中完成。

### 构造参数

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| `context` | `Context` | 上下文 |
| `checked` | `Boolean` | 初始选中状态 |
| `trackOn` | `Int` | 开启态轨道颜色，通常传入 `palette.accent` |
| `trackOff` | `Int` | 关闭态轨道颜色，通常传入 `palette.track` |
| `thumbColor` | `Int` | 圆钮颜色，深色模式下通常传入 `palette.cardAlt` |

控件继承 `CompoundButton`，因此具备 `isChecked`、`toggle()` 与 `setOnCheckedChangeListener()` 等标准语义。

### 绘制规格

| 项目 | 规格 |
| --- | --- |
| 可视尺寸 | 40 × 24 dp，胶囊半径 12 dp |
| 圆钮 | 直径 18 dp，四周留 3 dp |
| 触摸区域 | 高 40 dp，上下各 8 dp 内边距；绘制仍居中于 24 dp 轨道 |
| 状态动画 | 160 ms，`DecelerateInterpolator(1.4f)`，轨道颜色同步补间 |
| 禁用状态 | 绘制透明度降至 90 |
| 触觉反馈 | `performClick()` 触发 `HapticFeedbackConstants.CONFIRM` |

### 实现约束

- 必须显式设置 `isClickable = true`。以代码方式创建的 `CompoundButton` 默认不可点击，点击事件会穿透至父容器。
- 初始值必须通过 `super.setChecked(checked)` 写入。若调用控件自身的 `setChecked()`，首次渲染将触发一次不必要的补间动画。
- 控件在 `onDetachedFromWindow()` 中取消动画，避免视图复用后出现状态残留。

## DragOnlySeekBar

One UI 9 形态滑杆。系统 `SeekBar` 采用绝对定位，按下时数值即跳转至触点位置；在可滚动的配置界面中，纵向滑动容易误改数值。本控件以状态机限制为仅响应横向拖拽。基类 `SeslSeekBar` 来自 [oneui-core](https://github.com/OneUIProject/oneui-core) 的 SESL AppCompat 组件，本控件仅在其之上重写触摸处理与绘制逻辑。

### 公开属性

| 属性 | 类型 | 说明 |
| --- | --- | --- |
| `step` | `Int` | 分级跨度，默认 1 |
| `trackColor` | `Int` | 未完成段轨道颜色 |
| `fillColor` | `Int` | 已完成段轨道颜色 |
| `ringColor` | `Int` | 拇指圆环颜色 |
| `maskColor` | `Int` | 圆环内部遮罩颜色，应与控制所在的卡片底色一致 |
| `onLive` | `((Int) -> Unit)?` | 拖动过程中的连续回调 |
| `onCommit` | `((Int) -> Unit)?` | 拖拽结束后的提交回调 |

### 手势状态机

| 阶段 | 行为 |
| --- | --- |
| `ACTION_DOWN` | 记录起始坐标与当前进度，不修改数值，返回 `true` 以接收后续事件 |
| 纵向移动（纵向位移超过 `slop` 且大于横向位移） | 不接管手势，不阻止父容器拦截；由 `ScrollView` 拦截并向控件发送 `ACTION_CANCEL` |
| 横向移动（横向位移超过 `slop`） | 进入拖动状态，将基准点重置为当前位置，以当前进度作为起始值 |
| 拖动中 | `delta = (x - downX) / usable * max`，按增量调整进度并吸附至分级 |
| `ACTION_UP` | 仅在发生过拖动时触发 `onCommit`；单纯点击不提交 |
| `ACTION_CANCEL` | 释放拦截状态，保持当前进度 |

采用增量计算而非绝对定位，可避免纵向滚动中的微小横向位移导致数值跳转至触点位置。

### 绘制规格

| 项目 | 规格 |
| --- | --- |
| 轨道 | 高 16 dp 的胶囊，未完成段使用 `trackColor`，已完成段使用 `fillColor` 并裁剪至拇指中心 |
| 拇指 | 直径 21 dp 的空心环，环宽 3.5 dp；环内先以 `maskColor` 填充，避免轨道透出 |
| 控件高度 | 40 dp，轨道垂直居中 |
| 分级 | 量程不小于 40 时每级 5，不小于 12 时每级 2，其余为 1 |
| 触觉反馈 | 每跨越一个分级触发 `HapticFeedbackConstants.CLOCK_TICK` |

`max`、`progress` 等属性沿用 `SeslSeekBar` 的标准接口。

## OneUiWidgets

控件工厂，负责将色板注入控件并绑定回调。建议每个界面持有一个实例，色板变更时整体重建。

```kotlin
val palette = OneUiPalette(context)
val widgets = OneUiWidgets(context, palette)
```

### 方法

| 方法 | 参数 | 返回值 | 说明 |
| --- | --- | --- | --- |
| `switchView` | `checked: Boolean`、`onChange: (Boolean) -> Unit` | `BoSwitchView` | 使用色板中的 `accent`、`track` 与圆钮颜色构造开关，并绑定状态监听 |
| `sliderView` | `value: Int`、`max: Int`、`onLive: (Int) -> Unit`、`onCommit: (Int) -> Unit` | `DragOnlySeekBar` | 构造滑杆，设置分级、颜色与双阶段回调 |

`OneUiWidgets.stepFor(max)` 为分级跨度的计算方法：量程不小于 40 返回 5，不小于 12 返回 2，其余返回 1。

### 回调语义

| 回调 | 触发时机 | 建议用途 |
| --- | --- | --- |
| `onChange` | 开关状态变化时 | 立即更新配置 |
| `onLive` | 拖动过程中每次数值变化时 | 刷新数值标签等界面显示 |
| `onCommit` | 拖拽结束时 | 写入持久化配置 |

点击滑杆而不拖动不会触发 `onCommit`，避免误写入。

## 已知限制

- `DragOnlySeekBar` 的基类为 `SeslSeekBar`；不使用 SESL 组件库时，将基类替换为 `AppCompatSeekBar` 即可，触摸与绘制逻辑无需调整。
- 触觉反馈常量为 SESL 取值，非三星设备上可能退化为常规震动或无反馈。
- 滑杆的 `maskColor` 需与实际承载控件的表面色一致，否则圆环内部会出现轨道透色。
- 控件颜色均为构造时注入的静态值，色板变化后必须重新创建控件。
