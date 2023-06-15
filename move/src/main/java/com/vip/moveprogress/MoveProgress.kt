package com.vip.moveprogress

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt


/**
 *AUTHOR:AbnerMing
 *DATE:2023/3/20
 *INTRODUCE:运行进度
 */
class MoveProgress : View {

    private var mPaint: Paint? = null//画笔

    private var mIconPaint: Paint? = null//图片画笔

    private var mProgressPaint: Paint? = null//进度画笔

    private var mGraduationPaint: Paint? = null//刻度画笔

    private var mProgressHeight = 48f//进度条的高度

    private var mDefaultHeight = 100f//默认的高度

    private var mProgressThumb: Int? = null//拖拽的Icon

    private var mProgressMarginTopBottom = 12f//默认距离上下的距离

    private var mProgressMarginLeftRight = 32f//默认距离左右的距离

    private var mProgressRadius = 4f//默认圆角

    private var mProgressBackground = ContextCompat.getColor(context, R.color.text_d8d9e8)//默认的背景颜色

    private var mIntervalColor = ContextCompat.getColor(context, R.color.text_10000000)//间隔线颜色

    private var mIntervalSelectColor =
        ContextCompat.getColor(context, R.color.text_10000000)//间隔线选中颜色

    private var mIntervalParentLeftRight = 18f//离散分割线距离左右的距离

    private var mIntervalSize = 6//间隔线数量

    private var mIntervalWidth = 4f//间隔线宽度

    private var mIntervalMarginTopBottom = 14f//间隔线边距,距离上下

    private var mColorArray = intArrayOf()//颜色集合,拖拽时的颜色集合

    private var mMaxProgress = 60//默认最大值

    private var mDefaultProgress = 0//默认进度

    private var mMoveOldX = 0f//记录上一次手指抬起的x坐标

    private var mMoveProgress = 0f

    private var mGraduationMarginTop = 0f//刻度尺距离上边的距离

    private var mGraduationResult = 1//刻度结果

    private var mIsGraduation = true//默认展示刻度值

    private var mGraduationTextSize = 28f//刻度尺文字大小

    //刻度尺文字颜色
    private var mGraduationTextColor = ContextCompat.getColor(context, R.color.text_999999)

    //刻度尺文字选中颜色
    private var mGraduationSelectTextColor = ContextCompat.getColor(context, R.color.text_8548D2)

    //刻度尺分割值段
    private var mGraduationSection = 10

    private var mGraduationSectionZero = false//刻度尺是否从0开始

    private var mHideGraduationSectionCenter = false//刻度尺是否隐藏中间

    private var mThumbWidth = 0f//icon的宽

    private var mThumbHeight = 0f//icon的高

    private var mThumbMarginTop = 0f//icon距离上边的高度

    private var mProgressThumbWidth = 0//icon的宽

    private var mProgressThumbSpacing = 0f//图片的内边距

    private var mDisallowIntercept = true//是否拦截事件

    private var mProgressIsIntercept = false//是否禁止拖拽

    constructor(
        context: Context
    ) : super(context) {
        initData()
    }

