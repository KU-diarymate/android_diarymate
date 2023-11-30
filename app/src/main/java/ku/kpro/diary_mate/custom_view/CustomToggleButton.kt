package ku.kpro.diary_mate.custom_view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.setting

class CustomToggleButton(context : Context, attr : AttributeSet)
    : View(context, attr) {

    private val backgroundPaint = Paint()
    private val buttonPaint = Paint()
    private var isLeftSelected = true

    private val buttonRadius = 23f // 원 버튼의 반지름
    private val buttonMargin = 5f // 원 버튼과 둥근 직사각형의 간격

    interface OnToggleChangedListener {
        fun toggleChanged(isOn : Boolean)
    }

    private var toggleChangedListener : OnToggleChangedListener? = null

    fun setOnToggleChangedListener(listener : OnToggleChangedListener) {
        toggleChangedListener = listener
    }

    init {
        buttonPaint.color = Color.WHITE
        backgroundPaint.color = Color.parseColor(setting.themeColor)
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 터치 이벤트가 발생하면 토글 실행
                toggle()
                toggleChangedListener?.toggleChanged(isLeftSelected)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // 토글 상태 변경
    fun toggle() {
        isLeftSelected = !isLeftSelected
        setColor()
        animateToggle() // 애니메이션을 적용하여 부드럽게 이동
    }

    private fun setColor() {
        if(!isLeftSelected){
            backgroundPaint.color = Color.LTGRAY
        }
        else{
            backgroundPaint.color = Color.parseColor(setting.themeColor)
        }
    }

    // 토글 애니메이션 적용
    private fun animateToggle() {
        // 원형 버튼의 현재 x 좌표
        val currentX = if (isLeftSelected) buttonMargin + buttonRadius else width - buttonRadius - buttonMargin

        // 목표 x 좌표
        val targetX = if (isLeftSelected) width - buttonRadius - buttonMargin else buttonMargin + buttonRadius

        // ValueAnimator를 사용하여 애니메이션 적용
        val animator = ValueAnimator.ofFloat(currentX, targetX)
        animator.duration = 300 // 애니메이션 기간 (ms)
        animator.interpolator = AccelerateDecelerateInterpolator()

        // 애니메이션 진행 중 값이 변경될 때 호출되는 리스너
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            setButtonX(animatedValue)
        }

        animator.start()
    }

    // 원형 버튼의 x 좌표를 설정하는 메서드
    fun setButtonX(x: Float) {
        if (isLeftSelected) {
            buttonMargin + buttonRadius
        } else {
            width - buttonRadius - buttonMargin
        }
        invalidate() // 뷰를 다시 그리도록 갱신
    }

    // 테마 색상 업데이트
    fun updateThemeColor(themeColor: String) {
        backgroundPaint.color = Color.parseColor(themeColor)
        invalidate()
    }

    fun isLeftSelected(): Boolean {
        return isLeftSelected
    }

    fun setToggleOn(isOn : Boolean) {
        isLeftSelected = !isOn
        toggle()
    }

}