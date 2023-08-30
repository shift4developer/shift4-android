package com.shift4.checkout

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shift4.R
import com.shift4.Shift4
import com.shift4.checkout.component.*
import com.shift4.data.api.Result
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.result.CheckoutResult


internal class CheckoutDialogFragment : BottomSheetDialogFragment() {
    private lateinit var rootView: View
    private lateinit var buttonClose: AppCompatImageButton
    private lateinit var textViewMerchantName: TextView
    private lateinit var textViewMerchantDescription: TextView
    private lateinit var imageViewMerchantLogo: ImageView
    private lateinit var emailComponent: EmailComponent
    private lateinit var cardComponent: CardComponent
    private lateinit var smsComponent: SMSComponent
    private lateinit var addressComponent: AddressComponent
    private lateinit var viewButtonSeparator: View
    private lateinit var textViewAdditionalButtonInfo: TextView
    private lateinit var buttonComponent: ButtonComponent
    private lateinit var rememberSwitchComponent: SwitchComponent
    private lateinit var recyclerViewDonation: RecyclerView
    private lateinit var progressIndicator: ProgressBar
    private lateinit var textViewError: TextView

    private val viewModel: CheckoutDialogFragmentViewModel by viewModels()

    private val modalBottomSheetBehavior: BottomSheetBehavior<FrameLayout> get() = (this.dialog as BottomSheetDialog).behavior

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.com_shift4_checkout_dialog, container, false)
        buttonClose = rootView.findViewById(R.id.buttonClose)
        textViewMerchantName = rootView.findViewById(R.id.textViewMerchantName)
        textViewMerchantDescription = rootView.findViewById(R.id.textViewMerchantDescription)
        imageViewMerchantLogo = rootView.findViewById(R.id.imageViewMerchantLogo)
        emailComponent = rootView.findViewById(R.id.emailComponent)
        cardComponent = rootView.findViewById(R.id.cardComponent)
        smsComponent = rootView.findViewById(R.id.smsComponent)
        addressComponent = rootView.findViewById(R.id.addressComponent)
        viewButtonSeparator = rootView.findViewById(R.id.viewButtonSeparator)
        textViewAdditionalButtonInfo = rootView.findViewById(R.id.textViewAdditionalButtonInfo)
        buttonComponent = rootView.findViewById(R.id.buttonComponent)
        rememberSwitchComponent = rootView.findViewById(R.id.rememberSwitchComponent)
        recyclerViewDonation = rootView.findViewById(R.id.recyclerViewDonation)
        progressIndicator = rootView.findViewById(R.id.progressIndicator)
        textViewError = rootView.findViewById(R.id.textViewError)
        return rootView
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

        textViewMerchantName.text = requireArguments().getString("merchantName")
        textViewMerchantDescription.text =
            requireArguments().getString("description")
        requireArguments().getInt("merchantLogoRes", 0).let {
            if (it != 0) {
                val drawable = ResourcesCompat.getDrawable(resources, it, null)
                imageViewMerchantLogo.setImageDrawable(drawable)
            }
        }

        emailComponent.emailChangedListener = viewModel::onEmailChange
        cardComponent.cardChangedListener = viewModel::onCardChange
        cardComponent.expirationChangedListener = viewModel::onExpChange
        cardComponent.cvcChangedListener = viewModel::onCvcChange
        smsComponent.smsEnteredListener = viewModel::onSmsEntered
        addressComponent.onAddressUpdated = viewModel::onAddressEntered
        buttonComponent.onClickListener = { viewModel.onClickButton(requireActivity()) }
        rememberSwitchComponent.onCheckedListener = viewModel::onRememberSwitchChange

        viewModel.emailValue.observe(viewLifecycleOwner) {
            emailComponent.email = it
        }

        viewModel.cardValue.observe(viewLifecycleOwner) {
            cardComponent.card = it
        }

        viewModel.expValue.observe(viewLifecycleOwner) {
            cardComponent.expiration = it
        }

        viewModel.cvcValue.observe(viewLifecycleOwner) {
            cardComponent.cvc = it
        }

        viewModel.billingValue.observe(viewLifecycleOwner) {
            addressComponent.billing = it
        }

        viewModel.shippingValue.observe(viewLifecycleOwner) {
            addressComponent.shipping = it
        }

        viewModel.rememberValue.observe(viewLifecycleOwner) {
            rememberSwitchComponent.checked = it
        }

        viewModel.isRememberSwitchComponentEnabled.observe(viewLifecycleOwner) {
            rememberSwitchComponent.isEnabled = it
        }

        viewModel.isAddressComponentVisible.observe(viewLifecycleOwner) {
            addressComponent.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isAddressComponentEnabled.observe(viewLifecycleOwner) {
            addressComponent.isEnabled = it
        }

        viewModel.isDonationComponentVisible.observe(viewLifecycleOwner) {
            recyclerViewDonation.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isProgressComponentVisible.observe(viewLifecycleOwner) {
            progressIndicator.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isCardComponentEnabled.observe(viewLifecycleOwner) {
            cardComponent.isEnabled = it
        }

        viewModel.isCardComponentVisible.observe(viewLifecycleOwner) {
            cardComponent.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.cleanCardComponent.observe(viewLifecycleOwner) {
            if (it) {
                cardComponent.clean()
            }
        }

        viewModel.isCardComponentFocused.observe(viewLifecycleOwner) {
            if (it) {
                cardComponent.setFocus()
            } else {
                cardComponent.clearFocus()
            }
        }

        viewModel.creditCardValue.observe(viewLifecycleOwner) {
            cardComponent.setCreditCard(it.first, it.second)
        }

        viewModel.isSmsComponentVisible.observe(viewLifecycleOwner) {
            smsComponent.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isSmsComponentFocused.observe(viewLifecycleOwner) {
            if (it) {
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        smsComponent.focus()
                        showKeyboard()
                    },
                    100,
                )
                smsComponent.clean()
            }
        }

        viewModel.isSmsComponentError.observe(viewLifecycleOwner) {
            if (it) {
                smsComponent.blinkError()
            }
        }

        viewModel.isSwitchVisible.observe(viewLifecycleOwner) {
            if (it) {
                (viewButtonSeparator.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                    0
                rememberSwitchComponent.visibility = View.VISIBLE
            } else {
                (viewButtonSeparator.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                    64
                rememberSwitchComponent.visibility = View.GONE
            }
        }

        viewModel.isEmailComponentVisible.observe(viewLifecycleOwner) {
            emailComponent.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isEmailComponentEnabled.observe(viewLifecycleOwner) {
            emailComponent.isEnabled = it
        }

        viewModel.isAdditionalButtonInfoVisible.observe(viewLifecycleOwner) {
            textViewAdditionalButtonInfo.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isButtonSeparatorVisible.observe(viewLifecycleOwner) {
            viewButtonSeparator.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isButtonComponentVisible.observe(viewLifecycleOwner) {
            buttonComponent.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.buttonComponentText.observe(viewLifecycleOwner) {
            if (it != null) {
                buttonComponent.setText(getString(it.first, it.second))
            } else {
                buttonComponent.setText("")
            }
        }

        viewModel.isButtonComponentEnabled.observe(viewLifecycleOwner) {
            buttonComponent.isEnabled = it
        }

        viewModel.buttonComponentState.observe(viewLifecycleOwner) {
            buttonComponent.state = it
        }

        viewModel.isButtonAnimating.observe(viewLifecycleOwner) {
            if (it) {
                buttonComponent.state = ButtonComponent.State.PROGRESS
            } else {
                buttonComponent.state = ButtonComponent.State.NORMAL
            }
        }

        viewModel.isButtonCloseVisible.observe(viewLifecycleOwner) {
            buttonClose.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.donationsAdapter.observe(viewLifecycleOwner) {
            recyclerViewDonation.adapter = it
        }

        viewModel.error.observe(viewLifecycleOwner) {
            val message = it?.message(requireContext())
            textViewError.text = message
            if (message.isNullOrEmpty()) {
                textViewError.visibility = View.GONE
                (viewButtonSeparator.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    64
            } else {
                textViewError.visibility = View.VISIBLE
                (viewButtonSeparator.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    64
            }
        }

        viewModel.emailError.observe(viewLifecycleOwner) {
            emailComponent.error = it?.message(requireContext())
        }

        viewModel.cardError.observe(viewLifecycleOwner) {
            cardComponent.error = it?.message(requireContext())
        }

        viewModel.isKeyboardVisible.observe(viewLifecycleOwner) {
            if (it) {
                showKeyboard()
            } else {
                hideKeyboard()
            }
        }

        viewModel.isCancelable.observe(viewLifecycleOwner) { isCancelable = it }

        viewModel.callback.observe(viewLifecycleOwner) {
            callback(it)
            dismiss()
        }

        buttonClose.setOnClickListener {
            if (!isCancelable) {
                return@setOnClickListener
            }
            dismiss()
            hideKeyboard()
            callback(null)
        }

        recyclerViewDonation.addItemDecoration(
            DonationsAdapter.DonationItemDecoration(requireContext())
        )

        viewModel.initialize(requireArguments(), requireContext())

        addressComponent.setup(
            shipping = viewModel.collectShippingAddress,
            billing = viewModel.collectBillingAddress
        )
    }

    override fun onResume() {
        super.onResume()
        emailComponent.initialize()
        cardComponent.initialize()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.checkoutManager.threeDManager.closeTransaction()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        hideKeyboard()
        callback(null)
    }

    private fun hideKeyboard() {
        view?.also { view ->
            cardComponent.clearFocus()
            smsComponent.clearFocus()
            emailComponent.clearFocus()
            addressComponent.clearFocus()

            (requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showKeyboard() {
        (requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun callback(result: Result<ChargeResult>?) {
        val checkoutResult = result?.let { CheckoutResult(it.status, it.data, it.error) }
        (activity as? Shift4.CheckoutDialogFragmentResultListener)?.onCheckoutFinish(checkoutResult)
        (activity as? FragmentDismissalListener)?.onDismiss()
    }
}