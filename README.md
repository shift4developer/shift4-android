# Shift4 Android SDK

Welcome to Shift4 Android SDK. Framework allows you to easily add Shift4 payments to your mobile apps. It allows you to integrate Shift4 with just a few lines of code. It also exposes low-level Shift4 API which you can use to create custom payment form.

## Features

#### Security

All sensitive data is sent directly to our servers instead of using your backend, so you can be sure that your payments are highly secure.

#### 3D-Secure

Add a smart 3D Secure verification with superior UX to your transactions. Provide smooth and uninterrupted payment experience that doesn’t interfere with your conversion process.

#### Shift4 API

We provide methods corresponding to Shift4 API. It allows you creating an entirely custom UI embedded into your application to increase conversion by keeping clients inside your app.

#### Translations

You can process payments in 18 languages.

## Requirements and limitations

Strict requirements of PCI 3DS SDK make development impossible. Running on simulator or debugging are forbidden in a production build of your application. We provide two versions of Framework both for Debug and Release builds so you can create and debug your application without any issues.

Releasing using a store other than Play Store is forbidden by default. If you want to use a different store, for example Firebase App Distribution, you have to provide their identifiers as described below.

## Installation

3D Secure library license requirements force us to distribute it via email. Contact devsupport@shift4.com to get it. Download both ipworks3ds_sdk_deploy.aar and ipworks3ds_sdk.aar 3D-Secure libraries, copy them to your project and add as dependencies. Next, add `shift4-android` and `shift4-android-debug` to your `build.gradle` dependencies.

```
dependencies {
    implementation 'com.shift4:shift4-android:1.1.0'
    releaseImplementation files('ipworks3ds_sdk_deploy.aar')
    debugImplementation files('ipworks3ds_sdk.aar')
}
```

## Usage

If you have not created an account yet, you can do it here: https://dev.shift4.com/signup.

### Configuration

To configure the framework you need to provide the public key. You can find it here: https://dev.shift4.com/account-settings. Notice that there are two types of keys: live and test. The type of key determines application mode. Make sure you used a live key in build released to Play Store.

Framework also requires you to specify App Signature. This property should be set to the SHA256 fingerprint of the certificate used to sign the app. You can find it in Google Play Console. Any attempt to perform the 3D Secure operation in release mode results in error if they do not match. This value should not be hardcoded in the application for security reasons. You should provide it on your backend side.

Releasing using a store other than Play Store is forbidden by default. If you want to use a different store, for example Firebase App Distribution, you have to provide their identifiers.

```kotlin
val publicKey = "pk_test_..."
val signature = "00:11:22...."
val trustedAppStores = listOf("com.google.android.packageinstaller") // Firebase App Distribution
val shift4 = Shift4(applicationContext, publicKey, signature, trustedAppStores)
```

### Checkout Dialog

Checkout Dialog is an out-of-box solution designed to provide the smoothest payment experience possible. It is a simple overlay with payments that appears on top of your page. Well-designed and ready to use.

To present Checkout Dialog you need to create Checkout Request on your backend side. You can find more informations about Checkout Requests here: https://dev.shift4.com/docs/api#checkout-request. You can also create test Checkout Request here: https://dev.shift4.com/docs/checkout-request-generator.

```kotlin
val checkoutRequest = CheckoutRequest("...")
shift4.showCheckoutDialog(this, checkoutRequest, "Example merchant", "Example payment")
```

To receive the callback from Checkout Dialog, you have to implement interface `Shift4.CheckoutDialogFragmentResultListener`.

```kotlin
class MainActivity : AppCompatActivity(), Shift4.CheckoutDialogFragmentResultListener {
    override fun onCheckoutFinish(result: CheckoutResult?) {
        if (result == null) {
            // cancelled
        }
        if (result?.data != null) {
            // success
        }
        if (result?.error != null) {
            // error
        }
    }
}
```

We strongly recommend using AppCompatActivity to get the best UX experience. However, if you decide to use Activity, you need to implement the following additional code. This is necessary, for example, if you integrate the SDK into a Flutter App.
```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Shift4.SHIFT4_RESULT_CODE) {
        onCheckoutFinish(data?.getSerializableExtra("result") as CheckoutResult?)
    }
}
```
#### Saved cards

Checkout View Controller has a feature allowing to remember cards used before. To delete them, use code:

```kotlin
shift4.cleanSavedCards()
```

#### Possible errors

| Type          | Code                      | Message                                                | Explanation                                                  |
| ------------- | ------------------------- | ------------------------------------------------------ | ------------------------------------------------------------ |
| .sdk          | .unsupportedValue         | "Unsupported value: \(value)"                          | Framework does not accept Checkout Request fields: **termsAndConditionsUrl**, **customerId**, **crossSaleOfferIds**. |
| .sdk          | .incorrectCheckoutRequest | "Incorrect checkout request"                           | Checkout Request looks corrupted. Make sure you created it according to documentation. |
| .threeDSecure | .unknown                  | "Unknown 3D Secure Error. Check your SDK integration." |                                                              |
| .threeDSecure | .deviceJailbroken         | "The device is jailbroken."                            |                                                              |
| .threeDSecure | .integrityTampered        | "The integrity of the SDK has been tampered."          | Check your SDK integration. Error happens when you install app from untrusted App Store, when you try to run release version on Emulator or when your fingerprint is incorrect. |
| .threeDSecure | .simulator                | "An emulator is being used to run the app."            |                                                              |
| .threeDSecure | .osNotSupported           | "The OS or the OS version is not supported."           |                                                              |

