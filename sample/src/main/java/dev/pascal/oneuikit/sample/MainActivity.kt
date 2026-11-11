package dev.pascal.oneuikit.sample

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SeslProgressBar
import androidx.appcompat.widget.SeslSeekBar
import androidx.appcompat.widget.SeslToggleSwitch
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import dev.pascal.oneuikit.MonetTones
import dev.pascal.oneuikit.OneUiPalette
import dev.pascal.oneuikit.OneUiWidgets
import dev.oneuiproject.oneui.dialog.ProgressDialog
import dev.oneuiproject.oneui.widget.Toast as OneUiToast
import java.util.Locale

/**
 * oneui-kit 的组件预览页。
 *
 * 整页只有两种颜色源：色板（OneUiPalette）和自绘控件。
 * 唯一的例外是最下面的「组件库原生控件」卡片，那里放的是 SESL 原始控件，用来看差异。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var palette: OneUiPalette
    private lateinit var widgets: OneUiWidgets

    private val prefs by lazy { getSharedPreferences("oneui_kit_demo", MODE_PRIVATE) }
    private val density: Float get() = resources.displayMetrics.density

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        palette = OneUiPalette(this, forcedDark())
        widgets = OneUiWidgets(this, palette)
        applyWindow()
        setContentView(buildScreen())
    }

    // ---------------------------------------------------------------- 基础设施

    private fun forcedDark(): Boolean? = when (prefs.getInt(KEY_MODE, 0)) {
        MODE_LIGHT -> false
        MODE_DARK -> true
        else -> null
    }

    private fun applyWindow() {
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            isAppearanceLightStatusBars = !palette.dark
            isAppearanceLightNavigationBars = !palette.dark
        }
    }

    private fun buildScreen(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(palette.background)
        }
        root.addView(header())

        val scroll = ScrollView(this).apply {
            clipToPadding = false
            isVerticalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(4), 0, dp(40))
        }
        scroll.addView(content, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        root.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        fill(content)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        return root
    }

    private fun header(): View {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(22), dp(12), dp(16), dp(8))
        }
        bar.addView(
            label("One UI Kit", 21f, palette.text, bold = true),
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f),
        )
        val mode = prefs.getInt(KEY_MODE, 0)
        bar.addView(
            segmented(listOf("自动", "浅色", "深色"), mode, compact = true) { index ->
                prefs.edit().putInt(KEY_MODE, index).apply()
                recreate()
            },
        )
        return bar
    }

    // ---------------------------------------------------------------- 页面结构

    private fun fill(column: LinearLayout) {
        column.addView(section("种子色"))
        column.addView(seedCard(), cardParams())

        column.addView(section("语义色板"))
        column.addView(paletteCard(), cardParams())

        column.addView(section("开关 · BoSwitchView"))
        column.addView(switchCard(), cardParams())

        column.addView(section("滑杆 · DragOnlySeekBar"))
        column.addView(sliderCard(), cardParams())

        column.addView(section("分段控件"))
        column.addView(segmentCard(), cardParams())

        column.addView(section("按钮"))
        column.addView(buttonCard(), cardParams())

        column.addView(section("设置项"))
        column.addView(settingsCard(), cardParams())

        column.addView(section("输入与标签"))
        column.addView(inputCard(), cardParams())

        column.addView(section("进度与弹窗"))
        column.addView(progressCard(), cardParams())

        column.addView(section("组件库原生控件（对照）"))
        column.addView(nativeCard(), cardParams())

        column.addView(
            label(
                "oneui-kit · 种子色 → 语义色板 → 自绘控件\nOne UI Design 1.2.6 / SESL 1.5.0",
                12f,
                palette.subText,
            ).apply {
                gravity = Gravity.CENTER
                setPadding(dp(32), dp(10), dp(32), 0)
                setLineSpacing(dp(4).toFloat(), 1f)
            },
            cardParams(bottom = 0),
        )
    }

    private fun section(title: String): TextView =
        label(title, 13f, palette.subText, bold = true, letterSpacing = 0.05f).apply {
            setPadding(dp(26), dp(22), dp(26), dp(8))
        }

    private fun cardParams(bottom: Int = 12) = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    ).apply { setMargins(dp(16), 0, dp(16), dp(bottom)) }

    // ---------------------------------------------------------------- 种子色

    private fun seedCard(): View {
        val seed = MonetTones.seedOf(this)
        val raw = MonetTones.rawSetting(this)
        val card = card()

        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(16))
        }
        head.addView(
            View(this).apply { background = round(seed, 23f) },
            LinearLayout.LayoutParams(dp(46), dp(46)).apply { marginEnd = dp(16) },
        )
        val info = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        info.addView(label("种子色 SEED", 11f, palette.subText, bold = true, letterSpacing = 0.08f))
        info.addView(label(hex(seed), 22f, palette.text, bold = true).apply { setPadding(0, dp(3), 0, 0) })
        info.addView(
            label(
                if (raw != null) "读自三星调色板（Settings.Secure）" else "回落：AOSP 取色 / 固定配色",
                12f,
                palette.subText,
            ).apply { setPadding(0, dp(3), 0, 0) },
        )
        head.addView(info, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        card.addView(head)
        card.addView(hairline(20f))

        addRow(card, title = "色调方案", subtitle = toneStyle(raw))
        addRow(
            card,
            title = "当前外观",
            subtitle = if (palette.dark) "深色 · 由色板强制" else "浅色 · 跟随系统或手动指定",
            divider = false,
        )
        return card
    }

    private fun toneStyle(raw: String?): String =
        raw?.let { Regex("\"theme_style\"\\s*:\\s*\"([^\"]+)\"").find(it)?.groupValues?.get(1) }
            ?: "TONAL_SPOT（AOSP 回落时按同结构计算）"

    // ---------------------------------------------------------------- 色板

    private fun paletteCard(): View {
        val card = card(padV = 10)
        val entries = listOf(
            "background" to palette.background,
            "card" to palette.card,
            "cardAlt" to palette.cardAlt,
            "divider" to palette.divider,
            "text" to palette.text,
            "subText" to palette.subText,
            "track" to palette.track,
            "accent" to palette.accent,
            "accentDeep" to palette.accentDeep,
            "onAccent" to palette.onAccent,
            "chip" to palette.chip,
            "onChip" to palette.onChip,
            "segmentTrack" to palette.segmentTrack,
            "segmentThumb" to palette.segmentThumb,
        )
        entries.chunked(2).forEach { pair ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(dp(12), dp(5), dp(8), dp(5))
            }
            pair.forEach { (name, color) ->
                row.addView(swatch(name, color), LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            }
            if (pair.size == 1) {
                row.addView(View(this), LinearLayout.LayoutParams(0, 1, 1f))
            }
            card.addView(row)
        }
        return card
    }

    private fun swatch(name: String, color: Int): View {
        val cell = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        cell.addView(
            View(this).apply { background = round(color, 9f, palette.divider, 1f) },
            LinearLayout.LayoutParams(dp(30), dp(30)).apply { marginEnd = dp(10) },
        )
        val text = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        text.addView(label(name, 12f, palette.text))
        text.addView(label(hex(color), 11f, palette.subText).apply { setPadding(0, dp(1), 0, 0) })
        cell.addView(text, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        return cell
    }

    // ---------------------------------------------------------------- 开关

    private fun switchCard(): View {
        val card = card()
        val state = booleanArrayOf(true, false, true)
        addSwitchRow(
            card = card,
            title = "跟随系统调色板",
            subtitle = "从 Settings.Secure 读取三星选中的种子色",
            checked = state[0],
            onChange = { state[0] = it },
        )
        addSwitchRow(
            card = card,
            title = "通知提示音",
            subtitle = "开关关闭时轨道退回 track 色",
            checked = state[1],
            onChange = { state[1] = it },
        )
        addSwitchRow(
            card = card,
            title = "不可用状态",
            subtitle = "禁用时整体降透明度，点击无效",
            checked = state[2],
            enabled = false,
            onChange = { state[2] = it },
            divider = false,
        )
        return card
    }

    private fun addSwitchRow(
        card: LinearLayout,
        title: String,
        subtitle: String?,
        checked: Boolean,
        enabled: Boolean = true,
        divider: Boolean = true,
        onChange: (Boolean) -> Unit,
    ) {
        val toggle = widgets.switchView(checked, onChange).apply { isEnabled = enabled }
        addRow(
            card = card,
            title = title,
            subtitle = subtitle,
            trailing = toggle,
            divider = divider,
            rowEnabled = enabled,
        )
    }

    // ---------------------------------------------------------------- 滑杆

    private fun sliderCard(): View {
        val card = card(padV = 4)
        addSlider(card, "媒体音量", 60, 100, "%")
        addSlider(card, "屏幕亮度", 12, 20, " 级", dividerAfter = false)
        return card
    }

    private fun addSlider(
        card: LinearLayout,
        title: String,
        initial: Int,
        max: Int,
        suffix: String,
        dividerAfter: Boolean = true,
    ) {
        val block = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(14), dp(20), dp(8))
        }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(
            label(title, 16f, palette.text),
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f),
        )
        val valueLabel = label("$initial$suffix", 14f, palette.accentDeep, bold = true)
        head.addView(valueLabel)
        block.addView(head)

        val slider = widgets.sliderView(initial, max, onLive = { valueLabel.text = "$it$suffix" })
        block.addView(
            slider,
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(40)).apply { topMargin = dp(2) },
        )
        card.addView(block)
        if (dividerAfter) card.addView(hairline(20f))
    }

    // ---------------------------------------------------------------- 分段

    private fun segmentCard(): View {
        val card = card()
        var selected = 1
        val holder = LinearLayout(this).apply { setPadding(dp(18), dp(16), dp(18), dp(4)) }

        fun render() {
            holder.removeAllViews()
            holder.addView(
                segmented(listOf("小", "中", "大"), selected) { index ->
                    selected = index
                    render()
                },
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT),
            )
        }
        render()
        card.addView(holder)
        card.addView(
            label("轨道 segmentTrack + 选中块 segmentThumb，靠 1dp 阴影浮起", 12f, palette.subText).apply {
                setPadding(dp(20), dp(10), dp(20), dp(18))
            },
        )
        return card
    }

    private fun segmented(
        items: List<String>,
        selected: Int,
        compact: Boolean = false,
        onSelect: (Int) -> Unit,
    ): LinearLayout {
        val itemHeight = if (compact) 26 else 34
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(3), dp(3), dp(3), dp(3))
            background = round(palette.segmentTrack, itemHeight / 2f + 3f)
        }
        items.forEachIndexed { index, text ->
            val isSelected = index == selected
            val tab = label(
                value = text,
                size = if (compact) 11f else 14f,
                color = if (isSelected) palette.text else palette.subText,
                bold = isSelected,
            ).apply {
                gravity = Gravity.CENTER
                isClickable = true
                setOnClickListener { onSelect(index) }
            }
            if (isSelected) {
                tab.background = round(palette.segmentThumb, itemHeight / 2f)
                tab.elevation = dp(1).toFloat()
            }
            val params = if (compact) {
                LinearLayout.LayoutParams(dp(46), dp(itemHeight))
            } else {
                LinearLayout.LayoutParams(0, dp(itemHeight), 1f)
            }
            root.addView(tab, params)
        }
        return root
    }

    // ---------------------------------------------------------------- 按钮

    private fun buttonCard(): View {
        val card = card()

        val first = LinearLayout(this).apply { setPadding(dp(16), dp(18), dp(16), dp(8)) }
        first.addView(
            actionButton("填充按钮", Style.FILLED) { toast("填充按钮 · accentDeep") },
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = dp(8) },
        )
        first.addView(
            actionButton("描边按钮", Style.OUTLINE) { toast("描边按钮 · 1dp 强调色") },
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f),
        )
        card.addView(first)

        val second = LinearLayout(this).apply { setPadding(dp(16), dp(6), dp(16), dp(16)) }
        second.addView(
            actionButton("文字按钮", Style.TEXT) { toast("文字按钮 · 无底色") },
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = dp(8) },
        )
        second.addView(
            actionButton("不可用", Style.FILLED, enabled = false) {},
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f),
        )
        card.addView(second)
        return card
    }

    private enum class Style { FILLED, OUTLINE, TEXT }

    private fun actionButton(
        text: String,
        style: Style,
        enabled: Boolean = true,
        onClick: () -> Unit,
    ): TextView {
        val textColor = when (style) {
            Style.FILLED -> palette.onAccent
            else -> palette.accentDeep
        }
        val button = label(text, 15f, textColor, bold = true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(10), dp(14), dp(10), dp(14))
            alpha = if (enabled) 1f else 0.4f
            isEnabled = enabled
            isClickable = enabled
        }
        val fill = when (style) {
            Style.FILLED -> round(palette.accentDeep, 24f)
            Style.OUTLINE -> round(Color.TRANSPARENT, 24f, palette.accentDeep, 1f)
            Style.TEXT -> round(Color.TRANSPARENT, 24f)
        }
        button.background = RippleDrawable(
            ColorStateList.valueOf(ripple()),
            fill,
            round(Color.WHITE, 24f),
        )
        if (enabled) button.setOnClickListener { onClick() }
        return button
    }

    // ---------------------------------------------------------------- 设置项

    private fun settingsCard(): View {
        val card = card()
        addRow(
            card = card,
            title = "关于手机",
            subtitle = "SM-S911B · Android 17",
            iconRes = R.drawable.ic_list,
            trailing = chevron(),
            onClick = { toast("打开「关于手机」") },
        )
        addRow(
            card = card,
            title = "调色板",
            subtitle = "跟随系统种子色生成",
            value = hex(MonetTones.seedOf(this)),
            iconRes = R.drawable.ic_palette,
            trailing = chevron(),
            onClick = { toast("打开「调色板」") },
        )
        addRow(
            card = card,
            title = "字体大小",
            value = "中",
            iconRes = R.drawable.ic_text,
            trailing = chevron(),
            onClick = { toast("字体大小 · 中") },
        )
        addSwitchRow(
            card = card,
            title = "开发者选项",
            subtitle = "关闭后设置项整行变灰",
            checked = true,
            onChange = { toast(if (it) "已开启" else "已关闭") },
            divider = false,
        )
        return card
    }

    private fun chevron(): TextView = label("›", 22f, palette.subText).apply {
        gravity = Gravity.CENTER
        includeFontPadding = false
    }

    // ---------------------------------------------------------------- 输入与标签

    private fun inputCard(): View {
        val card = card()
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(6))
        }

        val edit = EditText(this).apply {
            hint = "搜索设置项"
            setSingleLine(true)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTextColor(palette.text)
            setHintTextColor(palette.subText)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = round(palette.cardAlt, 16f)
            backgroundTintList = null
            isFocusableInTouchMode = true
        }
        box.addView(edit, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(52)))

        val chips = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(14), 0, dp(10))
        }
        var selectedChip = 0
        val labels = listOf("全部", "开关", "滑杆")

        fun renderChips() {
            chips.removeAllViews()
            labels.forEachIndexed { index, text ->
                val isSelected = index == selectedChip
                val chip = label(
                    value = text,
                    size = 13f,
                    color = if (isSelected) palette.onAccent else palette.text,
                    bold = isSelected,
                ).apply {
                    gravity = Gravity.CENTER
                    setPadding(dp(16), dp(8), dp(16), dp(8))
                    background = if (isSelected) {
                        round(palette.accentDeep, 16f)
                    } else {
                        round(palette.cardAlt, 16f, palette.divider, 1f)
                    }
                    isClickable = true
                    setOnClickListener {
                        selectedChip = index
                        renderChips()
                    }
                }
                chips.addView(chip, LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply { marginEnd = dp(8) })
            }
        }
        renderChips()
        box.addView(chips)
        card.addView(box)
        return card
    }

    // ---------------------------------------------------------------- 进度与弹窗

    private fun progressCard(): View {
        val card = card()

        val progressBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(12))
        }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(
            label("存储空间", 16f, palette.text),
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f),
        )
        head.addView(label("65%", 14f, palette.accentDeep, bold = true))
        progressBox.addView(head)
        progressBox.addView(
            BarView(this, palette.track, palette.accent).apply { fraction = 0.65f },
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(10)).apply { topMargin = dp(12) },
        )
        card.addView(progressBox)
        card.addView(hairline(20f))

        val buttons = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(16), dp(14), dp(16), dp(16))
        }
        buttons.addView(
            actionButton("对话框", Style.FILLED) { showDialog() },
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = dp(8) },
        )
        buttons.addView(
            actionButton("Toast", Style.OUTLINE) {
                OneUiToast.makeText(this, "已保存到 One UI Kit", OneUiToast.LENGTH_SHORT).show()
            },
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = dp(8) },
        )
        buttons.addView(
            actionButton("加载", Style.OUTLINE) { showLoading() },
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f),
        )
        card.addView(buttons)
        return card
    }

    private fun showDialog() {
        AlertDialog.Builder(this)
            .setTitle("删除这条记录？")
            .setMessage("删除后无法恢复，历史记录会一起清空。")
            .setNegativeButton("取消", null)
            .setPositiveButton("删除") { _, _ -> toast("已删除") }
            .show()
    }

    private fun showLoading() {
        val dialog = ProgressDialog.show(this, "正在同步", "从调色板读取种子色…", true, true)
        Handler(Looper.getMainLooper()).postDelayed({ runCatching { dialog.dismiss() } }, 1600)
    }

    // ---------------------------------------------------------------- 原生控件

    private fun nativeCard(): View {
        val card = card()
        card.addView(
            label(
                "下面三个是 SESL 组件库里的原始控件，没做任何重绘，用来看和上面自绘版本的差别。",
                12f,
                palette.subText,
            ).apply {
                setPadding(dp(20), dp(16), dp(20), dp(4))
                setLineSpacing(dp(3).toFloat(), 1f)
            },
        )
        card.addView(nativeBlock("SeslToggleSwitch") {
            SeslToggleSwitch(this).apply { isChecked = true }
        })
        card.addView(nativeBlock("SeslSeekBar") {
            SeslSeekBar(this).apply {
                max = 100
                progress = 40
            }
        })
        card.addView(nativeBlock("SeslProgressBar", last = true) {
            SeslProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
                progress = 65
            }
        })
        return card
    }

    private fun nativeBlock(title: String, last: Boolean = false, build: () -> View): View {
        val block = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(12), dp(20), if (last) dp(18) else dp(12))
        }
        block.addView(label(title, 12f, palette.subText))
        // 组件库控件在拿不到主题样式时会抛异常，预览页不该因此挂掉
        val view = runCatching(build).getOrNull()
        if (view != null) {
            block.addView(
                view,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply { topMargin = dp(8) },
            )
        } else {
            block.addView(label("（当前系统取不到该控件的样式）", 13f, palette.subText))
        }
        return block
    }

    // ---------------------------------------------------------------- 绘制工具

    private fun dp(value: Number): Int = (value.toFloat() * density + 0.5f).toInt()

    private fun ripple(): Int = if (palette.dark) 0x33FFFFFF else 0x1F000000

    private fun round(
        fill: Int,
        radiusDp: Float,
        strokeColor: Int = Color.TRANSPARENT,
        strokeWidthDp: Float = 0f,
    ): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(radiusDp).toFloat()
        setColor(fill)
        if (strokeWidthDp > 0f) setStroke(dp(strokeWidthDp), strokeColor)
    }

    private fun card(padH: Int = 0, padV: Int = 0): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = round(palette.card, 26f)
        clipToOutline = true
        setPadding(dp(padH), dp(padV), dp(padH), dp(padV))
    }

    private fun hairline(startDp: Float = 0f): View = View(this).apply {
        setBackgroundColor(palette.divider)
    }.also { view ->
        view.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            1,
        ).apply { marginStart = dp(startDp) }
    }

    private fun label(
        value: CharSequence,
        size: Float,
        color: Int,
        bold: Boolean = false,
        letterSpacing: Float = 0f,
    ): TextView = TextView(this).apply {
        text = value
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        setTextColor(color)
        if (bold) setTypeface(typeface, Typeface.BOLD)
        this.letterSpacing = letterSpacing
        includeFontPadding = false
    }

    private fun iconBadge(res: Int): ImageView = ImageView(this).apply {
        setImageResource(res)
        imageTintList = ColorStateList.valueOf(palette.onChip)
        setPadding(dp(8), dp(8), dp(8), dp(8))
        background = round(palette.chip, 12f)
    }

    private fun addRow(
        card: LinearLayout,
        title: String,
        subtitle: String? = null,
        value: String? = null,
        iconRes: Int? = null,
        trailing: View? = null,
        divider: Boolean = true,
        rowEnabled: Boolean = true,
        onClick: (() -> Unit)? = null,
    ) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = dp(58)
            setPadding(dp(20), dp(10), dp(18), dp(10))
            alpha = if (rowEnabled) 1f else 0.45f
            if (onClick != null) {
                isClickable = true
                background = RippleDrawable(ColorStateList.valueOf(ripple()), null, null)
                setOnClickListener { onClick() }
            }
        }
        if (iconRes != null) {
            row.addView(
                iconBadge(iconRes),
                LinearLayout.LayoutParams(dp(36), dp(36)).apply { marginEnd = dp(14) },
            )
        }
        val column = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        column.addView(label(title, 16f, palette.text))
        if (subtitle != null) {
            column.addView(
                label(subtitle, 12.5f, palette.subText).apply { setPadding(0, dp(2), 0, 0) },
            )
        }
        row.addView(column, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        if (value != null) {
            row.addView(label(value, 14f, palette.subText).apply { setPadding(dp(10), 0, 0, 0) })
        }
        if (trailing != null) {
            row.addView(
                trailing,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply { marginStart = dp(12) },
            )
        }
        card.addView(row)
        if (divider) {
            card.addView(
                hairline(if (iconRes != null) 70f else 20f),
            )
        }
    }

    private fun hex(color: Int): String = String.format(Locale.US, "#%06X", color and 0xFFFFFF)

    private fun toast(text: String) {
        OneUiToast.makeText(this, text, OneUiToast.LENGTH_SHORT).show()
    }

    private companion object {
        const val KEY_MODE = "mode"
        const val MODE_LIGHT = 1
        const val MODE_DARK = 2
    }
}
