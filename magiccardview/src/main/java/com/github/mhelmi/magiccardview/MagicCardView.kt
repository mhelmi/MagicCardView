package com.github.mhelmi.magiccardview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Shader.TileMode.MIRROR
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.ShapeDrawable.ShaderFactory
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.github.mhelmi.magiccardview.R.styleable
import java.util.Arrays

class MagicCardView : FrameLayout {

  companion object {
    private const val TAG = "MagicCardView"
    const val MODE_NORMAL_BG = 0
    const val MODE_GRADIENT_BG = 1
  }

  private var mode = 0
  private var bgColor = 0
  private var gradientStartColor = 0
  private var gradientEndColor = 0
  private var shadowColor = 0
  private var rippleColor = 0
  private var cornerRadius = 0
  private var cornerTopLeftRadius = 0
  private var cornerTopRightRadius = 0
  private var cornerBottomRightRadius = 0
  private var cornerBottomLeftRadius = 0
  private var shadowRadius = 0
  private var shadowGravity = 0

  constructor(context: Context) : super(context) {
    init(context, null)
  }

  constructor(
    context: Context,
    attrs: AttributeSet?
  ) : super(context, attrs) {
    init(context, attrs)
  }

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr) {
    init(context, attrs)
  }

  @RequiresApi(VERSION_CODES.LOLLIPOP) constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
  ) : super(context, attrs, defStyleAttr, defStyleRes) {
    init(context, attrs)
  }

  private fun init(
    context: Context,
    set: AttributeSet?
  ) {
    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    val typedArray =
      context.obtainStyledAttributes(set, styleable.MagicCardView, 0, 0)
    mode = typedArray.getInt(
        styleable.MagicCardView_mcv_backgroundMode, MODE_NORMAL_BG
    )
    bgColor = typedArray.getColor(styleable.MagicCardView_mcv_backgroundColor, 0)
    gradientStartColor = typedArray.getColor(styleable.MagicCardView_mcv_gradientStartColor, 0)
    gradientEndColor = typedArray.getColor(styleable.MagicCardView_mcv_gradientEndColor, 0)
    shadowColor = typedArray.getColor(styleable.MagicCardView_mcv_shadowColor, 0)
    cornerRadius = typedArray.getDimension(styleable.MagicCardView_mcv_cornerRadius, 0f)
        .toInt()
    cornerTopLeftRadius =
      typedArray.getDimension(styleable.MagicCardView_mcv_cornerTopLeftRadius, 0f)
          .toInt()
    cornerTopRightRadius =
      typedArray.getDimension(styleable.MagicCardView_mcv_cornerTopRightRadius, 0f)
          .toInt()
    cornerBottomRightRadius =
      typedArray.getDimension(styleable.MagicCardView_mcv_cornerBottomRightRadius, 0f)
          .toInt()
    cornerBottomLeftRadius =
      typedArray.getDimension(styleable.MagicCardView_mcv_cornerBottomLeftRadius, 0f)
          .toInt()
    shadowRadius = typedArray.getDimension(styleable.MagicCardView_mcv_shadowRadius, 0f)
        .toInt()
    shadowGravity = typedArray.getInt(styleable.MagicCardView_mcv_shadowGravity, Gravity.CENTER)
    rippleColor = typedArray.getColor(styleable.MagicCardView_mcv_rippleColor, 0)
    typedArray.recycle()
    setCardBackground()
  }

  private fun setCardBackground() {
    val shapeDrawable = ShapeDrawable()
    val shapeDrawablePaint = shapeDrawable.paint
    shapeDrawablePaint.isAntiAlias = true
    handleBackGroundColor(shapeDrawable, shapeDrawablePaint)
    handleShadowPadding(shapeDrawable, shadowGravity)
    handleShadowGravity(shapeDrawablePaint)
    background = createDrawable(shapeDrawable, getShapeRadius())
  }

  @SuppressLint("RtlHardcoded")
  private fun handleShadowPadding(
    shapeDrawable: ShapeDrawable,
    shadowGravity: Int
  ) {
    shapeDrawable.apply {
      when (shadowGravity) {
        Gravity.CENTER -> setPadding(
            shadowRadius, shadowRadius, shadowRadius, shadowRadius
        )
        Gravity.LEFT -> setPadding(shadowRadius, 0, 0, 0)
        Gravity.TOP -> setPadding(0, shadowRadius, 0, 0)
        Gravity.RIGHT -> setPadding(0, 0, shadowRadius, 0)
        Gravity.BOTTOM -> setPadding(0, 0, 0, shadowRadius)
      }
    }
  }

  private fun handleBackGroundColor(
    shapeDrawable: ShapeDrawable,
    shapeDrawablePaint: Paint
  ) {
    when (mode) {
      MODE_NORMAL_BG -> shapeDrawablePaint.color = bgColor
      MODE_GRADIENT_BG -> shapeDrawable.shaderFactory = object : ShaderFactory() {
        override fun resize(
          width: Int,
          height: Int
        ): Shader {
          return LinearGradient(
              0f, 0f, width.toFloat(), 0f,
              gradientStartColor, gradientEndColor, MIRROR
          )
        }
      }
    }
  }

  @SuppressLint("RtlHardcoded")
  private fun handleShadowGravity(
    shapeDrawablePaint: Paint
  ) {
    var dx = 0.0f
    var dy = 0.0f
    when (shadowGravity) {
      Gravity.CENTER -> {
        dx = 0.0f
        dy = 0.0f
      }
      Gravity.LEFT -> {
        dx = -shadowRadius / 2.toFloat()
        dy = 0.0f
      }
      Gravity.TOP -> {
        dx = 0.0f
        dy = -shadowRadius / 2.toFloat()
      }
      Gravity.RIGHT -> {
        dx = shadowRadius / 2.toFloat()
        dy = 0.0f
      }
      Gravity.BOTTOM -> {
        dx = 0.0f
        dy = shadowRadius / 2.toFloat()
      }
    }
    shapeDrawablePaint.setShadowLayer(shadowRadius.toFloat(), dx, dy, shadowColor)
  }

  private fun getShapeRadius(): FloatArray {
    return if (cornerRadius == 0
        && (cornerTopLeftRadius != 0
            || cornerTopRightRadius != 0
            || cornerBottomRightRadius != 0
            || cornerBottomLeftRadius != 0)
    ) {
      floatArrayOf(
          cornerTopLeftRadius.toFloat(), cornerTopLeftRadius.toFloat(),
          cornerTopRightRadius.toFloat(), cornerTopRightRadius.toFloat(),
          cornerBottomRightRadius.toFloat(), cornerBottomRightRadius.toFloat(),
          cornerBottomLeftRadius.toFloat(), cornerBottomLeftRadius
          .toFloat()
      )
    } else {
      val outerRadius = FloatArray(8)
      Arrays.fill(outerRadius, cornerRadius.toFloat())
      outerRadius
    }
  }

  @SuppressLint("RtlHardcoded")
  private fun createDrawable(
    shapeDrawable: ShapeDrawable,
    outerRadius: FloatArray
  ): Drawable {
    val shape = RoundRectShape(outerRadius, null, null)
    shapeDrawable.shape = shape
    val drawable: LayerDrawable
    drawable = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      val colorStateList = ColorStateList.valueOf(rippleColor)
      val mask = ShapeDrawable(shape)
      RippleDrawable(colorStateList, shapeDrawable, mask)
    } else {
      LayerDrawable(arrayOf<Drawable>(shapeDrawable))
    }
    val padding = shadowRadius
    when (shadowGravity) {
      Gravity.CENTER -> drawable.setLayerInset(0, padding, padding, padding, padding)
      Gravity.LEFT -> drawable.setLayerInset(0, padding, 0, 0, 0)
      Gravity.TOP -> drawable.setLayerInset(0, 0, padding, 0, 0)
      Gravity.RIGHT -> drawable.setLayerInset(0, 0, 0, padding, 0)
      Gravity.BOTTOM -> drawable.setLayerInset(0, 0, 0, 0, padding)
    }
    return drawable
  }

}