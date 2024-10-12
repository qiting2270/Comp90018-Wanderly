plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.wanderly"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wanderly"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.camera.view)
    implementation(libs.play.services.location)
    implementation(libs.camera.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.glide)


    // CameraX core library using Camera2 implementation
    implementation (libs.camera.core)
    implementation (libs.camera.camera2)
    implementation (libs.camera.lifecycle.v110)
    implementation (libs.camera.view.v100alpha31)
    implementation (libs.guava)


}