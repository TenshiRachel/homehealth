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

# Keep Firebase (breaks if renamed)
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep data models (Firestore maps by field name)
-keep class com.example.homehealth.data.models.** { *; }

# Keep BuildConfig (Firebase credentials read from here)
-keep class com.example.homehealth.BuildConfig { *; }

# Kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Prevent debug UI tooling from breaking with minification
-keep class androidx.compose.ui.tooling.** { *; }
-keep class androidx.compose.ui.test.** { *; }
-dontwarn androidx.compose.ui.tooling.**
-dontwarn androidx.compose.ui.test.**

# Fix SuspendToFutureAdapter missing class
-dontwarn androidx.concurrent.**
-keep class androidx.concurrent.** { *; }

# Fix androidTest minification warning
-dontwarn androidx.test.**
-keep class androidx.test.** { *; }