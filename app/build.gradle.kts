plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.stomas.conectamobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.stomas.conectamobile"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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

    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.22")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.22")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/io.netty.versions.properties",
                "META-INF/INDEX.LIST",
                "META-INF/*.kotlin_module"
            )
        }
    }
}

dependencies {
        implementation("androidx.recyclerview:recyclerview:1.3.1")
        implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
        implementation("com.hivemq:hivemq-mqtt-client:1.3.0")
        implementation("androidx.navigation:navigation-fragment-ktx:2.7.1")
        implementation("androidx.navigation:navigation-ui-ktx:2.7.1")
        implementation("com.google.firebase:firebase-firestore:24.7.0")
        implementation("com.google.firebase:firebase-auth:22.1.0")
        implementation("com.google.firebase:firebase-database:20.2.2")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.9.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
