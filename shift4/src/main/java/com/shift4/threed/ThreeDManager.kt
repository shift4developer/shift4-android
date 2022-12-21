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
        deviceParameterBlacklist.add("C007")
        deviceParameterBlacklist.add("C009")
        deviceParameterBlacklist.add("C010")
        deviceParameterBlacklist.add("C011")
        deviceParameterBlacklist.add("C012")
        deviceParameterBlacklist.add("C014")

        deviceParameterBlacklist.add("D001")
        deviceParameterBlacklist.add("D002")
        deviceParameterBlacklist.add("D003")
        deviceParameterBlacklist.add("D005")
        deviceParameterBlacklist.add("D006")
        deviceParameterBlacklist.add("D008")
        deviceParameterBlacklist.add("D021")
        deviceParameterBlacklist.add("D022")
        deviceParameterBlacklist.add("D023")
        deviceParameterBlacklist.add("D024")
        deviceParameterBlacklist.add("D025")
        deviceParameterBlacklist.add("D026")
        deviceParameterBlacklist.add("D027")
        deviceParameterBlacklist.add("D028")

        // Telephony manager
        deviceParameterBlacklist.add("A001")
        deviceParameterBlacklist.add("A002")
        deviceParameterBlacklist.add("A003")
        deviceParameterBlacklist.add("A004")
        deviceParameterBlacklist.add("A005")
        deviceParameterBlacklist.add("A006")
        deviceParameterBlacklist.add("A007")
        deviceParameterBlacklist.add("A008")
        deviceParameterBlacklist.add("A009")
        deviceParameterBlacklist.add("A010")
        deviceParameterBlacklist.add("A011")
        deviceParameterBlacklist.add("A012")
        deviceParameterBlacklist.add("A013")
        deviceParameterBlacklist.add("A014")
        deviceParameterBlacklist.add("A015")
        deviceParameterBlacklist.add("A016")
        deviceParameterBlacklist.add("A017")
        deviceParameterBlacklist.add("A018")
        deviceParameterBlacklist.add("A019")
        deviceParameterBlacklist.add("A020")
        deviceParameterBlacklist.add("A021")
        deviceParameterBlacklist.add("A022")
        deviceParameterBlacklist.add("A023")
        deviceParameterBlacklist.add("A024")
        deviceParameterBlacklist.add("A025")
        deviceParameterBlacklist.add("A026")
        deviceParameterBlacklist.add("A027")

        // Wifi Manager
        deviceParameterBlacklist.add("A028")
        deviceParameterBlacklist.add("A029")
        deviceParameterBlacklist.add("A030")
        deviceParameterBlacklist.add("A031")
        deviceParameterBlacklist.add("A032")
        deviceParameterBlacklist.add("A033")
        deviceParameterBlacklist.add("A034")
        deviceParameterBlacklist.add("A035")
        deviceParameterBlacklist.add("A036")
        deviceParameterBlacklist.add("A037")
        deviceParameterBlacklist.add("A038")

        // Bluetoth manager
        deviceParameterBlacklist.add("A039")
        deviceParameterBlacklist.add("A040")
        deviceParameterBlacklist.add("A041")

        // Build
        deviceParameterBlacklist.add("A042")
        deviceParameterBlacklist.add("A043")
        deviceParameterBlacklist.add("A044")
        deviceParameterBlacklist.add("A045")
        deviceParameterBlacklist.add("A046")
        deviceParameterBlacklist.add("A047")
        deviceParameterBlacklist.add("A048")
        deviceParameterBlacklist.add("A049")
        deviceParameterBlacklist.add("A050")
        deviceParameterBlacklist.add("A051")
        deviceParameterBlacklist.add("A052")
        deviceParameterBlacklist.add("A053")
        deviceParameterBlacklist.add("A054")
        deviceParameterBlacklist.add("A055")
        deviceParameterBlacklist.add("A056")
        deviceParameterBlacklist.add("A057")
        deviceParameterBlacklist.add("A058")
        deviceParameterBlacklist.add("A059")

        // Build. Version
        deviceParameterBlacklist.add("A060")
        deviceParameterBlacklist.add("A061")
        deviceParameterBlacklist.add("A062")
        deviceParameterBlacklist.add("A063")
        deviceParameterBlacklist.add("A064")

        // Settings Secure
        deviceParameterBlacklist.add("A065")
        deviceParameterBlacklist.add("A066")
        deviceParameterBlacklist.add("A067")
        deviceParameterBlacklist.add("A068")
        deviceParameterBlacklist.add("A069")
        deviceParameterBlacklist.add("A070")
        deviceParameterBlacklist.add("A071")
        deviceParameterBlacklist.add("A072")
        deviceParameterBlacklist.add("A073")
        deviceParameterBlacklist.add("A074")
        deviceParameterBlacklist.add("A075")
        deviceParameterBlacklist.add("A076")
        deviceParameterBlacklist.add("A077")
        deviceParameterBlacklist.add("A078")
        deviceParameterBlacklist.add("A079")

        // Global settings
        deviceParameterBlacklist.add("A084")
        deviceParameterBlacklist.add("A085")
        deviceParameterBlacklist.add("A086")
        deviceParameterBlacklist.add("A087")
        deviceParameterBlacklist.add("A088")
        deviceParameterBlacklist.add("A089")
        deviceParameterBlacklist.add("A090")
        deviceParameterBlacklist.add("A091")
        deviceParameterBlacklist.add("A092")
        deviceParameterBlacklist.add("A093")
        deviceParameterBlacklist.add("A094")
        deviceParameterBlacklist.add("A095")
        deviceParameterBlacklist.add("A096")
        deviceParameterBlacklist.add("A097")
        deviceParameterBlacklist.add("A098")
        deviceParameterBlacklist.add("A099")
        deviceParameterBlacklist.add("A100")
        deviceParameterBlacklist.add("A101")
        deviceParameterBlacklist.add("A102")
        deviceParameterBlacklist.add("A103")
        deviceParameterBlacklist.add("A104")
        deviceParameterBlacklist.add("A105")
        deviceParameterBlacklist.add("A106")
        deviceParameterBlacklist.add("A107")
        deviceParameterBlacklist.add("A108")
        deviceParameterBlacklist.add("A109")
        deviceParameterBlacklist.add("A110")
        deviceParameterBlacklist.add("A111")
        deviceParameterBlacklist.add("A112")
        deviceParameterBlacklist.add("A113")
        deviceParameterBlacklist.add("A114")
        deviceParameterBlacklist.add("A115")
        deviceParameterBlacklist.add("A116")
        deviceParameterBlacklist.add("A117")
        deviceParameterBlacklist.add("A118")
        deviceParameterBlacklist.add("A119")
        deviceParameterBlacklist.add("A120")
        deviceParameterBlacklist.add("A121")
        deviceParameterBlacklist.add("A122")
        deviceParameterBlacklist.add("A123")

        // System Settings
        // Package manager
        deviceParameterBlacklist.add("A124")
        deviceParameterBlacklist.add("A125")
        deviceParameterBlacklist.add("A126")
        deviceParameterBlacklist.add("A127")
        deviceParameterBlacklist.add("A128")

        // Environment
        deviceParameterBlacklist.add("A129")

        // Locale
        deviceParameterBlacklist.add("A130")
        deviceParameterBlacklist.add("A136")

        // DisplayMetrics
        deviceParameterBlacklist.add("A146")
        deviceParameterBlacklist.add("A147")
        deviceParameterBlacklist.add("A148")
        deviceParameterBlacklist.add("A149")

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
        completion(false, APIError.threeDTimeout)
    }

    override fun protocolError(event: ProtocolErrorEvent?) {
        completion(false, APIError.unknown)
    }

    override fun runtimeError(event: RuntimeErrorEvent?) {
        completion(false, APIError.unknown)
    }
}

