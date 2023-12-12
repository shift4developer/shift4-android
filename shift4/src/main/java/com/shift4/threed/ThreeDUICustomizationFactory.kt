package com.shift4.threed

import android.content.Context
import androidx.annotation.DimenRes
import com.shift4.R
import com.shift4.threedsecure.pub.UiCustomization

internal class ThreeDUICustomizationFactory(val context: Context) {
    fun createUICustimization(): UiCustomization {
        val uiCustomization = UiCustomization(context)

        uiCustomization.getToolbarCustomization().setBackgroundColor(context.resources.getString(R.color.com_shift4_background))

        uiCustomization.getToolbarCustomization().setTextColor(
            context.resources.getString(R.color.com_shift4_text_primary))
        uiCustomization.getToolbarCustomization().setHeaderText("3D Secure") // TODO: translation
        uiCustomization.getToolbarCustomization().setButtonText("Cancel") // TODO: translation
        uiCustomization.getToolbarCustomization().setTextFontSize(dimension(R.dimen.com_shift4_font_size_button))
        uiCustomization.getToolbarCustomization().setTextFontName("shift4_3ds_nunitosans_semibold.ttf")
        
        return uiCustomization
    }

    private fun dimension(@DimenRes id: Int): Int {
        val scaleRatio: Float = context.resources.displayMetrics.density
        return (context.resources.getDimension(id)/scaleRatio).toInt()
    }
}