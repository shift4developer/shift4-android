# Shift4 Android SDK

Welcome to Shift4 Android SDK. Framework allows you to easily add Shift4 payments to your mobile apps. It allows you to integrate Shift4 with just a few lines of code.

## Features

#### Security

All sensitive data is sent directly to our servers instead of using your backend, so you can be sure that your payments are highly secure.

#### 3D-Secure

Add a smart 3D Secure verification with superior UX to your transactions. Provide smooth and uninterrupted payment experience that doesn’t interfere with your conversion process.

#### Translations

You can process payments in 18 languages.

## Installation

Add `shift4-android` to your `build.gradle` dependencies.

```
dependencies {
    implementation 'com.shift4:shift4-android:1.1.0'
}
```

## Usage

If you have not created an account yet, you can do it here: https://dev.shift4.com/signup.

### Configuration

To configure the framework you need to provide the public key. You can find it here: https://dev.shift4.com/account-settings. Notice that there are two types of keys: live and test. The type of key determines application mode. Make sure you used a live key in build released to Play Store.

If you are installing an app from a store other than the Play Store, enter its ID. Also provide the ID of the application. This data is useful in risk assessment for 3D-Secure transactions. 

```kotlin
val publicKey = "pk_test_..."
val trustedAppStores = listOf("com.google.android.packageinstaller") // Firebase App Distribution
val appIdentifier = "com.shift4.example"
shift4 = Shift4(applicationContext, publicKey, appIdentifier, trustedAppStores)
```

Remember to clean up the library in onDestroy method:

```kotlin
shift4.cleanUp()
```

### Checkout Dialog

Checkout Dialog is an out-of-box solution designed to provide the smoothest payment experience possible. It is a simple overlay with payments that appears on top of your page. Well-designed and ready to use.

To present Checkout Dialog you need to create Checkout Request on your backend side. You can find more information about Checkout Requests here: https://dev.shift4.com/docs/api#checkout-request. You can also create test Checkout Request here: https://dev.shift4.com/docs/checkout-request-generator.

```kotlin
val checkoutRequest = CheckoutRequest("...")
shift4.showCheckoutDialog(
    this,
    checkoutRequest = checkoutRequest,
    merchantName = "Example Merchant",
    description = "Example payment",
    merchantLogo = R.drawable.ic_example_merchant_logo, // Optional
    collectShippingAddress = true, // Optional
    collectBillingAddress = true, // Optional
    email = "example@mail.com" // Optional
)
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

### Custom form with 3D-secure

With custom form you can create a token and decorate it with 3D-secure authorization result.
```kotlin
 lifecycleScope.launch {
    val tokenRequest = TokenRequest(
        number = "4242424242424242",
        expMonth = "12",
        expYear = "2060",
        cvc = "123"
    )
     
     val token = shift4.createToken(tokenRequest).data!!
     val authenticatedToken = shift4.authenticate(
         token = token,
         amount = 100,
         currency = "USD",
         activity = this@MainActivity
     )
 }
```

### Google Pay with 3D-secure

When using Google Pay, you use a Google Pay token to create a Shift4 token and decorate it with 3D-secure authorization result. Please use Google Pay token "as is" without parsing it or decoding on your side.
```kotlin
 lifecycleScope.launch {
    val tokenRequest = TokenRequest(
        googlePay = TokenRequest.GooglePayRequest(GOOGLE_PAY_TOKEN)
    )
     
     val token = shift4.createToken(tokenRequest).data!!
     val authenticatedToken = shift4.authenticate(
         paymentMethod = token,
         amount = 100,
         currency = "USD",
         activity = this@MainActivity
     )
 }

```

## Testing
To check the correctness of the integration, we recommend testing with test cards in test mode to simulate successful charges as well as different types of errors. You can find list of card numbers here: https://dev.shift4.com/docs/testing. You can check status of every charge you made here: https://dev.shift4.com/charges.

To test the operation of 3D Secure, use the following cards:
```
Frictionless: 4012000100000114
OTP Challenge: 4016000000000004
```
We strongly recommend that you run these tests on a release-compiled application.

To test Google Pay, use Google Pay testing tokens:
```
Needs 3DS: PAN_ONLY
Without 3DS: CRYPTOGRAM_3DS
```

Remember not to make too many requests in a short period of time or you may reach a rate limit. If you reach the limit you have to wait 24h.

#### Possible errors

| Type          | Code                      | Message                                                | Explanation                                                  |
| ------------- | ------------------------- | ------------------------------------------------------ | ------------------------------------------------------------ |
| .sdk          | .unsupportedValue         | "Unsupported value: \(value)"                          |                                                              | 
| .sdk          | .incorrectCheckoutRequest | "Incorrect checkout request"                           | Checkout Request looks corrupted. Make sure you created it according to documentation. |
| .threeDSecure | .unknown                  | "Unknown 3D Secure Error. Check your SDK integration." |                                                              |
| .threeDSecure | .deviceJailbroken         | "The device is jailbroken."                            |                                                              |
| .threeDSecure | .integrityTampered        | "The integrity of the SDK has been tampered."          | Check your SDK integration. Error happens when you install app from untrusted App Store, when you try to run release version on Emulator or when your fingerprint is incorrect. |
| .threeDSecure | .simulator                | "An emulator is being used to run the app."            |                                                              |
| .threeDSecure | .osNotSupported           | "The OS or the OS version is not supported."           |                                                              |

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

At first that, open the project located in the /android subdirectory in the main project directory. Then perform the configuration described in the Installation section.

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
        val trustedAppStores = listOf("com.google.android.packageinstaller") // Firebase App Distribution
        val shift4 = Shift4(applicationContext, publicKey, trustedAppStores)

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

## Translations

SDK supports localization in for 18 languages. Your application must be localized.

## License

Framework is released under the MIT Licence.
3D Secure SDK is released under the Apache 2.0 Licence.
