package com.shift4.checkout

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shift4.R
import com.shift4.Shift4
import com.shift4.checkout.component.ButtonComponent
import com.shift4.data.api.Result
import com.shift4.data.api.Status
import com.shift4.data.model.CreditCard
import com.shift4.data.model.address.Billing
import com.shift4.data.model.address.Shipping
import com.shift4.data.model.error.APIError
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.model.pay.Donation
import com.shift4.data.model.sms.SMS
import com.shift4.data.model.subscription.Subscription
import com.shift4.data.model.token.TokenRequest
import com.shift4.data.repository.SDKRepository
import com.shift4.utils.EmailStorage
import com.shift4.utils.EmailValidator
import com.shift4.utils.empty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask

internal enum class Mode {
    INITIALIZING, LOADING, DONATION, NEW_CARD, SMS
}

internal class CheckoutDialogFragmentViewModel : ViewModel() {
    var email: String? = null
    var card: String? = null
    var expiration: String? = null
    var cvc: String? = null
    var shipping: Shipping? = null
    var billing: Billing? = null
    var smsValue: String? = null
    var remember = false

    private val _emailValue = MutableLiveData<String?>()
    val emailValue: LiveData<String?> get() = _emailValue

    private val _cardValue = MutableLiveData<String?>()
    val cardValue: LiveData<String?> get() = _cardValue

    private val _expValue = MutableLiveData<String?>()
    val expValue: LiveData<String?> get() = _expValue

    private val _cvcValue = MutableLiveData<String?>()
    val cvcValue: LiveData<String?> get() = _cvcValue

    private val _billingValue = MutableLiveData<Billing?>()
    val billingValue: LiveData<Billing?> get() = _billingValue

    private val _shippingValue = MutableLiveData<Shipping?>()
    val shippingValue: LiveData<Shipping?> get() = _shippingValue

    private val _rememberValue = MutableLiveData<Boolean>()
    val rememberValue: LiveData<Boolean> get() = _rememberValue

    private val _isAddressComponentVisible = MutableLiveData<Boolean>(true)
    val isAddressComponentVisible: LiveData<Boolean> get() = _isAddressComponentVisible

    private val _isAddressComponentEnabled = MutableLiveData<Boolean>()
    val isAddressComponentEnabled: LiveData<Boolean> get() = _isAddressComponentEnabled

    private val _isDonationComponentVisible = MutableLiveData<Boolean>()
    val isDonationComponentVisible: LiveData<Boolean> get() = _isDonationComponentVisible

    private val _isProgressComponentVisible = MutableLiveData<Boolean>()
    val isProgressComponentVisible: LiveData<Boolean> get() = _isProgressComponentVisible

    private val _isCardComponentVisible = MutableLiveData<Boolean>()
    val isCardComponentVisible: LiveData<Boolean> get() = _isCardComponentVisible

    private val _isCardComponentEnabled = MutableLiveData<Boolean>()
    val isCardComponentEnabled: LiveData<Boolean> get() = _isCardComponentEnabled

    private val _cleanCardComponent = MutableLiveData<Boolean>()
    val cleanCardComponent: LiveData<Boolean> get() = _cleanCardComponent

    private val _isCardComponentFocused = MutableLiveData<Boolean>()
    val isCardComponentFocused: LiveData<Boolean> get() = _isCardComponentFocused

    private val _creditCardValue = MutableLiveData<Pair<CreditCard, Boolean>>()
    val creditCardValue: LiveData<Pair<CreditCard, Boolean>> get() = _creditCardValue

    private val _isSmsComponentVisible = MutableLiveData<Boolean>()
    val isSmsComponentVisible: LiveData<Boolean> get() = _isSmsComponentVisible

    private val _isSmsComponentFocused = MutableLiveData<Boolean>()
    val isSmsComponentFocused: LiveData<Boolean> get() = _isSmsComponentFocused

    private val _isSmsComponentError = MutableLiveData<Boolean>()
    val isSmsComponentError: LiveData<Boolean> get() = _isSmsComponentError

    private val _isRememberSwitchComponentEnabled = MutableLiveData<Boolean>()
    val isRememberSwitchComponentEnabled: LiveData<Boolean> get() = _isRememberSwitchComponentEnabled

    private val _isSwitchVisible = MutableLiveData<Boolean>()
    val isSwitchVisible: LiveData<Boolean> get() = _isSwitchVisible

    private val _isEmailComponentVisible = MutableLiveData<Boolean>()
    val isEmailComponentVisible: LiveData<Boolean> get() = _isEmailComponentVisible

