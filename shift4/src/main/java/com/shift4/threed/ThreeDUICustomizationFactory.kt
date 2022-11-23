package com.shift4.threed

import android.content.Context
import android.view.View
import com.nsoftware.ipworks3ds.sdk.customization.UiCustomization
import com.shift4.R

internal class ThreeDUICustomizationFactory(val context: Context) {
    fun createUICustimization(): UiCustomization {
        val uiCustomization = UiCustomization()

        customizeButtons(uiCustomization, context)

        val labelCustomization = uiCustomization.labelCustomization
        labelCustomization.headingTextColor = context.resources.getString(R.color.com_shift4_black)
        labelCustomization.headingTextFontName = "shift4_3ds_nunitosans_semibold.ttf"
        labelCustomization.headingTextAlignment = View.TEXT_ALIGNMENT_TEXT_START
        labelCustomization.headingTextFontSize = 18

        labelCustomization.setTextFontName(UiCustomization.LabelType.INFO_LABEL, "shift4_3ds_nunitosans_semibold.ttf")
        labelCustomization.setTextFontSize(UiCustomization.LabelType.INFO_LABEL, 14)
        labelCustomization.setTextColor(UiCustomization.LabelType.INFO_LABEL, context.resources.getString(R.color.com_shift4_black))

        labelCustomization.setTextFontName(UiCustomization.LabelType.INFO_TEXT, "shift4_3ds_nunitosans_regular.ttf")
        labelCustomization.setTextFontSize(UiCustomization.LabelType.INFO_TEXT, 14)
        labelCustomization.setTextColor(UiCustomization.LabelType.INFO_TEXT, context.resources.getString(R.color.com_shift4_black))

        labelCustomization.setTextFontName(UiCustomization.LabelType.SELECTION_LIST, "shift4_3ds_nunitosans_regular.ttf")
        labelCustomization.setTextFontSize(UiCustomization.LabelType.SELECTION_LIST, 14)
        labelCustomization.setTextColor(UiCustomization.LabelType.SELECTION_LIST, context.resources.getString(R.color.com_shift4_black))

        labelCustomization.setTextFontName(UiCustomization.LabelType.WHY_INFO, "shift4_3ds_nunitosans_semibold.ttf")
        labelCustomization.setTextFontSize(UiCustomization.LabelType.WHY_INFO, 14)
        labelCustomization.setTextColor(UiCustomization.LabelType.WHY_INFO, context.resources.getString(R.color.com_shift4_black))
        labelCustomization.setBackgroundColor(UiCustomization.LabelType.WHY_INFO, context.resources.getString(R.color.com_shift4_white))

        labelCustomization.setTextFontName(UiCustomization.LabelType.WHY_INFO_TEXT, "shift4_3ds_nunitosans_regular.ttf")
        labelCustomization.setTextFontSize(UiCustomization.LabelType.WHY_INFO_TEXT, 14)
        labelCustomization.setTextColor(UiCustomization.LabelType.WHY_INFO_TEXT, context.resources.getString(R.color.com_shift4_black))
        labelCustomization.setBackgroundColor(UiCustomization.LabelType.WHY_INFO_TEXT, context.resources.getString(R.color.com_shift4_white))

        labelCustomization.setTextFontName(UiCustomization.LabelType.EXPANDABLE_INFO, "shift4_3ds_nunitosans_semibold.ttf")
        labelCustomization.setTextFontSize(UiCustomization.LabelType.EXPANDABLE_INFO, 14)
        labelCustomization.setTextColor(UiCustomization.LabelType.EXPANDABLE_INFO, context.resources.getString(R.color.com_shift4_black))
        labelCustomization.setBackgroundColor(UiCustomization.LabelType.EXPANDABLE_INFO, context.resources.getString(R.color.com_shift4_white))

        labelCustomization.setTextFontName(UiCustomization.LabelType.EXPANDABLE_INFO_TEXT, "shift4_3ds_nunitosans_regular.ttf")
        labelCustomization.setTextFontSize(UiCustomization.LabelType.EXPANDABLE_INFO_TEXT, 14)
        labelCustomization.setTextColor(UiCustomization.LabelType.EXPANDABLE_INFO_TEXT, context.resources.getString(R.color.com_shift4_black))
        labelCustomization.setBackgroundColor(UiCustomization.LabelType.EXPANDABLE_INFO_TEXT, context.resources.getString(R.color.com_shift4_white))

        val textBoxCustomization = uiCustomization.textBoxCustomization
        textBoxCustomization.borderWidth = 1
        textBoxCustomization.textFontName = "securionpay_3ds_nunitosans_regular"
        textBoxCustomization.textFontSize = 14
        textBoxCustomization.cornerRadius = 10
        textBoxCustomization.borderColor = context.resources.getString(R.color.com_shift4_grayMedium)
        textBoxCustomization.textColor = context.resources.getString(R.color.com_shift4_black)

        uiCustomization.toolbarCustomization.backgroundColor =
            context.resources.getString(R.color.com_shift4_white)
        uiCustomization.toolbarCustomization.textColor =
            context.resources.getString(R.color.com_shift4_black)
        uiCustomization.toolbarCustomization.headerText = "3D SECURE"
        uiCustomization.toolbarCustomization.textFontSize = 14
        uiCustomization.toolbarCustomization.textFontName = "shift4_3ds_nunitosans_semibold.ttf"
        uiCustomization.background = context.resources.getString(R.color.com_shift4_white)
        
        return uiCustomization
    }

    private fun customizeButton(uiCustomization: UiCustomization, type: UiCustomization.ButtonType, backgroundColor: String, textColor: String) {
        val button = uiCustomization.getButtonCustomization(type)
        button.backgroundColor = backgroundColor
        button.height = 40
        button.cornerRadius = 4
        button.textColor = textColor
        button.textFontName = "shift4_3ds_nunitosans_semibold.ttf"
        button.textFontSize = 14
    }

    private fun customizeButtons(uiCustomization: UiCustomization, context: Context) {
        customizeButton(uiCustomization, UiCustomization.ButtonType.SUBMIT, context.resources.getString(
            R.color.com_shift4_primary), context.resources.getString(R.color.com_shift4_white))
        customizeButton(uiCustomization, UiCustomization.ButtonType.RESEND, context.resources.getString(
            R.color.com_shift4_white), context.resources.getString(R.color.com_shift4_black))
        customizeButton(uiCustomization, UiCustomization.ButtonType.NEXT, context.resources.getString(
            R.color.com_shift4_primary), context.resources.getString(R.color.com_shift4_white))
        customizeButton(uiCustomization, UiCustomization.ButtonType.CANCEL, context.resources.getString(
            R.color.com_shift4_white), context.resources.getString(R.color.com_shift4_black))
        customizeButton(uiCustomization, UiCustomization.ButtonType.CONTINUE, context.resources.getString(
            R.color.com_shift4_primary), context.resources.getString(R.color.com_shift4_white))
    }
}