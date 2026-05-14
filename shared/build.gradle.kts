import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.apollo)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            freeCompilerArgs += listOf("-Xbinary=bundleId=com.brindawan.mobile")
        }
    }

    android {
        namespace = "com.brindawan.mobile.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.security.crypto)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.multiplatform.settings)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.apollo.runtime)
//            implementation(libs.apollo.normalized.cache)
            implementation(libs.normalized.cache)

            implementation(libs.navigation.compose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.compose.material.icons.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.keycloak.client)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

apollo {
    service("service") {
        packageName.set("com.brindawan.mobile")
        generateKotlinModels.set(true)
        languageVersion.set("1.9")
        introspection {
            endpointUrl.set("https://api.brindawan.com/graphql")
            schemaFile.set(file("src/commonMain/graphql/graphql.schema.json"))
        }
        plugin("com.apollographql.cache:normalized-cache-apollo-compiler-plugin:1.0.3")
        pluginArgument("com.apollographql.cache.packageName", packageName.get())
    }
    generateSourcesDuringGradleSync.set(true)
}