    constructor(
        context: Context, attrs: AttributeSet?
    ) : super(context, attrs) {
        val osa = context.obtainStyledAttributes(attrs, R.styleable.MoveProgress)
        osa.apply {
            //View视图的高度
            mDefaultHeight = getDimension(R.styleable.MoveProgress_ms_height, mDefaultHeight)
            //进度条的高度
            mProgressHeight =
                getDimension(R.styleable.MoveProgress_ms_progress_height, mProgressHeight)
            //获取进度条的拖拽Icon
            mProgressThumb =
                getResourceId(R.styleable.MoveProgress_ms_progress_thumb, R.drawable.view_ic_thumb)
            //进度条距离上下的距离
            mProgressMarginTopBottom = getDimension(
                R.styleable.MoveProgress_ms_progress_margin_top_bottom, mProgressMarginTopBottom
            )
            //进度条距离左右的距离
            mProgressMarginLeftRight = getDimension(
                R.styleable.MoveProgress_ms_progress_margin_left_right, mProgressMarginLeftRight
            )
            //进度条的圆角
            mProgressRadius = getDimension(
                R.styleable.MoveProgress_ms_progress_radius, mProgressRadius
            )
            //进度条的背景颜色
            mProgressBackground =
                getColor(R.styleable.MoveProgress_ms_progress_background, mProgressBackground)

            //间隔线颜色
            mIntervalColor = getColor(R.styleable.MoveProgress_ms_interval_color, mIntervalColor)

            //间隔线选中颜色
            mIntervalSelectColor =
                getColor(R.styleable.MoveProgress_ms_interval_select_color, mIntervalSelectColor)

            //间隔线距离父View的左右距离
            mIntervalParentLeftRight = getDimension(
                R.styleable.MoveProgress_ms_interval_parent_margin_left_right,
                mIntervalParentLeftRight
            )
            //间隔线数量
            mIntervalSize = getInt(
                R.styleable.MoveProgress_ms_interval_size, mIntervalSize
            )
            //间隔线的宽度
            mIntervalWidth = getDimension(
                R.styleable.MoveProgress_ms_interval_width, mIntervalWidth
            )
            //间隔线的上下边距
            mIntervalMarginTopBottom = getDimension(
                R.styleable.MoveProgress_ms_interval_margin_top_bottom, mIntervalMarginTopBottom
            )
            //获取定义的颜色数组
            val resourceId = getResourceId(R.styleable.MoveProgress_ms_progress_move_color, 0)
            if (resourceId != 0) {
                mColorArray = resources.getIntArray(resourceId)
            }
            //获取最大进度
            mMaxProgress = getInt(
                R.styleable.MoveProgress_ms_progress_max, mMaxProgress
            )
            //获取最大进度
            mDefaultProgress = getInt(
                R.styleable.MoveProgress_ms_progress_default, mDefaultProgress
            )
            //是否展示刻度值
            mIsGraduation = getBoolean(
                R.styleable.MoveProgress_ms_is_graduation, mIsGraduation
            )

            //刻度尺文字大小
            mGraduationTextSize = getDimension(
                R.styleable.MoveProgress_ms_graduation_text_size, mGraduationTextSize
            )

            //刻度尺文字颜色
            mGraduationTextColor =
                getColor(R.styleable.MoveProgress_ms_graduation_text_color, mGraduationTextColor)
            //刻度尺文字选中颜色
            mGraduationSelectTextColor = getColor(
                R.styleable.MoveProgress_ms_graduation_select_text_color, mGraduationSelectTextColor
            )
            //刻度尺段
            mGraduationSection = getInt(
                R.styleable.MoveProgress_ms_graduation_section, mGraduationSection
            )
            //刻度尺段是否从零开始
            mGraduationSectionZero = getBoolean(
                R.styleable.MoveProgress_ms_graduation_section_zero, false
            )
            //刻度尺中间是否隐藏
            mHideGraduationSectionCenter = getBoolean(
                R.styleable.MoveProgress_ms_graduation_hide_center, false
            )

            //icon的宽
            mThumbWidth = getDimension(
                R.styleable.MoveProgress_ms_progress_thumb_width, 0f
            )
            //icon的高
            mThumbHeight = getDimension(
                R.styleable.MoveProgress_ms_progress_thumb_height, 0f
            )
            //icon距离上边的高
            mThumbMarginTop = getDimension(
                R.styleable.MoveProgress_ms_progress_thumb_margin_top, mThumbMarginTop
            )
            //克服值距离上边的距离
            mGraduationMarginTop = getDimension(
                R.styleable.MoveProgress_ms_graduation_margin_top, mGraduationMarginTop
            )

            //图片的内边距
            mProgressThumbSpacing = getDimension(
                R.styleable.MoveProgress_ms_progress_thumb_spacing, mProgressThumbSpacing
            )

            //是否拦截
            mDisallowIntercept = getBoolean(
                R.styleable.MoveProgress_ms_progress_disallow_intercept, mDisallowIntercept
            )

            //是否禁止拖拽
            mProgressIsIntercept = getBoolean(
                R.styleable.MoveProgress_ms_progress_is_intercept, mProgressIsIntercept
            )

        }

        if (!mIsGraduation) {
            //如果不显示刻度值，那么就要和进度条保持一致
            mDefaultHeight = mProgressHeight
        }

        initData()
    }

    private fun initData() {
        mPaint = getPaint()

        mIconPaint = getPaint()

        mProgressPaint = getPaint()

        mGraduationPaint = getPaint()

        mGraduationPaint?.apply {
            color = mGraduationTextColor
            textSize = mGraduationTextSize
        }

        if (mDefaultProgress != 0) {
            changeProgress(mDefaultProgress)
        }

    }


    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:返回画笔
     */

