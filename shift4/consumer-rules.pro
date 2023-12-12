-keepclassmembers,allowobfuscation class * {
 @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.shift4.data.model.** { *; }
-keep class com.shift4.data.model.pay.CheckoutRequest { *; }
-keep class com.shift4.data.model.pay.ChargeResult { *; }
-keep class com.shift4.data.model.result.CheckoutResult { *; }

-keep class com.shift4.Shift4 { *; }
-keep interface com.shift4.Shift4$CheckoutDialogFragmentResultListener { *;}

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer