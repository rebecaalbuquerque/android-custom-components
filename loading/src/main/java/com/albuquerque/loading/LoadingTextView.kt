package com.albuquerque.loading

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.layout_loading_text_view.view.*


class LoadingTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var text: CharSequence = ""
        get() = textField.text
        set(value) {
            textField.text = value
            field = value
        }

    init {
        View.inflate(context, R.layout.layout_loading_text_view, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.LoadingTextView)

        // Configurando cor da view de loading
        (loading.background as GradientDrawable).setColor(attributes.getColor(R.styleable.LoadingTextView_ltvLoadingTint, Color.GRAY))

        // Configurando o texto
        textField.text = attributes.getString(R.styleable.LoadingTextView_android_text) ?: ""

        // Configurando o fontFamily do texto. As fontes tem que estar na pasta assets
        attributes.getString(R.styleable.LoadingTextView_android_fontFamily)?.let {
            getContext()?.let { ctx ->
                try {
                    textField.typeface = Typeface.createFromAsset(ctx.assets, "fonts/${it.split("/".last())}")
                } catch (e: Exception) {
                    textField.typeface = Typeface.DEFAULT
                }
            }
        }

        // Configurando o tamanho do texto
        attributes.getDimensionPixelSize(R.styleable.LoadingTextView_android_textSize, -1).let { size ->
            if(size > -1) textField.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.toFloat())
        }

        // Configurando cor do texto
        textField.setTextColor(attributes.getColor(R.styleable.LoadingTextView_android_textColor, Color.GRAY))

        // Configurando minWidth do texto
        attributes.getDimensionPixelSize(R.styleable.LoadingTextView_android_minWidth, -1).let { size ->
            if(size > -1) textField.minWidth = size
        }

        // Configurando alinhamento do texto
        attributes.getInteger(R.styleable.LoadingTextView_ltvTextAlignment, -1).let { alignment ->
            if(alignment > -1)
                textField.textAlignment = alignment
        }

        attributes.recycle()
    }

    fun setText(value: String) {
        textField.text = value
    }

    fun showLoading() {

        textField.visibility = View.INVISIBLE
        loading.visibility = View.VISIBLE

        loading.startAnimation(
            AlphaAnimation(1.0f, 0.6f).apply {
                this.duration = 900
                this.startOffset = 20
                this.repeatMode = Animation.REVERSE
                this.repeatCount = Animation.INFINITE
            }
        )

    }

    fun hideLoading() {
        textField.visibility = View.VISIBLE
        loading.visibility = View.INVISIBLE
        loading.animation?.cancel()
    }

}