    private val _isEmailComponentEnabled = MutableLiveData<Boolean>()
    val isEmailComponentEnabled: LiveData<Boolean> get() = _isEmailComponentEnabled

    private val _isAdditionalButtonInfoVisible = MutableLiveData<Boolean>()
    val isAdditionalButtonInfoVisible: LiveData<Boolean> get() = _isAdditionalButtonInfoVisible

    private val _isButtonSeparatorVisible = MutableLiveData<Boolean>()
    val isButtonSeparatorVisible: LiveData<Boolean> get() = _isButtonSeparatorVisible

    private val _isButtonComponentVisible = MutableLiveData<Boolean>()
    val isButtonComponentVisible: LiveData<Boolean> get() = _isButtonComponentVisible

    private val _buttonComponentText = MutableLiveData<Pair<Int, String?>?>()
    val buttonComponentText: LiveData<Pair<Int, String?>?> get() = _buttonComponentText

    private val _isButtonComponentEnabled = MutableLiveData<Boolean>()
    val isButtonComponentEnabled: LiveData<Boolean> get() = _isButtonComponentEnabled

    private val _buttonComponentState = MutableLiveData<ButtonComponent.State>()
    val buttonComponentState: LiveData<ButtonComponent.State> get() = _buttonComponentState

    private val _isButtonAnimating = MutableLiveData<Boolean>()
    val isButtonAnimating: LiveData<Boolean> get() = _isButtonAnimating

    private val _isButtonCloseVisible = MutableLiveData<Boolean>()
    val isButtonCloseVisible: LiveData<Boolean> get() = _isButtonCloseVisible

    private val _donationsAdapter = MutableLiveData<DonationsAdapter>()
    val donationsAdapter: LiveData<DonationsAdapter> get() = _donationsAdapter

    private val _error = MutableLiveData<APIError?>(null)
    val error: LiveData<APIError?> get() = _error

    private val _emailError = MutableLiveData<APIError?>(null)
    val emailError: LiveData<APIError?> get() = _emailError

    private val _cardError = MutableLiveData<APIError?>(null)
    val cardError: LiveData<APIError?> get() = _cardError

    private val _isKeyboardVisible = MutableLiveData<Boolean>()
    val isKeyboardVisible: LiveData<Boolean> get() = _isKeyboardVisible

    private val _isCancelable = MutableLiveData<Boolean>()
    val isCancelable: LiveData<Boolean> get() = _isCancelable

    private val _callback = MutableLiveData<Result<ChargeResult>?>()
    val callback: LiveData<Result<ChargeResult>?> get() = _callback

    fun onEmailChange(email: String?) {
        this.email = email
        clearTextIfSavedEmail()
        if (EmailValidator().isValidEmail(email)) {
            lookupTimer?.cancel()
            lookupTimer = Timer()
            lookupTimer?.schedule(object : TimerTask() {
                override fun run() {
                    lookup(silent = true)
                }
            }, 300)
        }
        updateButtonStatus()
    }

    fun onCardChange(card: String?) {
        this.card = card
        clearTextIfSavedEmail()
        updateButtonStatus()
    }

    fun onExpChange(exp: String?) {
        this.expiration = exp
        clearTextIfSavedEmail()
        updateButtonStatus()
    }

    fun onCvcChange(cvc: String?) {
        this.cvc = cvc
        updateButtonStatus()
    }

    fun onSmsEntered(sms: String?) {
        this.smsValue = sms
        verifySMS()
    }

    fun onAddressEntered(shipping: Shipping?, billing: Billing?) {
        this.shipping = shipping
        this.billing = billing
        updateButtonStatus()
    }

    fun onClickButton(activity: Activity) {
        pay(activity)
    }

    fun onRememberSwitchChange(remember: Boolean) {
        this.remember = remember
    }

    lateinit var checkoutManager: CheckoutManager
    lateinit var checkoutRequest: CheckoutRequest

    var currentMode: Mode = Mode.INITIALIZING

    var collectShippingAddress = false
    var collectBillingAddress = false
    var initialEmail: String? = null
    var subscription: Subscription? = null
    var selectedDonation: Donation? = null
    var savedEmail: String? = null
    var sms: SMS? = null
    var lookupTimer: Timer? = null
    var processing = false
    var verifiedCard = false

    private var initialized = false

