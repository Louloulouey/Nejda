import java.net.NetworkInterface
import java.util.Properties


fun getLocalIp(): String {
    return NetworkInterface.getNetworkInterfaces()
        .asSequence()
        .toList()
        .flatMap { it.inetAddresses.toList() }
        .filter { it.isSiteLocalAddress && !it.isLoopbackAddress && it.hostAddress.startsWith("192.168") }
        .map { it.hostAddress }
        .firstOrNull() ?: "localhost"
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.gms)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.sdidsa.nejda"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.sdidsa.nejda"
        minSdk = 28
        targetSdk = 36
        versionCode = 11
        versionName = "0.11"

        val properties = project.rootProject.file("gradle.properties").inputStream().use { stream ->
            Properties().apply { load(stream) }
        }

        properties.forEach { key, value ->
            if (key.toString().contains("API_")) {
                buildConfigField("String", key.toString(), "\"$value\"")
            }
        }

        val localIp = getLocalIp()
        buildConfigField("String", "LOCAL_IP", "\"$localIp\"")
    }

    signingConfigs {
        create("cert") {
            keyAlias = project.property("keyAlias") as String
            keyPassword = project.property("keyPassword") as String
            storeFile = file(project.property("storeFile") as String)
            storePassword = project.property("storePassword") as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("cert")
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("cert")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.overscroll.decor.android)
    implementation(platform(libs.kotlin.bom))
    implementation(libs.zxing)
    implementation(libs.firebase.messaging)
    implementation(libs.md.core)
    implementation(libs.filament.android)
    implementation(libs.gltfio.android)
    implementation(libs.filament.utils.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.filamat.android)
}