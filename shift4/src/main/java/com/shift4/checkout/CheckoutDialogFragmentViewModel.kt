package com.shift4.checkout

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shift4.R
import com.shift4.Shift4
import com.shift4.checkout.component.ButtonComponent
import com.shift4.data.api.Result
import com.shift4.data.model.result.Status
import com.shift4.data.model.CreditCard
import com.shift4.response.address.BillingRequest
import com.shift4.response.address.ShippingRequest
import com.shift4.data.model.error.APIError
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.model.pay.Donation
import com.shift4.data.model.subscription.Subscription
import com.shift4.request.token.TokenRequest
import com.shift4.data.repository.SDKRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal enum class Mode {
    INITIALIZING, LOADING, DONATION, NEW_CARD
}

internal class CheckoutDialogFragmentViewModel : ViewModel() {
    var email: String? = null
    var card: String? = null
    var expiration: String? = null
    var cvc: String? = null
    var name: String? = null
    var shippingRequest: ShippingRequest? = null
    var billingRequest: BillingRequest? = null
    var remember = false

    private val _emailValue = MutableLiveData<String?>()
    val emailValue: LiveData<String?> get() = _emailValue

    private val _cardValue = MutableLiveData<String?>()
    val cardValue: LiveData<String?> get() = _cardValue

    private val _expValue = MutableLiveData<String?>()
    val expValue: LiveData<String?> get() = _expValue

    private val _cvcValue = MutableLiveData<String?>()
    val cvcValue: LiveData<String?> get() = _cvcValue

    private val _nameValue = MutableLiveData<String?>()
    val nameValue: LiveData<String?> get() = _nameValue

    private val _billingRequestValue = MutableLiveData<BillingRequest?>()
    val billingRequestValue: LiveData<BillingRequest?> get() = _billingRequestValue

    private val _shippingRequestValue = MutableLiveData<ShippingRequest?>()
    val shippingRequestValue: LiveData<ShippingRequest?> get() = _shippingRequestValue

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
        updateButtonStatus()
    }
    fun onNameChange(name: String?) {
        this.name = name
        updateButtonStatus()
    }

    fun onCardChange(card: String?) {
        this.card = card
        updateButtonStatus()
    }

    fun onExpChange(exp: String?) {
        this.expiration = exp
        updateButtonStatus()
    }

    fun onCvcChange(cvc: String?) {
        this.cvc = cvc
        updateButtonStatus()
    }

    fun onAddressEntered(shippingRequest: ShippingRequest?, billingRequest: BillingRequest?) {
        this.shippingRequest = shippingRequest
        this.billingRequest = billingRequest
        updateButtonStatus()
    }

    fun onClickButton(activity: Activity) {
        pay(activity)
    }

    lateinit var checkoutManager: CheckoutManager
    lateinit var checkoutRequest: CheckoutRequest

    var currentMode: Mode = Mode.INITIALIZING

    var collectShippingAddress = false
    var collectBillingAddress = false
    var initialEmail: String? = null
    var subscription: Subscription? = null
    var selectedDonation: Donation? = null
    var processing = false

    private var initialized = false

    fun initialize(arguments: Bundle, context: Context) {
        if (initialized) {
            _emailValue.value = email
            _cardValue.value = card
            _expValue.value = expiration
            _cvcValue.value = cvc
            _shippingRequestValue.value = shippingRequest
            _billingRequestValue.value = billingRequest
            _rememberValue.value = remember
            return
        }
        initialized = true

        checkoutRequest =
            CheckoutRequest(arguments.getString("checkoutRequest", null).orEmpty())
        checkoutManager = CheckoutManager(
            SDKRepository(arguments.getString("publicKey", null).orEmpty()),
            arguments.getString("signature", null).orEmpty(),
            arguments.getString("packageName", null).orEmpty(),
            arguments.getStringArray("trustedAppStores")?.toList(),
            viewModelScope
        )
        collectShippingAddress = arguments.getBoolean("collectShippingAddress")
        collectBillingAddress =
            collectShippingAddress || arguments.getBoolean("collectBillingAddress")
        initialEmail = arguments.getString("initialEmail")

        email = initialEmail
        _emailValue.value = email

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
            }

            else -> {
                switchMode(Mode.NEW_CARD)
                updateButtonStatus()
                updateAmountOnButton()
            }
        }
    }

    private fun adaptViewsToCurrentMode() {
        _isAddressComponentVisible.value = false
        _isDonationComponentVisible.value = false
        _isProgressComponentVisible.value = false
        _isCardComponentVisible.value = false
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
                _isEmailComponentVisible.value = true
                _isButtonSeparatorVisible.value = true
                _isButtonComponentVisible.value = true
                _isButtonCloseVisible.value = true
                updateAmountOnButton()
                updateButtonStatus()
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
    }

    private fun updateButtonStatus() {
        val card = CreditCard(number = card.orEmpty())

        val correctEmail = !email.isNullOrBlank()
        val correctNumber = card.correct
        val correctExpiration = !expiration.isNullOrBlank()
        val correctCVC = !cvc.isNullOrBlank()
        val correctName = !name.isNullOrBlank()
        val correctShipping = !collectShippingAddress || this.shippingRequest != null
        val correctBilling = !collectBillingAddress || this.billingRequest != null

        _isButtonComponentEnabled.value =
            correctEmail && correctNumber && correctExpiration && correctCVC && correctShipping && correctBilling && correctName
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

    private fun switchMode(newMode: Mode) {
        if (currentMode == newMode) {
            return
        }
        currentMode = newMode
        adaptViewsToCurrentMode()
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

        setProcessingState(true)
        startButtonAnimation()

        val month = expiration?.split("/")?.first()
        val year = expiration?.split("/")?.last()

        _isKeyboardVisible.value = false

        _error.value = null
        _emailError.value = null
        _cardError.value = null
        billingRequest = billingRequest?.let { BillingRequest(name, it.address, it.vat) }
        shippingRequest = shippingRequest?.let { ShippingRequest(it.name ?: name, it.address ) }

        val tokenRequest = TokenRequest(number = card, expMonth = month, expYear = year, cvc = cvc)
        viewModelScope.launch(Dispatchers.IO) {
            checkoutManager.pay(
                tokenRequest,
                checkoutRequest,
                email!!,
                activity,
                customAmount = selectedDonation?.amount ?: subscription?.plan?.amount,
                customCurrency = selectedDonation?.currency ?: subscription?.plan?.currency,
                billingRequest = billingRequest ?: BillingRequest(name = name),
                shippingRequest = shippingRequest
            ) {
                viewModelScope.launch(Dispatchers.Main) {
                    processChargeResult(it)
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
                        email = initialEmail
                        _emailValue.value = email
                        _isCardComponentFocused.value = email != null
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