    fun initialize(arguments: Bundle, context: Context) {
        if (initialized) {
            _emailValue.value = email
            _cardValue.value = card
            _expValue.value = expiration
            _cvcValue.value = cvc
            _shippingValue.value = shipping
            _billingValue.value = billing
            _rememberValue.value = remember
            return
        }
        initialized = true

        checkoutRequest =
            CheckoutRequest(arguments.getString("checkoutRequest", null) ?: String.empty)
        checkoutManager = CheckoutManager(
            SDKRepository(
                Shift4(
                    context,
                    arguments.getString("publicKey", null) ?: String.empty,
                    arguments.getString("signature", null) ?: String.empty,
                    arguments.getStringArray("trustedAppStores")?.toList()
                )
            ),
            EmailStorage(context),
            arguments.getString("signature", null) ?: String.empty,
            arguments.getStringArray("trustedAppStores")?.toList(),
            viewModelScope
        )
        collectShippingAddress = arguments.getBoolean("collectShippingAddress")
        collectBillingAddress =
            collectShippingAddress || arguments.getBoolean("collectBillingAddress")
        initialEmail = arguments.getString("initialEmail")

        if (currentMode == Mode.INITIALIZING) {
            setupInitialState()
            adaptViewsToCurrentMode()
        } else {
            adaptViewsToCurrentMode()
        }
    }

    private fun setupInitialState() {
        when {
            checkoutRequest.donations != null -> {
                val donationsAdapter = DonationsAdapter(checkoutRequest.donations!!.toTypedArray())
                donationsAdapter.onItemClick = {
                    selectedDonation = it
                }
                _donationsAdapter.value = donationsAdapter
                switchMode(Mode.DONATION)
            }

            checkoutRequest.subscriptionPlanId != null -> {
                switchMode(Mode.LOADING)
                _isKeyboardVisible.value = false
                getCheckoutDetails()
                remember = checkoutRequest.rememberMe
                _rememberValue.value = checkoutRequest.rememberMe
            }

            checkoutManager.emailStorage.lastEmail != null || initialEmail != null -> {
                _isKeyboardVisible.value = false
                switchMode(Mode.LOADING)

                updateButtonStatus()
                _rememberValue.value = checkoutRequest.rememberMe
                updateAmountOnButton()
                email = initialEmail ?: checkoutManager.emailStorage.lastEmail
                lookup(silent = true)
                _emailValue.value = email
            }

            else -> {
                switchMode(Mode.NEW_CARD)
                updateButtonStatus()
                remember = checkoutRequest.rememberMe
                _rememberValue.value = checkoutRequest.rememberMe
                updateAmountOnButton()
            }
        }
    }

    private fun adaptViewsToCurrentMode() {
        _isAddressComponentVisible.value = false
        _isDonationComponentVisible.value = false
        _isProgressComponentVisible.value = false
        _isCardComponentVisible.value = false
        _isSmsComponentVisible.value = false
        _isSwitchVisible.value = false
        _isEmailComponentVisible.value = false
        _isAdditionalButtonInfoVisible.value = false
        _isButtonSeparatorVisible.value = false
        _isButtonComponentVisible.value = false
        _isButtonCloseVisible.value = false

        when (currentMode) {
            Mode.LOADING -> {
                _isProgressComponentVisible.value = true
            }

            Mode.NEW_CARD -> {
                if (collectShippingAddress || collectBillingAddress) {
                    _isAddressComponentVisible.value = true
                }
                _isCardComponentVisible.value = true
                _isSwitchVisible.value = true
                _isEmailComponentVisible.value = true
                _isButtonSeparatorVisible.value = true
                _isButtonComponentVisible.value = true
                _isButtonCloseVisible.value = true
                updateAmountOnButton()
                updateButtonStatus()
            }

            Mode.SMS -> {
                _isSmsComponentVisible.value = true
                _isButtonCloseVisible.value = true
                _isSmsComponentFocused.value = true
                _isAdditionalButtonInfoVisible.value = false
                _isButtonComponentVisible.value = true
                _buttonComponentText.value = Pair(R.string.com_shift4_enter_payment_data, null)
                _isButtonComponentEnabled.value = true
            }

            Mode.DONATION -> {
                _isDonationComponentVisible.value = true
                _isButtonSeparatorVisible.value = true
                _isButtonCloseVisible.value = true
                _isButtonComponentVisible.value = true
                _buttonComponentText.value = Pair(R.string.com_shift4_confirm, null)
                _isButtonComponentEnabled.value = true
            }

            else -> {
            }
        }

        _isSwitchVisible.value = currentMode == Mode.NEW_CARD
    }

