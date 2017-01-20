# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Development\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class username to the JavaScript interfaces
# class:
#-keepclassmembers class fqcn.of.javascript.interfaces.for.webview {
#   public *;
#
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-printmapping mapping.txt
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**


-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.popdq.app.model.** { *; }
-keep class com.popdq.app.gcm.** { *; }
-keepattributes *Annotation*

-dontwarn org.apache.**
-keep class org.apache.** { *; }
-dontwarn android.net.http.AndroidHttpClient
-keep class android.support.** { *; }
-keep class com.google.** { *; }
-dontwarn org.joda.**
-keep class org.joda.** { *; }
-keep class org.webrtc.** { *; }
-assumenosideeffects class android.util.Log {
public static *** d(...);
public static *** w(...);
public static *** v(...);
public static *** i(...);
}

