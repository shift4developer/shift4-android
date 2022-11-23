package com.shift4.threed

import android.app.Activity
import android.app.ProgressDialog
import com.shift4.BuildConfig
import com.nsoftware.ipworks3ds.sdk.*
import com.nsoftware.ipworks3ds.sdk.event.CompletionEvent
import com.nsoftware.ipworks3ds.sdk.event.ProtocolErrorEvent
import com.nsoftware.ipworks3ds.sdk.event.RuntimeErrorEvent
import com.shift4.data.model.error.APIError
import com.shift4.data.model.threeD.DirectoryServerCertificate
import java.util.*

internal class ThreeDManager : ChallengeStatusReceiver {
    private var activity: Activity? = null
    private var threeDService = ThreeDS2Service.INSTANCE
    private var sdkTransaction: Transaction? = null
    private var sdkProgressDialog: ProgressDialog? = null
    private var completion: (Boolean, APIError?) -> Unit? = { _: Boolean, _: APIError? -> }

    fun createTransaction(version: String, cardBrand: String) {
        sdkTransaction = threeDService.createTransaction(cardBrand, version)
    }

    fun authenticationRequestParameters(): AuthenticationRequestParameters? {
        return sdkTransaction?.authenticationRequestParameters
    }

    fun showProgressDialog() {
        sdkProgressDialog = sdkTransaction?.getProgressView(activity)
        sdkProgressDialog?.show()
    }

    fun hideProgressDialog() {
        sdkProgressDialog?.hide()
        sdkProgressDialog = null
    }

    fun startChallenge(authResponse: String?, completion: (Boolean, APIError?) -> Unit) {
        this.completion = completion
        val challengeParameters = ChallengeParameters()
        challengeParameters.threeDSServerAuthResponse = authResponse
        sdkTransaction!!.doChallenge(activity, challengeParameters, this, 15)
    }

    fun closeTransaction() {
        sdkTransaction?.close()
        sdkProgressDialog = null
    }

    fun initialize(
        activity: Activity,
        cardBrand: String,
        certificate: DirectoryServerCertificate,
        sdkLicense: String,
        signature: String,
        trustedAppStores: List<String>?
    ): Array<Warning> {
        this.activity = activity
        try {
            threeDService.cleanup(activity)
        } catch (_: Exception) {
        }
        val directoryServerInfoList: List<ConfigParameters.DirectoryServerInfo> = listOf(
            ConfigParameters.DirectoryServerInfo(
                cardBrand,
                certificate.certificate,
                certificate.caCertificates.toTypedArray()
            )
        )

        val clientConfigs: MutableList<String> = ArrayList()
        clientConfigs.add("MaskSensitive=true")
        val eventListener: ClientEventListener?
        val securityEventListener: SecurityEventListener?
        if (BuildConfig.DEBUG) {
            clientConfigs.add("logLevel=3")
            eventListener = DebugEventListener()
            securityEventListener = DebugSecurityEventListener()
        } else {
            clientConfigs.add("logLevel=0")
            eventListener = null
            securityEventListener = null
        }
        val deviceParameterBlacklist: MutableList<String> = ArrayList()
        deviceParameterBlacklist.add("A009")
        deviceParameterBlacklist.add("A010")
        val configParametersBuilder = ConfigParameters.Builder(directoryServerInfoList, sdkLicense)
            .clientConfig(clientConfigs)
            .deviceParameterBlacklist(deviceParameterBlacklist)
            .appSignature(signature)
        trustedAppStores?.let { configParametersBuilder.trustedAppStores(it) }
        val configParameters = configParametersBuilder.build()

        configParameters.addParam(null, "ShowWhiteBoxInProcessingScreen", "true")
        configParameters.addParam(null, "ProgressBarColor", "#0E5BF3")

        val locale: String? = null
        threeDService.initialize(
            activity,
            configParameters,
            locale,
            ThreeDUICustomizationFactory(activity.applicationContext).createUICustimization(),
            eventListener,
            securityEventListener
        )
        return threeDService.warnings.filter { it.id != "SW04" }.toTypedArray()
    }

    override fun completed(completionEvent: CompletionEvent) {
        completion(true, null)
        closeTransaction()
    }

    override fun cancelled() {
        completion(false, null)
    }

    override fun timedout() {
        completion(false, APIError.unknown)
    }

    override fun protocolError(event: ProtocolErrorEvent?) {
        completion(false, APIError.unknown)
    }

    override fun runtimeError(event: RuntimeErrorEvent?) {
        completion(false, APIError.unknown)
    }
}

