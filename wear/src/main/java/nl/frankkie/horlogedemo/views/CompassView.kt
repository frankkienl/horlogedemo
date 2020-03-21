package nl.frankkie.horlogedemo.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

class CompassView @JvmOverloads constructor(
    ctx: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(ctx, attrs, defStyleAttr) {

    var compassDegrees: Float = 0F
    private val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }
    private val paintArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    private val backgroundRect: Rect = Rect(0, 0, 280, 280)
    private val arrowPath: Path = Path().apply {
        val w = 280F
        val h = 280F
        //Clockwise
        moveTo(w / 2, h / 5) //top
        lineTo((w / 3) * 2, (h / 5) * 4) //bottom right
        lineTo(w / 2, (h / 4) * 3) //bottom middle
        lineTo(w / 3, (h / 5) * 4) //Bottom left
        lineTo(w / 2, h / 5) //Top
        close()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //Backround
        if (backgroundRect.width() != w || backgroundRect.height() != h) {
            backgroundRect.set(0, 0, w, h)
        }

        Log.d("Horloge c", "compassView onSizeChanged")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            //Background
            //canvas.drawRect(backgroundRect, paintBackground)
            canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paintBackground)

            //Arrow
            canvas.rotate(compassDegrees, (width / 2).toFloat(), (height / 2).toFloat())
            canvas.drawPath(arrowPath, paintArrow)
            canvas.rotate(-compassDegrees, (width / 2).toFloat(), (height / 2).toFloat())
        }

        Log.d("Horloge c", "compassView onDraw")
    }

    fun updateCompass(degrees: Float) {
        compassDegrees = degrees
        invalidate() //redraw
    }
}