    private fun updateButtonStatus() {
        val email = this.email ?: String.empty
        val number = this.card ?: String.empty
        val expiration = this.expiration ?: String.empty
        val cvc = this.cvc ?: String.empty

        val card = CreditCard(number = number)

        val correctEmail = email.isNotEmpty()
        val correctNumber = card.correct
        val correctExpiration = expiration.isNotEmpty()
        val correctCVC = cvc.isNotEmpty()
        val correctShipping = !collectShippingAddress || this.shipping != null
        val correctBilling = !collectBillingAddress || this.billing != null

        _isButtonComponentEnabled.value =
            correctEmail && correctNumber && correctExpiration && correctCVC && correctShipping && correctBilling
    }

    private fun updateAmountOnButton() {
        when {
            subscription != null -> _buttonComponentText.value =
                Pair(R.string.com_shift4_pay, subscription?.readable())

            selectedDonation != null -> _buttonComponentText.value =
                Pair(R.string.com_shift4_pay, selectedDonation?.readable)

            else -> _buttonComponentText.value =
                Pair(R.string.com_shift4_pay, checkoutRequest.readable)
        }
    }

    private fun clearTextIfSavedEmail() {
        if (savedEmail == null) {
            return
        }
        savedEmail = null

        _cleanCardComponent.value = true

        sms = null
        verifiedCard = false

        _isSwitchVisible.value = currentMode == Mode.NEW_CARD
        _error.value = null
        _emailError.value = null
        _cardError.value = null
    }

    private fun switchMode(newMode: Mode) {
        if (currentMode == newMode) {
            return
        }
        currentMode = newMode
        adaptViewsToCurrentMode()
    }

    private fun verifySMS() {
        if (processing) {
            return
        }
        setProcessingState(true)
        _error.value = null
        viewModelScope.launch(Dispatchers.IO) {
            val verifyResult = checkoutManager.verifySMS(smsValue ?: String.empty, sms!!)
            withContext(Dispatchers.Main) {
                setProcessingState(false)
                when (verifyResult.status) {
                    Status.SUCCESS -> verifyResult.data?.also {
                        card = CreditCard(it.card).readable
                        expiration = CreditCard(it.card).expPlaceholder
                        cvc = CreditCard(it.card).cvcPlaceholder
                        _creditCardValue.value = Pair(CreditCard(card = it.card), verifiedCard)
                        remember = true
                        _rememberValue.value = true
                        fillCardForm()
                        switchMode(Mode.NEW_CARD)
                    }

                    Status.ERROR -> verifyResult.error?.also { error ->
                        _isSmsComponentError.value = true
                        if (error.code != APIError.Code.InvalidVerificationCode) {
                            _error.value = APIError.unknown
                        }
                    }
                }
            }
        }
    }

    private fun pay(activity: Activity) {
        _isKeyboardVisible.value = false

        if (currentMode == Mode.DONATION) {
            switchMode(Mode.NEW_CARD)
            return
        }

        if (processing) {
            return
        }
        if (currentMode == Mode.SMS) {
            _cleanCardComponent.value = true
            savedEmail = null
            sms = null
            switchMode(Mode.NEW_CARD)
            return
        }
        setProcessingState(true)
        startButtonAnimation()

        val month = expiration?.split("/")?.first()
        val year = expiration?.split("/")?.last()

        _isKeyboardVisible.value = false

        _error.value = null
        _emailError.value = null
        _cardError.value = null

        if (savedEmail != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val token = checkoutManager.savedToken(savedEmail!!)
                viewModelScope.launch(Dispatchers.Main) {
                    when (token.status) {
                        Status.SUCCESS -> checkoutManager.pay(
                            token.data!!,
                            checkoutRequest,
                            email ?: String.empty,
                            remember = remember,
                            activity,
                            sms = sms,
                            cvc = cvc,
                            customAmount = selectedDonation?.amount ?: subscription?.plan?.amount,
                            customCurrency = selectedDonation?.currency
                                ?: subscription?.plan?.currency,
                            shipping = shipping,
                            billing = billing
                        ) {
                            viewModelScope.launch(Dispatchers.Main) {
                                checkoutManager.emailStorage.lastEmail =
                                    if (it.data != null) savedEmail else null

                                if (it.error != null) {
                                    savedEmail = null
                                    _cleanCardComponent.value = true
                                    _isSwitchVisible.value = currentMode == Mode.NEW_CARD
                                }

                                processChargeResult(it)
                            }
                        }

                        Status.ERROR -> {
                            setProcessingState(false)
                            revertButtonAnimation()
                            token.error?.let { _error.value = it }
                        }
                    }
                }
            }
            return
        }

