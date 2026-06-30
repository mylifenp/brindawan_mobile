import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleGmsGoogleServices)
}

dependencies {
    implementation(libs.firebase.messaging)
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.compose.foundation)
    implementation(libs.ktor.client.okhttp)
}

val graphqlServerUrl = providers.gradleProperty("GRAPHQL_SERVER_URL")
    .orElse(providers.environmentVariable("GRAPHQL_SERVER_URL"))
    .orElse("https://api.brindawan.com/graphql")
val keycloakRealmUrl = providers.gradleProperty("KEYCLOAK_REALM_URL")
    .orElse(providers.environmentVariable("KEYCLOAK_REALM_URL"))
    .orElse("https://auth.brindawan.com/realms/brindawan")
val keycloakClientId = providers.gradleProperty("KEYCLOAK_CLIENT_ID")
    .orElse(providers.environmentVariable("KEYCLOAK_CLIENT_ID"))
    .orElse("brindawan_mobile")
val keycloakRedirectUri = providers.gradleProperty("KEYCLOAK_REDIRECT_URI")
    .orElse(providers.environmentVariable("KEYCLOAK_REDIRECT_URI"))
    .orElse("com.brindawan.mobile://auth/callback")
val keycloakScope = providers.gradleProperty("KEYCLOAK_SCOPE")
    .orElse(providers.environmentVariable("KEYCLOAK_SCOPE"))
    .orElse("openid profile email")
val keycloakClientSecret = providers.gradleProperty("KEYCLOAK_CLIENT_SECRET")
    .orElse(providers.environmentVariable("KEYCLOAK_CLIENT_SECRET"))
    .orElse("qbbFYlSA0Ol0A66vJcvp7PH33NWTNNsE")

val parsedRedirectUri = URI(keycloakRedirectUri.get())
val redirectScheme = parsedRedirectUri.scheme ?: "com.brindawan.mobile"
val redirectHost = parsedRedirectUri.host ?: "auth"
val redirectPath = parsedRedirectUri.path ?: "/callback"

android {
    namespace = "com.brindawan.mobile"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.brindawan.mobile"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        val gitCommitCount = providers.exec {
            commandLine("git", "rev-list", "--count", "HEAD")
        }.standardOutput.asText.map { it.trim().toInt() }.getOrElse(1)

        versionCode = gitCommitCount
        versionName = "1.0.$gitCommitCount"

        buildConfigField("String", "GRAPHQL_SERVER_URL", "\"${graphqlServerUrl.get()}\"")
        buildConfigField("String", "KEYCLOAK_REALM_URL", "\"${keycloakRealmUrl.get()}\"")
        buildConfigField("String", "KEYCLOAK_CLIENT_ID", "\"${keycloakClientId.get()}\"")
        buildConfigField("String", "KEYCLOAK_REDIRECT_URI", "\"${keycloakRedirectUri.get()}\"")
        buildConfigField("String", "KEYCLOAK_SCOPE", "\"${keycloakScope.get()}\"")
        buildConfigField("String", "KEYCLOAK_CLIENT_SECRET", "\"${keycloakClientSecret.get()}\"")

        manifestPlaceholders["keycloakRedirectScheme"] = redirectScheme
        manifestPlaceholders["keycloakRedirectHost"] = redirectHost
        manifestPlaceholders["keycloakRedirectPath"] = redirectPath
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    signingConfigs {
        create("release") {
            val props = rootProject.file("local.properties").inputStream().use { stream ->
                Properties().apply { load(stream) }
            }
            storeFile = rootProject.file(props.getProperty("RELEASE_STORE_FILE"))
            storePassword = props.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = props.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = props.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

