#Butterknife
 -keep class butterknife.** { *; }
 -dontwarn butterknife.internal.**
 -keep class **$$ViewBinder { *; }

 -keepclasseswithmembernames class * {
     @butterknife.* <fields>;
 }

 -keepclasseswithmembernames class * {
     @butterknife.* <methods>;
 }

#Retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

#OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

#Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

#Retrolambda
-dontwarn java.lang.invoke.*

#CODE obfuscation
-keepclassmembers class com.codepath.models** { <fields>; }

#Crashlytics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

#Firebase
-keep class com.google.firebase.provider.FirebaseInitProvider

#Maps -- Google
-optimizations !code/simplification/variable
-keep class android.location.** { *; }
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment

# The Maps API uses custom Parcelables.
# Use this rule (which is slightly broader than the standard recommended one)
# to avoid obfuscating them.
-keepclassmembers class * implements android.os.Parcelable {
    static *** CREATOR;
}

# The Maps API uses serialization.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Google, Maps,Location,FusedClient
-keep class com.google.android.gms.location.** { *; }
-keep class com.google.android.gms.internal.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.google.android.** { *; }
-keep class com.google.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.common.api.** { *; }
-dontwarn com.google.android

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

#Parcelable
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#avi loader
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }
#Firebase
-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

-keep class com.cityzipcorp.customer.** { *; }


