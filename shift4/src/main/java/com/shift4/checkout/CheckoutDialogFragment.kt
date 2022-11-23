package com.shift4.checkout

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shift4.R
import com.shift4.Shift4
import com.shift4.checkout.component.ButtonComponent
import com.shift4.data.api.Result
import com.shift4.data.api.Status
import com.shift4.data.model.CreditCard
import com.shift4.data.model.error.APIError
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.pay.CheckoutRequest
import com.shift4.data.model.pay.Donation
import com.shift4.data.model.sms.SMS
import com.shift4.data.model.subscription.Subscription
import com.shift4.data.model.token.TokenRequest
import com.shift4.data.repository.SDKRepository
import com.shift4.databinding.ComShift4CheckoutDialogBinding
import com.shift4.utils.EmailStorage
import com.shift4.utils.EmailValidator
import com.shift4.utils.empty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


internal class CheckoutDialogFragment : BottomSheetDialogFragment() {
    private lateinit var checkoutManager: CheckoutManager
    private lateinit var checkoutRequest: CheckoutRequest

    private var _binding: ComShift4CheckoutDialogBinding? = null
    private val binding get() = _binding!!

    private enum class Mode {
        INITIALIZING,
        LOADING,
        DONATION,
        NEW_CARD,
        SMS
    }

    private var collectShippingAddress = false
    private var collectBillingAddress = false
    private var initialEmail: String? = null

    private var savedEmail: String? = null
    private var sms: SMS? = null
    private var subscription: Subscription? = null
    private var lookupTimer: Timer? = null

    private var currentMode: Mode = Mode.INITIALIZING
    private var processing = false
    private var verifiedCard = false
    private var selectedDonation: Donation? = null
    private val modalBottomSheetBehavior: BottomSheetBehavior<FrameLayout> get() = (this.dialog as BottomSheetDialog).behavior

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ComShift4CheckoutDialogBinding.inflate(inflater, container, false)

        checkoutRequest =
            CheckoutRequest(arguments?.getString("checkoutRequest", null) ?: String.empty)
        checkoutManager = CheckoutManager(
            SDKRepository(
                Shift4(
                    requireActivity(),
                    arguments?.getString("publicKey", null) ?: String.empty,
                    arguments?.getString("signature", null) ?: String.empty,
                    arguments?.getStringArray("trustedAppStores")?.toList()
                )
            ),
            EmailStorage(requireActivity()),
            arguments?.getString("signature", null) ?: String.empty,
            arguments?.getStringArray("trustedAppStores")?.toList()
        )
        collectShippingAddress = arguments?.getBoolean("collectShippingAddress") ?: false
        collectBillingAddress =
            collectShippingAddress || arguments?.getBoolean("collectBillingAddress") ?: false
        initialEmail = arguments?.getString("initialEmail")

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            DialogFragment.STYLE_NORMAL,
            R.style.SecurionPaySDKBottomSheetDialogThemeNoFloating
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modalBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        modalBottomSheetBehavior.isDraggable = false

        binding.titleBar.textViewMerchantName.text = arguments?.getString("merchantName")
        binding.titleBar.textViewMerchantDescription.text = arguments?.getString("description")
        arguments?.getInt("merchantLogoRes", 0)?.let {
            if (it != 0) {
                val drawable = ResourcesCompat.getDrawable(resources, it, null)
                binding.titleBar.imageViewMerchantLogo.setImageDrawable(drawable)
            }
        }

        setupTextInputs()

        binding.buttonComponent.onClickListener = {
            pay()
        }

        binding.titleBar.buttonClose.setOnClickListener {
            if (!isCancelable) {
                return@setOnClickListener
            }
            dismiss()
            hideKeyboard()
            callback(null)
        }

        binding.addressComponent.setup(
            shipping = collectShippingAddress,
            billing = collectBillingAddress
        )

        binding.addressComponent.onAddressUpdated = { _, _ ->
            updateButtonStatus()
        }

