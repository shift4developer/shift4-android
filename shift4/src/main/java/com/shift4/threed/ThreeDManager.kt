package com.shift4.threed

import android.app.Activity
import android.app.ProgressDialog
import com.shift4.data.model.error.APIError
import com.shift4.data.model.threeD.DirectoryServerCertificate
import com.shift4.threedsecure.pub.AuthenticationRequestParameters
import com.shift4.threedsecure.pub.ChallengeParameters
import com.shift4.threedsecure.pub.ChallengeStatusReceiver
import com.shift4.threedsecure.pub.CompletionEvent
import com.shift4.threedsecure.pub.ConfigParameters
import com.shift4.threedsecure.pub.DirectoryServerInfo
import com.shift4.threedsecure.pub.Transaction
import com.shift4.threedsecure.pub.ProtocolErrorEvent
import com.shift4.threedsecure.pub.RuntimeErrorEvent
import com.shift4.threedsecure.pub.ThreeDS2Service
import com.shift4.threedsecure.pub.Warning
import java.util.*

internal class ThreeDManager : ChallengeStatusReceiver {
    private var activity: Activity? = null
    private var threeDService = ThreeDS2Service()
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
        sdkProgressDialog = activity?.let { sdkTransaction?.getProgressView(it) }
        sdkProgressDialog?.show()
    }

    fun hideProgressDialog() {
        sdkProgressDialog?.hide()
        sdkProgressDialog = null
    }

    fun startChallenge(authResponse: String, completion: (Boolean, APIError?) -> Unit) {
        this.completion = completion
        val challengeParameters = ChallengeParameters(authResponse, "")
        sdkTransaction!!.doChallenge(activity!!, challengeParameters, this, 5)
    }

    fun closeTransaction() {
        sdkTransaction?.close()
        sdkProgressDialog = null
    }

    fun initialize(
        activity: Activity,
        cardBrand: String,
        certificate: DirectoryServerCertificate,
        signature: String,
        packageName: String,
        trustedAppStores: List<String>?
    ): Array<Warning> {
        this.activity = activity
        try {
            threeDService.cleanup(activity)
        } catch (_: Exception) {
        }

        val directoryServerInfoList = listOf(
            DirectoryServerInfo(
                cardBrand,
                certificate.certificate,
                certificate.caCertificates
            )
        )

        val clientConfigs: MutableList<String> = ArrayList()
        clientConfigs.add("MaskSensitive=true")
        val excludedDeviceParameters: MutableList<String> = ArrayList()
        excludedDeviceParameters.add("C007")
        excludedDeviceParameters.add("C009")
        excludedDeviceParameters.add("C010")
        excludedDeviceParameters.add("C011")
        excludedDeviceParameters.add("C012")
        excludedDeviceParameters.add("C014")

        excludedDeviceParameters.add("D001")
        excludedDeviceParameters.add("D002")
        excludedDeviceParameters.add("D003")
        excludedDeviceParameters.add("D005")
        excludedDeviceParameters.add("D006")
        excludedDeviceParameters.add("D008")
        excludedDeviceParameters.add("D021")
        excludedDeviceParameters.add("D022")
        excludedDeviceParameters.add("D023")
        excludedDeviceParameters.add("D024")
        excludedDeviceParameters.add("D025")
        excludedDeviceParameters.add("D026")
        excludedDeviceParameters.add("D027")
        excludedDeviceParameters.add("D028")

        // Telephony manager
        excludedDeviceParameters.add("A001")
        excludedDeviceParameters.add("A002")
        excludedDeviceParameters.add("A003")
        excludedDeviceParameters.add("A004")
        excludedDeviceParameters.add("A005")
        excludedDeviceParameters.add("A006")
        excludedDeviceParameters.add("A007")
        excludedDeviceParameters.add("A008")
        excludedDeviceParameters.add("A009")
        excludedDeviceParameters.add("A010")
        excludedDeviceParameters.add("A011")
        excludedDeviceParameters.add("A012")
        excludedDeviceParameters.add("A013")
        excludedDeviceParameters.add("A014")
        excludedDeviceParameters.add("A015")
        excludedDeviceParameters.add("A016")
        excludedDeviceParameters.add("A017")
        excludedDeviceParameters.add("A018")
        excludedDeviceParameters.add("A019")
        excludedDeviceParameters.add("A020")
        excludedDeviceParameters.add("A021")
        excludedDeviceParameters.add("A022")
        excludedDeviceParameters.add("A023")
        excludedDeviceParameters.add("A024")
        excludedDeviceParameters.add("A025")
        excludedDeviceParameters.add("A026")
        excludedDeviceParameters.add("A027")

        // Wifi Manager
        excludedDeviceParameters.add("A028")
        excludedDeviceParameters.add("A029")
        excludedDeviceParameters.add("A030")
        excludedDeviceParameters.add("A031")
        excludedDeviceParameters.add("A032")
        excludedDeviceParameters.add("A033")
        excludedDeviceParameters.add("A034")
        excludedDeviceParameters.add("A035")
        excludedDeviceParameters.add("A036")
        excludedDeviceParameters.add("A037")
        excludedDeviceParameters.add("A038")

        // Bluetoth manager
        excludedDeviceParameters.add("A039")
        excludedDeviceParameters.add("A040")
        excludedDeviceParameters.add("A041")

        // Build
        excludedDeviceParameters.add("A042")
        excludedDeviceParameters.add("A043")
        excludedDeviceParameters.add("A044")
        excludedDeviceParameters.add("A045")
        excludedDeviceParameters.add("A046")
        excludedDeviceParameters.add("A047")
        excludedDeviceParameters.add("A048")
        excludedDeviceParameters.add("A049")
        excludedDeviceParameters.add("A050")
        excludedDeviceParameters.add("A051")
        excludedDeviceParameters.add("A052")
        excludedDeviceParameters.add("A053")
        excludedDeviceParameters.add("A054")
        excludedDeviceParameters.add("A055")
        excludedDeviceParameters.add("A056")
        excludedDeviceParameters.add("A057")
        excludedDeviceParameters.add("A058")
        excludedDeviceParameters.add("A059")

        // Build. Version
        excludedDeviceParameters.add("A060")
        excludedDeviceParameters.add("A061")
        excludedDeviceParameters.add("A062")
        excludedDeviceParameters.add("A063")
        excludedDeviceParameters.add("A064")

        // Settings Secure
        excludedDeviceParameters.add("A065")
        excludedDeviceParameters.add("A066")
        excludedDeviceParameters.add("A067")
        excludedDeviceParameters.add("A068")
        excludedDeviceParameters.add("A069")
        excludedDeviceParameters.add("A070")
        excludedDeviceParameters.add("A071")
        excludedDeviceParameters.add("A072")
        excludedDeviceParameters.add("A073")
        excludedDeviceParameters.add("A074")
        excludedDeviceParameters.add("A075")
        excludedDeviceParameters.add("A076")
        excludedDeviceParameters.add("A077")
        excludedDeviceParameters.add("A078")
        excludedDeviceParameters.add("A079")

        // Global settings
        excludedDeviceParameters.add("A084")
        excludedDeviceParameters.add("A085")
        excludedDeviceParameters.add("A086")
        excludedDeviceParameters.add("A087")
        excludedDeviceParameters.add("A088")
        excludedDeviceParameters.add("A089")
        excludedDeviceParameters.add("A090")
        excludedDeviceParameters.add("A091")
        excludedDeviceParameters.add("A092")
        excludedDeviceParameters.add("A093")
        excludedDeviceParameters.add("A094")
        excludedDeviceParameters.add("A095")
        excludedDeviceParameters.add("A096")
        excludedDeviceParameters.add("A097")
        excludedDeviceParameters.add("A098")
        excludedDeviceParameters.add("A099")
        excludedDeviceParameters.add("A100")
        excludedDeviceParameters.add("A101")
        excludedDeviceParameters.add("A102")
        excludedDeviceParameters.add("A103")
        excludedDeviceParameters.add("A104")
        excludedDeviceParameters.add("A105")
        excludedDeviceParameters.add("A106")
        excludedDeviceParameters.add("A107")
        excludedDeviceParameters.add("A108")
        excludedDeviceParameters.add("A109")
        excludedDeviceParameters.add("A110")
        excludedDeviceParameters.add("A111")
        excludedDeviceParameters.add("A112")
        excludedDeviceParameters.add("A113")
        excludedDeviceParameters.add("A114")
        excludedDeviceParameters.add("A115")
        excludedDeviceParameters.add("A116")
        excludedDeviceParameters.add("A117")
        excludedDeviceParameters.add("A118")
        excludedDeviceParameters.add("A119")
        excludedDeviceParameters.add("A120")
        excludedDeviceParameters.add("A121")
        excludedDeviceParameters.add("A122")
        excludedDeviceParameters.add("A123")

        // System Settings
        // Package manager
        excludedDeviceParameters.add("A125")
        excludedDeviceParameters.add("A127")
        excludedDeviceParameters.add("A128")

        // Environment
        excludedDeviceParameters.add("A129")

        // Locale
        excludedDeviceParameters.add("A130")
        excludedDeviceParameters.add("A136")

        // DisplayMetrics
        excludedDeviceParameters.add("A146")
        excludedDeviceParameters.add("A147")
        excludedDeviceParameters.add("A148")
        excludedDeviceParameters.add("A149")

        val configParameters = ConfigParameters(
            signature,
            packageName,
            directoryServerInfoList,
            excludedDeviceParameters,
            trustedAppStores ?: listOf()
        )

        val locale: String? = null
        threeDService.initialize(
            activity,
            configParameters,
            locale,
            ThreeDUICustomizationFactory(activity.applicationContext).createUICustimization(),
        )
        return threeDService.getWarnings().filter { it.id != "SW04" }.toTypedArray()
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

    override fun protocolError(event: ProtocolErrorEvent) {
        completion(false, APIError.protocolThreeD(event))
    }

    override fun runtimeError(event: RuntimeErrorEvent) {
        completion(false, APIError.runtimeThreeD(event))
    }
}

