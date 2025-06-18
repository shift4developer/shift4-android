package com.shift4.data.model.threeD



internal data class ThreeDCheckRequest(
    val amount: Int,
    val currency: String,
    val card: String? = null,
    val paymentMethod: String? = null,
    val paymentUserAgent: String,
    val platform: String = "android",
    val browser: Browser = Browser(),
    val options: Options
)

internal data class Browser(
    val screenColorDepth: Int = 24,
    val screenWidth: Int = 430,
    val screenHeight: Int = 932,
    val timeZone: Int = -120
)

internal data class Options(
    val clientAuthRequest: String
)