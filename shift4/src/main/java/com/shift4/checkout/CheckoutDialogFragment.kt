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
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shift4.R
import com.shift4.Shift4
import com.shift4.checkout.component.ButtonComponent
import com.shift4.data.api.Result
import com.shift4.data.model.pay.ChargeResult
import com.shift4.data.model.result.CheckoutResult
import com.shift4.databinding.ComShift4CheckoutDialogBinding


internal class CheckoutDialogFragment : BottomSheetDialogFragment() {
    private val viewModel: CheckoutDialogFragmentViewModel by viewModels()

    private var _binding: ComShift4CheckoutDialogBinding? = null
    private val binding get() = _binding!!

    private val modalBottomSheetBehavior: BottomSheetBehavior<FrameLayout> get() = (this.dialog as BottomSheetDialog).behavior

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ComShift4CheckoutDialogBinding.inflate(inflater, container, false)
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

        binding.titleBar.textViewMerchantName.text = requireArguments().getString("merchantName")
        binding.titleBar.textViewMerchantDescription.text =
            requireArguments().getString("description")
        requireArguments().getInt("merchantLogoRes", 0).let {
            if (it != 0) {
                val drawable = ResourcesCompat.getDrawable(resources, it, null)
                binding.titleBar.imageViewMerchantLogo.setImageDrawable(drawable)
            }
        }

        binding.emailComponent.emailChangedListener = viewModel::onEmailChange
        binding.cardComponent.cardChangedListener = viewModel::onCardChange
        binding.cardComponent.expirationChangedListener = viewModel::onExpChange
        binding.cardComponent.cvcChangedListener = viewModel::onCvcChange
        binding.smsComponent.smsEnteredListener = viewModel::onSmsEntered
        binding.addressComponent.onAddressUpdated = viewModel::onAddressEntered
        binding.buttonComponent.onClickListener = { viewModel.onClickButton(requireActivity()) }
        binding.rememberSwitchComponent.onCheckedListener = viewModel::onRememberSwitchChange

        viewModel.emailValue.observe(viewLifecycleOwner) {
            binding.emailComponent.email = it
        }

        viewModel.cardValue.observe(viewLifecycleOwner) {
            binding.cardComponent.card = it
        }

        viewModel.expValue.observe(viewLifecycleOwner) {
            binding.cardComponent.expiration = it
        }

        viewModel.cvcValue.observe(viewLifecycleOwner) {
            binding.cardComponent.cvc = it
        }

        viewModel.billingValue.observe(viewLifecycleOwner) {
            binding.addressComponent.billing = it
        }

        viewModel.shippingValue.observe(viewLifecycleOwner) {
            binding.addressComponent.shipping = it
        }

        viewModel.rememberValue.observe(viewLifecycleOwner) {
            binding.rememberSwitchComponent.checked = it
        }

        viewModel.isAddressComponentVisible.observe(viewLifecycleOwner) {
            binding.addressComponent.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isAddressComponentEnabled.observe(viewLifecycleOwner) {
            binding.addressComponent.isEnabled = it
        }

        viewModel.isDonationComponentVisible.observe(viewLifecycleOwner) {
            binding.recyclerViewDonation.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isProgressComponentVisible.observe(viewLifecycleOwner) {
            binding.progressIndicator.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isCardComponentEnabled.observe(viewLifecycleOwner) {
            binding.cardComponent.isEnabled = it
        }

        viewModel.isCardComponentVisible.observe(viewLifecycleOwner) {
            binding.cardComponent.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.cleanCardComponent.observe(viewLifecycleOwner) {
            if (it) {
                binding.cardComponent.clean()
            }
        }

        viewModel.isCardComponentFocused.observe(viewLifecycleOwner) {
            if (it) {
                binding.cardComponent.setFocus()
            } else {
                binding.cardComponent.clearFocus()
            }
        }

        viewModel.creditCardValue.observe(viewLifecycleOwner) {
            binding.cardComponent.setCreditCard(it.first, it.second)
        }

        viewModel.isSmsComponentVisible.observe(viewLifecycleOwner) {
            binding.smsComponent.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isSmsComponentFocused.observe(viewLifecycleOwner) {
            if (it) {
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        binding.smsComponent.focus()
                        showKeyboard()
                    },
                    100,
                )
                binding.smsComponent.clean()
            }
        }

        viewModel.isSmsComponentError.observe(viewLifecycleOwner) {
            if (it) {
                binding.smsComponent.blinkError()
            }
        }

        viewModel.isSwitchVisible.observe(viewLifecycleOwner) {
            if (it) {
                (binding.viewButtonSeparator.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                    0
                binding.rememberSwitchComponent.visibility = View.VISIBLE
            } else {
                (binding.viewButtonSeparator.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
                    64
                binding.rememberSwitchComponent.visibility = View.GONE
            }
        }

        viewModel.isEmailComponentVisible.observe(viewLifecycleOwner) {
            binding.emailComponent.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isEmailComponentEnabled.observe(viewLifecycleOwner) {
            binding.emailComponent.isEnabled = it
        }

        viewModel.isAdditionalButtonInfoVisible.observe(viewLifecycleOwner) {
            binding.textViewAdditionalButtonInfo.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isButtonSeparatorVisible.observe(viewLifecycleOwner) {
            binding.viewButtonSeparator.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.isButtonComponentVisible.observe(viewLifecycleOwner) {
            binding.buttonComponent.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.buttonComponentText.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.buttonComponent.setText(getString(it.first, it.second))
            } else {
                binding.buttonComponent.setText("")
            }
        }

        viewModel.isButtonComponentEnabled.observe(viewLifecycleOwner) {
            binding.buttonComponent.isEnabled = it
        }

        viewModel.buttonComponentState.observe(viewLifecycleOwner) {
            binding.buttonComponent.state = it
        }

        viewModel.isButtonAnimating.observe(viewLifecycleOwner) {
            if (it) {
                binding.buttonComponent.state = ButtonComponent.State.PROGRESS
            } else {
                binding.buttonComponent.state = ButtonComponent.State.NORMAL
            }
        }

        viewModel.isButtonCloseVisible.observe(viewLifecycleOwner) {
            binding.titleBar.buttonClose.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.donationsAdapter.observe(viewLifecycleOwner) {
            binding.recyclerViewDonation.adapter = it
        }

        viewModel.error.observe(viewLifecycleOwner) {
            val message = it?.message(requireContext())
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

        viewModel.emailError.observe(viewLifecycleOwner) {
            binding.emailComponent.error = it?.message(requireContext())
        }

        viewModel.cardError.observe(viewLifecycleOwner) {
            binding.cardComponent.error = it?.message(requireContext())
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

        binding.titleBar.buttonClose.setOnClickListener {
            if (!isCancelable) {
                return@setOnClickListener
            }
            dismiss()
            hideKeyboard()
            callback(null)
        }

        binding.recyclerViewDonation.addItemDecoration(
            DonationsAdapter.DonationItemDecoration(requireContext())
        )

        viewModel.initialize(requireArguments(), requireContext())

        binding.addressComponent.setup(
            shipping = viewModel.collectShippingAddress,
            billing = viewModel.collectBillingAddress
        )
    }

    override fun onResume() {
        super.onResume()
        binding.emailComponent.initialize()
        binding.cardComponent.initialize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            binding.cardComponent.clearFocus()
            binding.smsComponent.clearFocus()
            binding.emailComponent.clearFocus()
            binding.addressComponent.clearFocus()

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