package space.jay.bingle.modules.loading

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import space.jay.bingle.R

class ViewLoading(context: Context?, attrs: AttributeSet?) : SurfaceView(context, attrs),
    SurfaceHolder.Callback {

    private var mDrawThread: DrawThread? = null
    private var mCenterX = 0f
    private var mCenterY = 0f

    init {
        //화면 가운데 확인
        context?.resources?.displayMetrics?.also {
            mCenterX = (it.widthPixels / 2).toFloat()
            mCenterY = (it.heightPixels / 2).toFloat()
        }

        //surface view 투명하게 만들기
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSLUCENT)

        //콜백 추가
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mDrawThread = DrawThread()
        mDrawThread?.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mDrawThread = null
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    inner class DrawThread : Thread() {

        override fun run() {

            val rectF = RectF()
            val paint = Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = 30f
                isAntiAlias = true
            }
            val penBackground = Paint().apply {
                style = Paint.Style.FILL
                color = Color.WHITE
                isAntiAlias = true
            }

            val pen = Paint().apply {
                textAlign = Paint.Align.CENTER
                textSize = 30f
                isAntiAlias = true
            }

            var i = 0
            var degrees1 = 0f
            var degrees2 = 0f
            var degrees3 = 0f
            while (mDrawThread != null) {

                val canvas: Canvas? = holder.lockCanvas()
                canvas?.also {
                    //화면 지우기
                    it.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

                    //중앙 화면 원 및 로딩 텍스트 그리기
                    canvas.drawArc(mCenterX - 80f, mCenterY - 80f, mCenterX + 80f, mCenterY + 80f, 0f, 360f, true, penBackground)
                    canvas.drawText(context.getString(R.string.loading), mCenterX, mCenterY+15, pen)

                    //돌아가는 로딩바 그리기
                    drawLoading(it, paint, Color.WHITE, rectF, 200, degrees1)
                    drawLoading(it, paint, Color.YELLOW, rectF, 160, degrees2)
                    drawLoading(it, paint, Color.MAGENTA, rectF, 120, degrees3)

                    //각도별 총합이 32가 되게 맞추자
                    when {
                        i < 90 -> {
                            degrees1 += 1
                            degrees2 += 2
                            degrees3 += 3
                        }
                        i < 180 -> {
                            degrees1 += 5
                            degrees2 += 2
                            degrees3 += 3
                        }
                        i < 270 -> {
                            degrees1 += 6
                            degrees2 += 2
                            degrees3 += 4
                        }
                        i < 360 -> {
                            degrees1 += 4
                            degrees2 += 6
                            degrees3 += 6
                        }
                        i < 450 -> {
                            degrees1 += 1
                            degrees2 += 6
                            degrees3 += 3
                        }
                        i < 540 -> {
                            degrees1 += 5
                            degrees2 += 6
                            degrees3 += 3
                        }
                        i < 630 -> {
                            degrees1 += 6
                            degrees2 += 6
                            degrees3 += 6
                        }
                        i < 720 -> {
                            degrees1 += 4
                            degrees2 += 2
                            degrees3 += 4
                        }
                        i < 730 -> {}
                        else -> {
                            i = 0
                        }
                    }

                    degrees1 %= 360
                    degrees2 %= 360
                    degrees3 %= 360
                    i++
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        private fun drawLoading(canvas: Canvas, paint: Paint, color: Int, rectF: RectF, rectSize: Int, degrees: Float) {
            paint.color = color
            rectF.set(mCenterX - rectSize, mCenterY - rectSize, mCenterX + rectSize, mCenterY + rectSize)
            canvas.save()
            canvas.rotate(degrees, mCenterX, mCenterY)
            canvas.drawArc(rectF, 200f, 180f, false, paint)
            canvas.restore()
        }
    }
}