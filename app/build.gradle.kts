import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "top.jingbh.zhixuehelper"
        minSdk = 23
        targetSdk = 31
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "app_version_info", "Version $versionName ($versionCode)")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".debug"

            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.collection:collection-ktx:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.4.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.paging:paging-runtime:3.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.android.material:material:1.6.0-alpha01")
    implementation("com.google.dagger:hilt-android:2.40.5")
    implementation("com.microsoft.appcenter:appcenter-analytics:4.4.2")
    implementation("com.microsoft.appcenter:appcenter-crashes:4.4.2")
    implementation("io.github.reactivecircus.cache4k:cache4k:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    kapt("com.google.dagger:hilt-android-compiler:2.40.5")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
