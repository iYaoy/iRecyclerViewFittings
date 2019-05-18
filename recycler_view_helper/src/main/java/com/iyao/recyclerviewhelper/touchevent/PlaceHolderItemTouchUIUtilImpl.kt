package com.iyao.recyclerviewhelper.touchevent

import android.graphics.*
import android.os.Build
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class PlaceHolderItemTouchUIUtilImpl : BaseItemTouchUIUtilImpl() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val inRect = Rect()
    private val outRectF = RectF()
    var radius = 0f
    var drawPlaceHolder = true
    var placeHolder = Bitmap.createBitmap(intArrayOf(Color.GRAY, Color.LTGRAY), 1, 2, Bitmap.Config.ARGB_4444)

    override fun onDraw(c: Canvas, recyclerView: RecyclerView, view: View, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (isCurrentlyActive && drawPlaceHolder) {
            c.run {
                inRect.apply {
                    view.getDrawingRect(this)
                    offset(view.left, view.top)
                }.also {
                    outRectF.set(it)
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    saveLayer(outRectF, paint)
                } else {
                    saveLayer(outRectF, paint, Canvas.ALL_SAVE_FLAG)
                }.also {
                    drawRoundRect(outRectF, radius, radius, paint)
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    drawBitmap(placeHolder, inRect.apply {
                        set(0, 0, placeHolder.width, placeHolder.height)
                    }, outRectF, paint)
                    paint.xfermode = null
                    restoreToCount(it)
                }
            }
        }
        super.onDraw(c, recyclerView, view, dX, dY, actionState, isCurrentlyActive)
    }
}