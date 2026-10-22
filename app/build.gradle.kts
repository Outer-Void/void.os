plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val gdxVersion = "1.14.2"
val gdxNatives = configurations.create("gdxNatives") {
    isCanBeConsumed = false
    isCanBeResolved = true
}
val extractGdxNatives by tasks.registering {
    val output = layout.buildDirectory.dir("generated/gdxNatives")
    inputs.files(gdxNatives)
    outputs.dir(output)
    doLast {
        project.sync {
            into(output)
            gdxNatives.resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
                val abi = requireNotNull(artifact.classifier).removePrefix("natives-")
                from(zipTree(artifact.file)) { include("*.so"); into(abi) }
            }
        }
    }
}

tasks.configureEach {
    if (name.startsWith("merge") && name.endsWith("JniLibFolders")) dependsOn(extractGdxNatives)
}

android {
    namespace = "com.outervoid.axiom"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.outervoid.axiom"
        minSdk = 26
        targetSdk = 36
        versionCode = 20
        versionName = "2.0.0-dev"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    sourceSets.getByName("main").jniLibs.srcDir(layout.buildDirectory.dir("generated/gdxNatives"))

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    testImplementation("junit:junit:4.13.2")
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    add(gdxNatives.name, "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    add(gdxNatives.name, "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    add(gdxNatives.name, "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    add(gdxNatives.name, "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
