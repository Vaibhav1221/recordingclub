# ✅ KEEP all classes in the external module
-keep class com.videvelopers.app.vuh.** { *; }

# ✅ DONTWARN rules to suppress related warnings
-dontwarn com.videvelopers.app.vuh.AppInitializer
-dontwarn com.videvelopers.app.vuh.app_components.AppCore
-dontwarn com.videvelopers.app.vuh.app_helpers.AppActionBar
-dontwarn com.videvelopers.app.vuh.app_helpers.AppFunctions
-dontwarn com.videvelopers.app.vuh.app_helpers.URLHelpers

# Optional if you're using crypto providers
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn proguard.annotation.**


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