        val tokenRequest = TokenRequest(
            card ?: String.empty, month ?: String.empty, year ?: String.empty, cvc ?: String.empty
        )
        viewModelScope.launch(Dispatchers.IO) {
            checkoutManager.pay(
                tokenRequest,
                checkoutRequest,
                email!!,
                remember = remember,
                activity,
                customAmount = selectedDonation?.amount ?: subscription?.plan?.amount,
                customCurrency = selectedDonation?.currency ?: subscription?.plan?.currency,
                billing = billing,
                shipping = shipping
            ) {
                viewModelScope.launch(Dispatchers.Main) {
                    processChargeResult(it)
                }
            }
        }
    }

    private fun lookup(silent: Boolean = false) {
        if (email.isNullOrEmpty()) {
            return
        }
        if (!silent) {
            setProcessingState(true)
        }
        viewModelScope.launch(Dispatchers.IO) {
            val lookup = checkoutManager.lookup(email ?: String.empty)
            viewModelScope.launch(Dispatchers.Main) {
                when (lookup.status) {
                    Status.SUCCESS -> lookup.data?.also { data ->
                        savedEmail = email

                        if (data.phone != null) {
                            if (data.phone.verified) {
                                verifiedCard = true
                            }
                            val sendSMSResult = checkoutManager.sendSMS(email ?: String.empty)
                            viewModelScope.launch(Dispatchers.Main) {
                                setProcessingState(false)
                                when (sendSMSResult.status) {
                                    Status.SUCCESS -> {
                                        sms = sendSMSResult.data
                                        switchMode(Mode.SMS)
                                    }

                                    Status.ERROR -> lookup.error?.also {
                                        _error.value = it
                                        updateAmountOnButton()
                                    }
                                }
                            }
                        } else {
                            card = CreditCard(data.card).readable
                            expiration = CreditCard(data.card).expPlaceholder
                            _creditCardValue.value =
                                Pair(CreditCard(card = data.card), verifiedCard)
                            remember = true
                            _rememberValue.value = true
                            fillCardForm()
                            setProcessingState(false)
                            switchMode(Mode.NEW_CARD)
                            _isCardComponentFocused.value = true
                        }
                    }

                    Status.ERROR -> lookup.error?.also {
                        if (!silent) {
                            _error.value = it
                            updateAmountOnButton()
                            setProcessingState(false)
                        }
                        if (currentMode == Mode.LOADING) {
                            switchMode(Mode.NEW_CARD)
                        }
                    }
                }
            }
        }
    }

    private fun getCheckoutDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            val details = checkoutManager.checkoutRequestDetails(checkoutRequest)
            viewModelScope.launch(Dispatchers.Main) {
                when (details.status) {
                    Status.SUCCESS -> {
                        subscription = details.data!!.subscription
                        switchMode(Mode.NEW_CARD)
                        email = initialEmail ?: checkoutManager.emailStorage.lastEmail
                        _emailValue.value = email
                        _isCardComponentFocused.value = email != null
                        lookup(true)
                    }

                    Status.ERROR -> {
                        _callback.value = Result.error(APIError.unknown)
                    }
                }
            }
        }
    }

    private fun fillCardForm() {
        updateButtonStatus()
        _isSwitchVisible.value = currentMode == Mode.NEW_CARD
    }

    private fun processChargeResult(charge: Result<ChargeResult>) {
        when (charge.status) {
            Status.SUCCESS -> charge.data?.also {
                _buttonComponentState.value = ButtonComponent.State.SUCCESS
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        _callback.value = charge
                    },
                    500,
                )
            } ?: run {
                revertButtonAnimation()
                setProcessingState(false)
            }

            Status.ERROR -> charge.error?.also { error ->
                setProcessingState(false)
                _isKeyboardVisible.value = false
                revertButtonAnimation()
                if (error.type == APIError.Type.CardError && error.code != null) {
                    _cardError.value = error
                } else if (error.code == APIError.Code.InvalidEmail) {
                    _emailError.value = error
                } else if (error.code == APIError.Code.VerificationCodeRequired) {
                    lookup()
                } else if (error.type == APIError.Type.ThreeDSecure) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        _callback.value = Result.error(error)
                    }, 150)
                } else {
                    _error.value = error
                }
            }
        }
    }

    private fun setProcessingState(processing: Boolean) {
        _isCancelable.value = !processing
        this.processing = processing
        _isEmailComponentEnabled.value = !processing
        _isCardComponentEnabled.value = !processing
        _isRememberSwitchComponentEnabled.value = !processing
        _isAddressComponentEnabled.value = !processing
    }

    private fun startButtonAnimation() {
        _isButtonAnimating.value = true
    }

    private fun revertButtonAnimation() {
        _isButtonAnimating.value = false
        updateAmountOnButton()
        updateButtonStatus()
    }
}