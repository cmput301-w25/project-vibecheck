plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    //id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.vibecheck"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vibecheck"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(libs.play.services.maps)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.14")
    testImplementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    testImplementation("com.google.firebase:firebase-auth")
    testImplementation("androidx.test:core:1.5.0")

    //testImplementation(libs.junit)
    //testImplementation ("org.mockito:mockito-core:5.11.0")
    //testImplementation ("org.mockito:mockito-inline:5.2.0")
    //testImplementation ("androidx.test:core:1.5.0")
    //testImplementation("org.robolectric:robolectric:4.10.3")
    //testImplementation("junit:junit:4.13.2")
    //testImplementation("org.robolectric:robolectric:4.14")

    // Google Places SDK for Android (Autocomplete support)
    implementation("com.google.android.libraries.places:places:3.3.0")

    // Google Play Services Location (FusedLocationProvider)
    implementation ("com.google.android.gms:play-services-location:21.0.1")
}


secrets {
    propertiesFileName = "secrets.properties"
    
    defaultPropertiesFileName = "local.defaults.properties"
}