    private fun getPaint(): Paint {
        val paint = Paint()
        paint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        return paint
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //绘制背景
        canvasBackground(canvas!!)
        //绘制离散间隔
        canvasIntervalLine(canvas, false)
        //绘制进度
        canvasMoveProgress(canvas)
        //绘制进度离散间隔
        canvasIntervalLine(canvas, true)
        //绘制移动的图标
        canvasMoveIcon(canvas)

    }


    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:绘制进度
     */
    private fun canvasMoveProgress(canvas: Canvas) {
        //为空
        if (mColorArray.isEmpty()) {
            mColorArray = intArrayOf(
                ContextCompat.getColor(context, R.color.text_ff3e3e93),
                ContextCompat.getColor(context, R.color.text_ff8548d2),
            )
        }
        val linearShader = LinearGradient(
            0f,
            0f,
            mMoveProgress + mProgressMarginLeftRight,
            mProgressHeight,
            mColorArray,
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        mProgressPaint!!.shader = linearShader


        //等于0时
        val rect = RectF()
        rect.left = mProgressMarginLeftRight
        rect.top = mProgressMarginTopBottom
        rect.right = mMoveProgress + mProgressMarginLeftRight
        rect.bottom = mProgressHeight + mProgressMarginTopBottom
        canvas.drawRoundRect(rect, mProgressRadius, mProgressRadius, mProgressPaint!!)


        //计算比例
        //  val viewWidth = getViewWidth() - mIntervalParentLeftRight * 2
        //val endProgress = (mMoveProgress - mIntervalParentLeftRight) / viewWidth
        mGraduationResult =
            ((mMoveProgress / getViewWidth()) * mMaxProgress).roundToInt()//(endProgress * mMaxProgress).roundToInt()

        if (mGraduationResult < 1) {
            mGraduationResult = if (mGraduationSectionZero) {
                0
            } else {
                1
            }
        }
        if (mGraduationResult >= mMaxProgress) {
            mGraduationResult = mMaxProgress
        }

        mMoveProgressCallback?.invoke(mGraduationResult)

    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:键盘完成
     */
    private var mMoveProgressCallback: ((Int) -> Unit?)? = null
    fun getMoveProgress(block: (progress: Int) -> Unit) {
        mMoveProgressCallback = block
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:改变进度
     */
    fun changeProgress(progress: Int) {
        var pro = progress
        if (pro > mMaxProgress) {
            pro = mMaxProgress
        }
        post {
            val viewWidth = getViewWidth()//获取宽度
            val rect = viewWidth / mMaxProgress//一个宽度为多少
            mMoveProgress = pro * rect//设置的宽度
            mMoveOldX = mMoveProgress
            postInvalidate()
        }
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:获取当前进度
     */
    fun getProgress(): Int {
        return mGraduationResult
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:绘制移动的图标
     */
    private fun canvasMoveIcon(canvas: Canvas) {
        mProgressThumb?.let {
            var decodeResource = BitmapFactory.decodeResource(resources, it)
            mProgressThumbWidth = decodeResource.width
            if (mThumbWidth != 0f) {
                val height: Int = decodeResource.height
                // 设置想要的大小
                val newWidth = mThumbWidth
                val newHeight = mThumbHeight
                // 计算缩放比例
                val scaleWidth = newWidth / width
                val scaleHeight = newHeight / height
                // 取得想要缩放的matrix参数
                val matrix = Matrix()
                matrix.postScale(scaleWidth, scaleHeight)
                // 得到新的图片
                decodeResource =
                    Bitmap.createBitmap(decodeResource, 0, 0, width, height, matrix, true)

            }

            var mThumpLeft = mMoveProgress
            if (mThumpLeft < (mProgressThumbWidth / 2 - mIntervalParentLeftRight + mProgressThumbSpacing)) {
                mThumpLeft =
                    mProgressThumbWidth / 2 - mIntervalParentLeftRight + mProgressThumbSpacing
            }

            if (mThumpLeft > (getViewWidth() - mIntervalParentLeftRight + mProgressThumbSpacing)) {
                mThumpLeft = getViewWidth() - mIntervalParentLeftRight + mProgressThumbSpacing
            }

            canvas.drawBitmap(
                decodeResource, mThumpLeft, mThumbMarginTop, mIconPaint!!
            )
        }
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:绘制离散间隔
     */
    private fun canvasIntervalLine(canvas: Canvas, isCanvas: Boolean) {
        val rect =
            (width - mProgressMarginLeftRight * 2 - mIntervalParentLeftRight * 2) / mIntervalSize
        if (isCanvas) {
            mPaint!!.color = mIntervalSelectColor
        } else {
            mPaint!!.color = mIntervalColor
        }

        mPaint!!.strokeWidth = mIntervalWidth
        for (a in 0..mIntervalSize) {
            val x = (rect * a) + mProgressMarginLeftRight + mIntervalParentLeftRight
            val y = mIntervalMarginTopBottom + mProgressMarginTopBottom
            canvas.drawLine(
                x,
                y,
                x,
                mProgressHeight + mProgressMarginTopBottom - mIntervalMarginTopBottom,
                mPaint!!
            )
            //绘制刻度值
            if (mIsGraduation && isCanvas) {

                if (mHideGraduationSectionCenter && (a != 0 && a != mIntervalSize)) {
                    //隐藏中间
                    continue
                }

                var graduation = a * mGraduationSection
                //是否从0开始记录
                if (graduation == 0 && !mGraduationSectionZero) {
                    graduation = 1
                }

                //如果移动到了，改变颜色


                if (mGraduationResult >= graduation && mGraduationResult < graduation + mGraduationSection) {
                    mGraduationPaint?.color = mGraduationSelectTextColor
                } else {
                    mGraduationPaint?.color = mGraduationTextColor
                }


                val text = graduation.toString()
                val rectText = Rect()
                mGraduationPaint!!.getTextBounds(text, 0, text.length, rectText)
                val textWidth = rectText.width()
                val textHeight = rectText.height()
                canvas.drawText(
                    text,
                    x - textWidth / 2,
                    mProgressHeight + mProgressMarginTopBottom * 2 + textHeight + mGraduationMarginTop,
                    mGraduationPaint!!
                )

            }

        }
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:绘制背景
     */
    private fun canvasBackground(canvas: Canvas) {
        mPaint!!.color = mProgressBackground
        val rect = RectF().apply {
            left = mProgressMarginLeftRight
            top = mProgressMarginTopBottom
            right = width.toFloat() - mProgressMarginLeftRight
            bottom = mProgressHeight + mProgressMarginTopBottom
        }
        canvas.drawRoundRect(rect, mProgressRadius, mProgressRadius, mPaint!!)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        //如果为true直接返回，不进行拖拽
        if (mProgressIsIntercept) {
            return mProgressIsIntercept
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(mDisallowIntercept)
                val downX = getChangeX(event.x)
                val startX = mMoveOldX - mProgressMarginLeftRight
                val endX = mMoveOldX + mProgressMarginLeftRight
                return downX in startX..endX
            }
            MotionEvent.ACTION_MOVE -> {
                //移动
                var moveX = getChangeX(event.x)
                //滑动至最右边
                //计算最后边的坐标
                val viewWidth = getViewWidth()

                if (moveX >= viewWidth) {
                    moveX = viewWidth
                }

                mMoveProgress = moveX

                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                //手指谈起
                mMoveOldX = getChangeX(event.x)

                val viewWidth = getViewWidth()

                if (mMoveOldX >= viewWidth) {
                    mMoveOldX = viewWidth
                }
            }
        }
        return true
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:获取X
     */
    private fun getChangeX(x: Float): Float {
        var changeX = x
        //滑动至最左边,就等于最左边
        if (changeX <= (left + mProgressMarginLeftRight)) {
            changeX = left + mProgressMarginLeftRight
        }
        //进行相减得到最终的坐标
        changeX -= (left + mProgressMarginLeftRight)
        return changeX
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:获取View的宽
     */
    private fun getViewWidth(): Float {
        var w = width
        if (w == 0) {
            w = getScreenWidth()
        }
        return w - left * 2 - mProgressMarginLeftRight * 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var windowHeight = heightMeasureSpec
        if (heightMode == MeasureSpec.AT_MOST) {
            windowHeight = mDefaultHeight.toInt()//默认的高度
        }
        setMeasuredDimension(widthMeasureSpec, windowHeight)
    }

    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:获取屏幕的宽
     */
    private fun getScreenWidth(): Int {
        return resources.displayMetrics.widthPixels
    }


    /**
     * AUTHOR:AbnerMing
     * INTRODUCE:设置是否进行拦截
     */
    fun setProgressIsIntercept(progressIsIntercept: Boolean) {
        mProgressIsIntercept = progressIsIntercept
    }
}