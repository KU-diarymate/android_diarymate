package ku.kpro.diary_mate.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ku.kpro.diary_mate.R

class CustomToggleButton(context : Context, attr : AttributeSet)
    : View(context, attr) {

    private val backgroundPaint = Paint()
    private val buttonPaint = Paint()
    private var isLeftSelected = true

    private val buttonRadius = 23f // 원 버튼의 반지름
    private val buttonMargin = 5f // 원 버튼과 둥근 직사각형의 간격

    init {
        backgroundPaint.color = ContextCompat.getColor(context, R.color.green_theme)
        buttonPaint.color = Color.WHITE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // 둥근 직사각형의 사각형 영역 계산
        val rectF = RectF(0f, 0f, width, height)

        // 둥근 직사각형 그리기
        canvas.drawRoundRect(rectF, height / 2, height / 2, backgroundPaint)

        // 원 버튼의 x 좌표 계산
        val buttonX = if (isLeftSelected) buttonMargin + buttonRadius else width - buttonRadius - buttonMargin

        // 원 버튼의 y 좌표 계산 (수직 가운데 정렬)
        val buttonY = height / 2

        // 원 버튼 그리기
        canvas.drawCircle(buttonX, buttonY, buttonRadius, buttonPaint)
    }

    // 토글 상태 변경
    fun toggle() {
        isLeftSelected = !isLeftSelected
        invalidate() // 화면을 다시 그리도록 강제 호출
    }
}