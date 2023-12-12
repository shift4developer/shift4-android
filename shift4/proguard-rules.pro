# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-repackageclasses "com.shift4.sdkinternal"

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

-if class **$Companion extends **
-keep class <2>
-if class **$Companion implements **
-keep class <2>


#########

-keep class com.shift4.threedsecure.pub.* { *; }
-keep class com.shift4.threedsecure.exception.* { *; }

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keep class org.bouncycastle.** { *; }