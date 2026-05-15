# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the SDK tools.

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.weathersnap.backend.api.dto.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keep class com.weathersnap.backend.model.** { *; }