        updateError(null)
        updateEmailError(null)
        updateCardError(null)

        setupInitialState()
    }

    private fun setupTextInputs() {
        binding.emailComponent.emailChangedListener = {
            clearTextIfSavedEmail()
            if (EmailValidator().isValidEmail(it)) {
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

        binding.cardComponent.cardChangedListener = {
            clearTextIfSavedEmail()
            updateButtonStatus()
        }

        binding.cardComponent.expirationChangedListener = {
            clearTextIfSavedEmail()
            updateButtonStatus()
        }

        binding.cardComponent.cvcChangedListener = {
            updateButtonStatus()
        }

        binding.smsComponent.smsEnteredListener = {
            verifySMS()
        }
    }

    private fun setupInitialState() {
        when {
            checkoutRequest.donations != null -> {
                val donationsAdapter = DonationsAdapter(checkoutRequest.donations!!.toTypedArray())
                donationsAdapter.onItemClick = {
                    selectedDonation = it
                }
                binding.recyclerViewDonation.adapter = donationsAdapter
                binding.recyclerViewDonation.addItemDecoration(
                    DonationsAdapter.DonationItemDecoration(
                        context
                    )
                )
                switchMode(Mode.DONATION)
            }
            checkoutRequest.subscriptionPlanId != null -> {
                switchMode(Mode.LOADING)
                hideKeyboard()
                getCheckoutDetails()
                binding.rememberSwitchComponent.checked = checkoutRequest.rememberMe
            }
            checkoutManager.emailStorage.lastEmail != null || initialEmail != null -> {
                hideKeyboard()
                switchMode(Mode.LOADING)

                updateButtonStatus()
                binding.rememberSwitchComponent.checked = checkoutRequest.rememberMe
                updateAmountOnButton()
                binding.emailComponent.email =
                    initialEmail ?: checkoutManager.emailStorage.lastEmail
                if (binding.emailComponent.email != null) {
                    binding.cardComponent.setFocus()
                }
            }
            else -> {
                switchMode(Mode.NEW_CARD)
                updateButtonStatus()
                binding.rememberSwitchComponent.checked = checkoutRequest.rememberMe
                updateAmountOnButton()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        hideKeyboard()
        callback(null)
    }

    private fun switchMode(newMode: Mode) {
        if (currentMode == newMode) {
            return
        }
        currentMode = newMode

        binding.addressComponent.visibility = View.GONE
        binding.recyclerViewDonation.visibility = View.GONE
        binding.progressIndicator.visibility = View.GONE
        binding.cardComponent.visibility = View.GONE
        binding.smsComponent.visibility = View.GONE
        binding.rememberSwitchComponent.visibility = View.GONE
        binding.emailComponent.visibility = View.GONE
        binding.textViewAdditionalButtonInfo.visibility = View.GONE
        binding.viewButtonSeparator.visibility = View.GONE
        binding.buttonComponent.visibility = View.GONE
        binding.titleBar.buttonClose.visibility = View.GONE

        when (newMode) {
            Mode.LOADING -> {
                binding.progressIndicator.visibility = View.VISIBLE
            }
            Mode.NEW_CARD -> {
                if (collectShippingAddress || collectBillingAddress) {
                    binding.addressComponent.visibility = View.VISIBLE
                }
                binding.cardComponent.visibility = View.VISIBLE
                binding.rememberSwitchComponent.visibility = View.VISIBLE
                binding.emailComponent.visibility = View.VISIBLE
                binding.viewButtonSeparator.visibility = View.VISIBLE
                binding.buttonComponent.visibility = View.VISIBLE
                binding.titleBar.buttonClose.visibility = View.VISIBLE
                updateAmountOnButton()
                updateButtonStatus()
            }
            Mode.SMS -> {
                binding.smsComponent.visibility = View.VISIBLE
                binding.titleBar.buttonClose.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        binding.smsComponent.focus()
                        showKeyboard()
                    },
                    100,
                )
                binding.smsComponent.clean()
                binding.textViewAdditionalButtonInfo.visibility = View.VISIBLE
                binding.buttonComponent.visibility = View.VISIBLE
                binding.buttonComponent.setText(R.string.com_shift4_enter_payment_data)
                binding.buttonComponent.isEnabled = true
            }
            Mode.DONATION -> {
                binding.recyclerViewDonation.visibility = View.VISIBLE
                binding.viewButtonSeparator.visibility = View.VISIBLE
                binding.titleBar.buttonClose.visibility = View.VISIBLE

                binding.buttonComponent.visibility = View.VISIBLE
                binding.buttonComponent.setText(R.string.com_shift4_confirm)
                binding.buttonComponent.isEnabled = true
            }
            else -> {
            }
        }

        updateSwitchVisibility()
        updateError(null)
        updateEmailError(null)
        updateCardError(null)
    }

    private fun clearTextIfSavedEmail() {
        if (savedEmail == null) {
            return
        }
        savedEmail = null

        binding.cardComponent.clean()

        sms = null
        verifiedCard = false

        updateSwitchVisibility()
        updateError(null)
        updateEmailError(null)
        updateCardError(null)
    }

    private fun verifySMS() {
        if (processing) {
            return
        }
        setProcessing(true)
        updateError(null)
        GlobalScope.launch {
            val verifyResult = checkoutManager.verifySMS(
                binding.smsComponent.sms,
                sms!!
            )
            withContext(Dispatchers.Main) {
                setProcessing(false)
                when (verifyResult.status) {
                    Status.SUCCESS -> verifyResult.data?.also {
                        binding.cardComponent.setCreditCard(
                            CreditCard(card = it.card),
                            verifiedCard
                        )
                        fillCardForm()
                        switchMode(Mode.NEW_CARD)
                    }
                    Status.ERROR -> verifyResult.error?.also { error ->
                        binding.smsComponent.blinkError()
                        if (error.code != APIError.Code.InvalidVerificationCode) {
                            updateError(APIError.unknown.message(requireContext()))
                        }
                    }
                }
            }
        }
    }

    private fun startButtonAnimation() {
        binding.buttonComponent.state = ButtonComponent.State.PROGRESS
    }

    private fun revertButtonAnimation() {
        binding.buttonComponent.state = ButtonComponent.State.NORMAL
        updateAmountOnButton()
        updateButtonStatus()
    }

    private fun pay() {
        hideKeyboard()

        if (currentMode == Mode.DONATION) {
            switchMode(Mode.NEW_CARD)
            return
        }

        if (processing) {
            return
        }
        if (currentMode == Mode.SMS) {
            binding.cardComponent.clean()
            savedEmail = null
            sms = null
            switchMode(Mode.NEW_CARD)
            return
        }
        setProcessing(true)
        startButtonAnimation()
        val remember = binding.rememberSwitchComponent.checked
        val email = binding.emailComponent.email
        val number = binding.cardComponent.card
        val expiration = binding.cardComponent.expiration
        val cvc = binding.cardComponent.cvc

        val month = expiration?.split("/")?.first()
        val year = expiration?.split("/")?.last()

        hideKeyboard()
        updateError(null)
        updateEmailError(null)
        updateCardError(null)

        if (savedEmail != null) {
            GlobalScope.launch {
                val token = checkoutManager.savedToken(savedEmail!!)
                GlobalScope.launch(Dispatchers.Main) {
                    when (token.status) {
                        Status.SUCCESS -> checkoutManager.pay(
                            token.data!!,
                            checkoutRequest,
                            email ?: String.empty,
                            remember = remember,
                            requireActivity(),
                            sms = sms,
                            cvc = cvc,
                            customAmount = selectedDonation?.amount ?: subscription?.plan?.amount,
                            customCurrency = selectedDonation?.currency
                                ?: subscription?.plan?.currency,
                            shipping = binding.addressComponent.shipping,
                            billing = binding.addressComponent.billing
                        ) {
                            GlobalScope.launch(Dispatchers.Main) {
                                checkoutManager.emailStorage.lastEmail =
                                    if (it.data != null) savedEmail else null

                                if (it.error != null) {
                                    savedEmail = null
                                    binding.cardComponent.clean()
                                    updateSwitchVisibility()
                                }

                                processChargeResult(it)
                            }
                        }
                        Status.ERROR -> {
                            setProcessing(false)
                            revertButtonAnimation()
                            token.error?.let { updateError(it.message(this@CheckoutDialogFragment.requireContext())) }
                        }
                    }
                }
            }
            return
        }

        val tokenRequest = TokenRequest(
            number ?: String.empty,
            month ?: String.empty,
            year ?: String.empty,
            cvc ?: String.empty
        )
        GlobalScope.launch {
            checkoutManager.pay(
                tokenRequest,
                checkoutRequest,
                email!!,
                remember = remember,
                requireActivity(),
                customAmount = selectedDonation?.amount ?: subscription?.plan?.amount,
                customCurrency = selectedDonation?.currency ?: subscription?.plan?.currency,
                billing = binding.addressComponent.billing,
                shipping = binding.addressComponent.shipping
            ) {
                GlobalScope.launch(Dispatchers.Main) {
                    processChargeResult(it)
                }
            }
        }
    }

    private fun processChargeResult(charge: Result<ChargeResult>) {
        when (charge.status) {
            Status.SUCCESS -> charge.data?.also {
                binding.buttonComponent.state = ButtonComponent.State.SUCCESS
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        callback(charge)
                        dismiss()
                    },
                    500,
                )
            } ?: run {
                revertButtonAnimation()
                setProcessing(false)
            }
            Status.ERROR -> charge.error?.also { error ->
                setProcessing(false)
                hideKeyboard()
                revertButtonAnimation()
                if (error.type == APIError.Type.CardError && error.code != null) {
                    updateCardError(error.message(requireContext()))
                } else if (error.code == APIError.Code.InvalidEmail) {
                    updateEmailError(error.message(requireContext()))
                } else if (error.code == APIError.Code.VerificationCodeRequired) {
                    lookup()
                } else if (error.type == APIError.Type.ThreeDSecure) {
                    callback(Result.error(error))
                    Handler(Looper.getMainLooper()).postDelayed({
                        dismiss()
                    }, 150)
                } else {
                    updateError(error.message(requireContext()))
                }
            }
        }
    }

    private fun updateError(message: String?) {
        binding.textViewError.text = message
        if (message.isNullOrEmpty()) {
            binding.textViewError.visibility = View.GONE
            (binding.viewButtonSeparator.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                64
        } else {
            binding.textViewError.visibility = View.VISIBLE
            (binding.viewButtonSeparator.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                64
        }
    }

    private fun updateEmailError(message: String?) {
        binding.emailComponent.error = message
    }

    private fun updateCardError(message: String?) {
        binding.cardComponent.error = message
    }

    private fun updateButtonStatus() {
        val email = binding.emailComponent.email ?: String.empty
        val number = binding.cardComponent.card ?: String.empty
        val expiration = binding.cardComponent.expiration ?: String.empty
        val cvc = binding.cardComponent.cvc ?: String.empty

        val card = CreditCard(number = number)

        val correctEmail = email.isNotEmpty()
        val correctNumber = card.correct
        val correctExpiration = expiration.isNotEmpty()
        val correctCVC = cvc.isNotEmpty()
        val correctShipping = !collectShippingAddress || binding.addressComponent.shipping != null
        val correctBilling = !collectBillingAddress || binding.addressComponent.billing != null

        binding.buttonComponent.isEnabled =
            correctEmail && correctNumber && correctExpiration && correctCVC && correctShipping && correctBilling
    }

    private fun hideKeyboard() {
        view?.also { view ->
            binding.cardComponent.clearFocus()
            binding.smsComponent.clearFocus()
            binding.emailComponent.clearFocus()

            (context
                ?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showKeyboard() {
        (context
            ?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun lookup(silent: Boolean = false) {
        val email = binding.emailComponent.email.toString()
        if (email.isEmpty()) {
            return
        }
        if (!silent) {
            setProcessing(true)
        }
        GlobalScope.launch {
            val lookup = checkoutManager.lookup(email)
            withContext(Dispatchers.Main) {
                when (lookup.status) {
                    Status.SUCCESS -> lookup.data?.also { data ->
                        savedEmail = email

                        if (data.phone != null) {
                            if (data.phone.verified) {
                                verifiedCard = true
                            }
                            val sendSMSResult = checkoutManager.sendSMS(email)
                            withContext(Dispatchers.Main) {
                                setProcessing(false)
                                when (sendSMSResult.status) {
                                    Status.SUCCESS -> {
                                        sms = sendSMSResult.data
                                        switchMode(Mode.SMS)
                                    }
                                    Status.ERROR -> lookup.error?.also { error ->
                                        updateError(error.message(requireContext()))
                                        updateAmountOnButton()
                                    }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                binding.cardComponent.setCreditCard(
                                    CreditCard(card = data.card),
                                    verifiedCard
                                )
                                fillCardForm()
                                setProcessing(false)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    switchMode(Mode.NEW_CARD)
                                }, 200)
                            }
                        }
                    }
                    Status.ERROR -> lookup.error?.also { error ->
                        if (!silent) {
                            updateError(error.message(requireContext()))
                            updateAmountOnButton()
                            setProcessing(false)
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
        GlobalScope.launch {
            val details = checkoutManager.checkoutRequestDetails(checkoutRequest)
            GlobalScope.launch(Dispatchers.Main) {
                when (details.status) {
                    Status.SUCCESS -> {
                        subscription = details.data!!.subscription
                        switchMode(Mode.NEW_CARD)
                        binding.emailComponent.email =
                            initialEmail ?: checkoutManager.emailStorage.lastEmail
                        if (binding.emailComponent.email != null) {
                            binding.cardComponent.setFocus()
                        }
                    }
                    Status.ERROR -> {
                        callback(Result.error(APIError.unknown))
                        dismiss()
                    }
                }
            }
        }
    }

    private fun fillCardForm() {
        updateButtonStatus()
        updateSwitchVisibility()
    }

    private fun updateSwitchVisibility() {
        if (currentMode != Mode.NEW_CARD) {
            (binding.viewButtonSeparator.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                64
            binding.rememberSwitchComponent.visibility = View.GONE
            return
        }
        (binding.viewButtonSeparator.layoutParams as ViewGroup.MarginLayoutParams).topMargin = 0
        binding.rememberSwitchComponent.visibility = View.VISIBLE
    }

    private fun updateAmountOnButton() {
        val text = when {
            subscription != null -> getString(
                R.string.com_shift4_pay,
                subscription?.readable()
            )
            selectedDonation != null -> getString(
                R.string.com_shift4_pay,
                selectedDonation?.readable
            )
            else -> getString(R.string.com_shift4_pay, checkoutRequest.readable)
        }
        binding.buttonComponent.setText(text)
    }

    private fun setProcessing(processing: Boolean) {
        isCancelable = !processing
        this.processing = processing
        binding.emailComponent.isEnabled = !processing
        binding.cardComponent.isEnabled = !processing
        binding.rememberSwitchComponent.isEnabled = !processing
        binding.addressComponent.isEnabled = !processing
    }

    private fun callback(result: Result<ChargeResult>?) {
        (activity as? Shift4.CheckoutDialogFragmentResultListener)?.onCheckoutFinish(result)
    }
}