### Custom Form

```kotlin
val tokenRequest = TokenRequest("4242424242424242", "10", "2034", "123")
shift4.createToken(tokenRequest) { token ->
    when (token.status) {
        Status.ERROR -> {
            Log.e("Shift4", token.error!!.message(this))
        }
        Status.SUCCESS -> {
            shift4.authenticate(token.data!!, 10000, "EUR", this) { authenticatedToken ->
                when(authenticatedToken.status) {
                    Status.SUCCESS -> {
                        Log.d("Shift4", token.data!!.id)
                    }
                    Status.ERROR -> {
                        Log.e("Shift4", token.error!!.message(this))
                    }
                }
            }
        }
    }
}
```

#### Possible errors

##### Creating token

| Type       | Code                | Message                                              | Explanation |
| ---------- | ------------------- | ---------------------------------------------------- | ----------- |
| .cardError | .invalidNumber      | "The card number is not a valid credit card number." |             |
| .cardError | .invalidExpiryMonth | "The card's expiration month is invalid."            |             |
| .cardError | .invalidExpiryYear  | "The card's expiration year is invalid."             |             |
| .cardError | .expiredCard        | "The card has expired."                              |             |
| .cardError | .invalidCVC         | "Your card's security code is invalid."              |             |

##### Authentication

| Type          | Code               | Message                                                | Explanation                                                  |
| ------------- | ------------------ | ------------------------------------------------------ | ------------------------------------------------------------ |
| .sdk          | .anotherOperation  | "Another task is in progress."                         | You can complete only one authentication operation at a time. Your UI should prevent it from being triggered multiple times. |
| .threeDSecure | .unknown           | "Unknown 3D Secure Error. Check your SDK integration." |                                                              |
| .threeDSecure | .deviceJailbroken  | "The device is jailbroken."                            |                                                              |
| .threeDSecure | .integrityTampered | "The integrity of the SDK has been tampered."          | Check your SDK integration. Error happens when you install app from untrusted App Store, when you try to run release version on Emulator or when your fingerprint is incorrect. |
| .threeDSecure | .simulator         | "An emulator is being used to run the app."            |                                                              |
| .threeDSecure | .osNotSupported    | "The OS or the OS version is not supported."           |                                                              |

## Flutter

The SDK is created in native technologies, but since Flutter allows you to use native components, integrating the library on this platform is possible, but requires a few additional steps.

At first that, open the project located in the /android subdirectory in the main project directory. Then perform the configuration described in the Installation section. Don't forget to request the 3D-Secure library, without which you won't compile the project, and which we can't publish on github for licensing reasons. You will get it by contacting devsupport@shift4.com.

Add following code to onCreate method of calling activity:
```kotlin
val checkoutChannel = MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, "com.example/checkout")
checkoutChannel.setMethodCallHandler { call: MethodCall, result: MethodChannel.Result ->
    if (call.method == "checkout") {
        performCheckout(result)
    } else {
        result.notImplemented()
    }
}
```

And create the following method and variable:
```kotlin
    private var flutterChannelResult: MethodChannel.Result? = null

    private fun performCheckout(result: MethodChannel.Result) {
        flutterChannelResult = result

        val checkoutRequest = CheckoutRequest("...")
    
        val publicKey = "pk_test_..."
        val signature = "00:11:22...."
        val trustedAppStores = listOf("com.google.android.packageinstaller") // Firebase App Distribution
        val shift4 = Shift4(applicationContext, publicKey, signature, trustedAppStores)

        shift4.showCheckoutDialog(this, checkoutRequest, "Example merchant", "Example payment")
    }
```

Then implement listeners as in the Installation section:

```kotlin
override fun onCheckoutFinish(result: CheckoutResult?) {
    if (result == null) {
        // cancelled
    }
    if (result?.data != null) {
        flutterChannelResult?.success(result.toMap())
    }
    if (result?.error != null) {
        flutterChannelResult?.error(result.error!!.code.toString(), result.error!!.message(context), null)
    }
    flutterChannelResult = null
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Shift4.SHIFT4_RESULT_CODE) {
        onCheckoutFinish(data?.getSerializableExtra("result") as CheckoutResult?)
    }
}
```

Remember to provide the appropriate publicKey and checkoutRequest.

In the State of your application add the lines:

```dart
static const platform = MethodChannel('com.example/checkout');

Future<void> _checkout() async {
  try {
    final Map result = await platform.invokeMethod('checkout');
    print(result);
  } on PlatformException catch (e) {
    print(e);
  }
}
```

And execute the created function somewhere, such as creating a button:

```dart
TextButton(
    onPressed: _checkout, 
    child: const Text('Hello Shift4!')),
```

That's it. You can launch your app.


## Testing

When making requests in test mode you have to use special card numbers to simulate successful charges or processing errors. You can find list of card numbers here: https://dev.shift4.com/docs/testing. You can check status of every charge you made here: https://dev.shift4.com/charges.

Remember not to make too many requests in a short period of time or you may reach a rate limit. If you reach the limit you have to wait 24h.

## Translations

SDK supports localization in for 18 languages. Your application must be localized.

## License

Framework is released under the MIT Licence.
