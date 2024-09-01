plugins {
    id("com.android.application")

    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" // this version matches your Kotlin version
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "com.sduduzog.slimlauncher"
        minSdk = 26
        targetSdk = 34
        versionName = "2.4.22"
        versionCode = 55
        vectorDrawables { useSupportLibrary = true }
        signingConfigs {
            if (project.extra.has("RELEASE_STORE_FILE")) {
                register("release") {
                    storeFile = file(project.extra["RELEASE_STORE_FILE"] as String)
                    storePassword = project.extra["RELEASE_STORE_PASSWORD"] as String
                    keyAlias = project.extra["RELEASE_KEY_ALIAS"] as String
                    keyPassword = project.extra["RELEASE_KEY_PASSWORD"] as String
                }
            }
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    buildTypes {
        named("release").configure {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.maybeCreate("release")
        }
        named("debug").configure {
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
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    lint {
        abortOnError = false
    }
    namespace = "com.sduduzog.slimlauncher"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin Libraries
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.20")

    // Support Libraries
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Arch Components
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.4")
    implementation("androidx.preference:preference-ktx:1.2.1")
    kapt("androidx.room:room-compiler:2.6.1")

    //3rd party libs
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")

    // Integration with activities
    implementation("androidx.activity:activity-compose:1.9.1")
    // Compose Material Design
    implementation("androidx.compose.material:material:1.6.8")
    // Animations
    implementation("androidx.compose.animation:animation:1.6.8")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.6.8")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    // When using a AppCompat theme
    implementation("com.google.accompanist:accompanist-appcompat-theme:0.34.0")


    // Unit test libs
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.robolectric:robolectric:4.13")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("com.google.dagger:hilt-android-testing:2.52")
    kaptTest("com.google.dagger:hilt-android-compiler:2.52")

    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation ("androidx.test.ext:junit:1.2.1")
}
kapt {
    correctErrorTypes = true
}