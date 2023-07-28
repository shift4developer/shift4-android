package com.shift4.checkout.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.shift4.R

internal class SwitchComponent @JvmOverloads constructor(
    context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private lateinit var textViewSwitchTitle: TextView
    private lateinit var textViewSwitchSubtitle: TextView
    private lateinit var switchRememberCard: SwitchCompat

    var onCheckedListener: (Boolean) -> Unit = {}

   init {
        LayoutInflater.from(context).inflate(R.layout.com_shift4_layout_switch, this, true)


        textViewSwitchTitle = findViewById<TextView>(R.id.textViewSwitchTitle)
        textViewSwitchSubtitle = findViewById<TextView>(R.id.textViewSwitchSubtitle)
        switchRememberCard = findViewById<SwitchCompat>(R.id.switchRememberCard)

        context.theme.obtainStyledAttributes(
            attrs, R.styleable.Shift4SwitchComponent, 0, 0
        ).apply {

            try {
                textViewSwitchTitle.text = getString(R.styleable.Shift4SwitchComponent_title)
                val subtitle = getString(R.styleable.Shift4SwitchComponent_subtitle) ?: ""
                textViewSwitchSubtitle.text = subtitle
                textViewSwitchSubtitle.visibility =
                    if (subtitle.isEmpty()) View.GONE else View.VISIBLE
            } finally {
                recycle()
            }
        }
        switchRememberCard.setOnCheckedChangeListener { _, value ->
            onCheckedListener(value)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        switchRememberCard.isEnabled = enabled
    }

    override fun isEnabled(): Boolean {
        return switchRememberCard.isEnabled
    }

    var checked: Boolean
        get() {
            return switchRememberCard.isChecked
        }
        set(value) {
            switchRememberCard.isChecked = value
        }
}