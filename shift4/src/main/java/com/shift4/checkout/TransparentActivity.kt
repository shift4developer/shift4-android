package com.shift4.checkout

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.shift4.Shift4
import com.shift4.Shift4.Companion.SHIFT4_RESULT_CODE
import com.shift4.data.model.result.CheckoutResult


class TransparentActivity : AppCompatActivity(), Shift4.CheckoutDialogFragmentResultListener,
    FragmentDismissalListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        val checkoutDialogFragment = CheckoutDialogFragment()
        checkoutDialogFragment.arguments = intent.extras
        checkoutDialogFragment.show(supportFragmentManager, "checkoutDialogFragment")
    }

    private var checkoutResult: CheckoutResult? = null

    override fun onCheckoutFinish(result: CheckoutResult?) {
        checkoutResult = result
    }

    override fun onDismiss() {
        val intent = Intent()
        intent.putExtra("result", checkoutResult)
        setResult(SHIFT4_RESULT_CODE, intent)
        finish()
    }
}