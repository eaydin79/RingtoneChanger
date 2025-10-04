plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.eaydin79.ringtonechanger"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.eaydin79.ringtonechanger"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.documentfile)
}