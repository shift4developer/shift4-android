package com.shift4.utils

import android.os.Build
import com.shift4.BuildConfig


internal class UserAgentGenerator() {
    internal fun userAgent(): String {
        val packageName = BuildConfig.LIBRARY_PACKAGE_NAME
        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        val buildType = BuildConfig.BUILD_TYPE
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        val manufacturer = Build.MANUFACTURER
        val model = Build.PRODUCT

        return "$packageName $versionName($versionCode) [$buildType] OS: $sdkVersion($release) DEVICE: $manufacturer $model"
    }
}