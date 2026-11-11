package dev.pascal.oneuikit.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

/**
 * 极简进度条：圆角轨道 + 强调色已走段。
 * 只用色板里的两个颜色，跟着调色板变，不依赖主题属性。
 */
class BarView(
    context: Context,
    private val trackColor: Int,
    private val fillColor: Int,
) : View(context) {

    var fraction: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        val h = height.toFloat()
        if (h <= 0f || width <= 0) return
        val r = h / 2f
        paint.color = trackColor
        canvas.drawRoundRect(0f, 0f, width.toFloat(), h, r, r, paint)
        if (fraction <= 0f) return
        paint.color = fillColor
        canvas.drawRoundRect(0f, 0f, (width * fraction).coerceAtLeast(h), h, r, r, paint)
    }
}
