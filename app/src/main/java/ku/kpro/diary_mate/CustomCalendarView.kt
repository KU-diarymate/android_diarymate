package ku.kpro.diary_mate

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.util.Calendar
import java.util.Date

class CustomCalendarView(context : Context, attr : AttributeSet) : View(context, attr) {

    private var lineInterval = 0f
    private val calendar = Calendar.getInstance()
    private val shiftDate : Int
    private val paint = Paint()
    private val weekChar = arrayOf("S", "M", "T", "W", "T", "F", "S")

    init {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        shiftDate = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> 0
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            else -> -1
        }
        calendar.time = Date()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        lineInterval = MeasureSpec.getSize(widthMeasureSpec).toFloat() / 7
    }

    override fun onDraw(canvas: Canvas) {
        fun widthPosition(pos : Int, word : String) : Float {
            val bound = Rect()
            paint.getTextBounds(word, 0, word.length, bound)
            return (pos * lineInterval + lineInterval / 2 - bound.exactCenterX() / 2)
        }
        fun heightPosition(pos : Int) : Float {
            return (240f + pos * 140f)
        }

        super.onDraw(canvas)
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        paint.textSize = 37f
        paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        weekChar.forEachIndexed { idx, w ->
            canvas.drawText(w, widthPosition(idx, w), 100f, paint)
        }

        val currentDate = calendar.get(Calendar.DAY_OF_MONTH)
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for(day in 1..lastDayOfMonth) {
            canvas.drawText(day.toString(), widthPosition((day % 7 - 1 + shiftDate) % 7, day.toString()), heightPosition((day - 1 + shiftDate) / 7), paint)
            if(day == currentDate) paint.color = ContextCompat.getColor(context, R.color.grey)
        }
    }

}