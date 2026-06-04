<!-- BEGIN:apollo-kotlin-agent-rules -->
# This project uses Apollo Kotlin version 5.0.0.
# For more information, read the [documentation](https://www.apollographql.com/docs/kotlin/v5)

<!-- END:apollo-kotlin-agent-rules -->

# Libraries used in this project
- [Apollo Kotlin](https://www.apollographql.com/docs/kotlin/v5)
- [Kotlin] version 2.3.21 [Documentation](https://kotlinlang.org/docs/home.html)
- [Multiplatform] version 2.3.21 [Documentation](https://kotlinlang.org/docs/multiplatform/get-started.html)
- [kotlin-multiplatform-keycloak-client] version 1.0.2 [Documentation](https://github.com/klsoft-mobile/kotlin-multiplatform-keycloak-client)
- [Compose Multiplatform] version 1.11.0
- [Koin] version 4.2.1 (DI)
- [Ktor] version 3.4.3 (HTTP client)
- [Coil] version 3.5.0-beta01 (image loading, coil-network-ktor3 for multiplatform)
- [Apollo Normalized Cache] version 1.0.3 (`com.apollographql.cache:normalized-cache`)
- [multiplatform-settings] version 1.3.0 (key/value storage — iOS only in legacy path)
- [androidx.security.crypto] version 1.1.0 (EncryptedSharedPreferences — Android only)
- [Google Maps Compose / KTX] versions 4.1.1 / 2.2.0 (Android only)

---

# Project Architecture

## Module structure
```
:shared          → KMP library — all shared logic and UI (Compose Multiplatform)
:androidApp      → Android application shell; delegates to :shared
iosApp/          → Xcode project; entry via MainViewController() in shared/iosMain
```

## Source-set layout inside `:shared`
```
shared/src/
  commonMain/
    kotlin/com/brindawan/mobile/
      App.kt                     ← root @Composable; applies BrindawanTheme
      data/                      ← repositories, models, AuthSessionStore
      di/                        ← Koin modules + AppConfig
      screens/                   ← screen composables + ViewModels per feature
        auth/                    ← AccountScreen, AuthViewModel, AuthDeepLinkHandler
        detail/                  ← DetailScreen, DetailViewModel
        list/                    ← ListScreen, ListViewModel (museum items)
        locations/               ← LocationsScreen, LocationsViewModel
        products/                ← ProductsScreen, ProductsViewModel
      ui/theme/                  ← BrindawanTheme.kt
    graphql/
      graphql.schema.json        ← Apollo schema (update via Gradle introspection task)
      queries/
        ProductQuery.graphql
        DealersQuery.graphql
  androidMain/kotlin/…/
    data/SecureKeyValueStore.android.kt   ← EncryptedSharedPreferences impl
    data/LegacyAuthSessionStorage.android.kt
    screens/locations/GoogleMapContent.android.kt
    screens/locations/MapsNavigation.android.kt
  iosMain/kotlin/…/
    MainViewController.kt                  ← ComposeUIViewController { App() }
    data/SecureKeyValueStore.ios.kt        ← Keychain impl
    data/LegacyAuthSessionStorage.ios.kt
    screens/locations/GoogleMapContent.ios.kt
    screens/locations/MapsNavigation.ios.kt
```

## Dependency injection (Koin)
- `initKoin(AppConfig)` is the single entry point.  
- Android: called in `BrindawanApp.onCreate()` using `BuildConfig` fields.  
- iOS: call `doInitKoin(...)` overloads from Swift (see `di/Koin.kt`).  
- Modules: `dataModule` (repositories, HttpClient, ApolloClient), `viewModelModule` (all ViewModels as `factoryOf`).

## GraphQL (Apollo Kotlin 5 + Normalized Cache)
- `ApolloClient` is a Koin singleton built in `initApolloClient(config)`.
- Normalized cache uses `MemoryCacheFactory(10 MB)` wired via the `normalized-cache-apollo-compiler-plugin`.
- **Add a query**: create a `.graphql` file under `shared/src/commonMain/graphql/queries/`, then run `./gradlew generateApolloSources`.
- **Update schema**: run `./gradlew :shared:downloadApolloSchema` (introspects `https://api.brindawan.com/graphql`).
- Generated classes land in `com.brindawan.mobile` (package set in `shared/build.gradle.kts`).
- Repository pattern: inject `ApolloClient`, call `.query(XyzQuery()).execute()`, check `response.hasErrors()`.  
  Example: `ProductRepository`, `DealerRepository`.

## Authentication (Keycloak OIDC)
- Flow: authorization-code via in-app browser → deep-link callback → `KeycloakAuthRepository.loginWithAuthorizationCode(code)`.
- Redirect URI: `com.brindawan.mobile://auth/callback` (registered in `AndroidManifest.xml` as an intent filter via manifest placeholders).
- Session persisted in `AuthSessionStore` using platform-specific `SecureKeyValueStore` (`expect`/`actual`):  
  Android → `EncryptedSharedPreferences` (AES-256); iOS → Keychain.
- One-time migration from legacy plain storage is handled automatically in `AuthSessionStore.load()`.
- `KeycloakAuthRepository` also exposes `refreshSession(refreshToken)` and `logout(refreshToken)`.

## Navigation
- Single `Scaffold` with a floating rounded `NavigationBar` (`MainTabsScreen.kt`).
- Tabs: `Home`, `Products`, `Locations`, `Offers` (placeholder), `Account`.
- Tab state held in `rememberSaveable { mutableStateOf(BottomTab.Home) }` — survives configuration changes.
- Home tab manages its own nested state (`selectedObjectId`) for list→detail navigation.
- Do **not** use Jetpack Navigation graph for top-level tabs; keep tab switching in `MainTabsScreen`.

## Theme
- `BrindawanTheme(darkTheme, variant)` in `ui/theme/BrindawanTheme.kt`.
- Three variants: `Soft` (muted pastels, **currently active**), `Bold` (full-saturation brand), `WebsiteExact` (derived from the palette below).
- Active variant set in `App.kt`: `BrindawanThemeVariant.Soft`.
- Helper: `brindawanBottomNavContainerColor()` — luminance-aware nav bar background.
- When adding UI, use `MaterialTheme.colorScheme.*` tokens; avoid hardcoded colours.

---

# Build & Workflow

## Common Gradle tasks
```bash
./gradlew :shared:generateApolloSources     # re-generate Apollo Kotlin models
./gradlew :shared:downloadApolloSchema      # re-introspect GraphQL schema
./gradlew :androidApp:assembleDebug         # build Android debug APK
./gradlew :androidApp:assembleRelease       # build signed release APK (needs local.properties)
./gradlew build                             # build everything
```

## Android runtime configuration
Config values are injected as `BuildConfig` fields.  
Priority order: `gradle.properties` > environment variable > hardcoded default.

| Property key | Default |
|---|---|
| `GRAPHQL_SERVER_URL` | `https://api.brindawan.com/graphql` |
| `KEYCLOAK_REALM_URL` | `https://auth.brindawan.com/realms/brindawan` |
| `KEYCLOAK_CLIENT_ID` | `brindawan_mobile` |
| `KEYCLOAK_REDIRECT_URI` | `com.brindawan.mobile://auth/callback` |
| `KEYCLOAK_SCOPE` | `openid profile email` |
| `KEYCLOAK_CLIENT_SECRET` | *(see androidApp/build.gradle.kts)* |

## Release signing (`local.properties`)
```properties
RELEASE_STORE_FILE=upload-keystore.jks
RELEASE_STORE_PASSWORD=…
RELEASE_KEY_ALIAS=…
RELEASE_KEY_PASSWORD=…
```

## iOS build
Open `iosApp/iosApp.xcodeproj` in Xcode. The shared framework (`Shared.framework`) is built by the Xcode run script phase via Gradle. Call `doInitKoin(...)` from Swift before presenting `MainViewController()`.

## Platform-specific expect/actual pattern
When a feature needs platform code, declare `expect` in `commonMain` and provide `actual` implementations in `androidMain` and `iosMain`.  
Existing examples: `SecureKeyValueStore`, `createSecureKeyValueStore()`, `readLegacySessionRaw()`, `clearLegacySessionRaw()`, `GoogleMapContent`, `MapsNavigation`.

## Android SDK targets
- `compileSdk` 37 · `minSdk` 24 · `targetSdk` 36
- JVM target: 11 (both `:shared` android and `:androidApp`)

---

<!-- BEGIN: template-for-this-project -->

# Template the project is based on

/* CSS HEX */
--tea-green: #ccd5aeff;
--beige: #e9edc9ff;
--cornsilk: #fefae0ff;
--papaya-whip: #faedcdff;
--light-bronze: #d4a373ff;

/* CSS HSL */
--tea-green: hsla(74, 32%, 76%, 1);
--beige: hsla(67, 50%, 86%, 1);
--cornsilk: hsla(52, 94%, 94%, 1);
--papaya-whip: hsla(43, 82%, 89%, 1);
--light-bronze: hsla(30, 53%, 64%, 1);

/* SCSS HEX */
$tea-green: #ccd5aeff;
$beige: #e9edc9ff;
$cornsilk: #fefae0ff;
$papaya-whip: #faedcdff;
$light-bronze: #d4a373ff;

/* SCSS HSL */
$tea-green: hsla(74, 32%, 76%, 1);
$beige: hsla(67, 50%, 86%, 1);
$cornsilk: hsla(52, 94%, 94%, 1);
$papaya-whip: hsla(43, 82%, 89%, 1);
$light-bronze: hsla(30, 53%, 64%, 1);

/* SCSS RGB */
$tea-green: rgba(204, 213, 174, 1);
$beige: rgba(233, 237, 201, 1);
$cornsilk: rgba(254, 250, 224, 1);
$papaya-whip: rgba(250, 237, 205, 1);
$light-bronze: rgba(212, 163, 115, 1);

/* SCSS Gradient */
$gradient-top: linear-gradient(0deg, #ccd5aeff, #e9edc9ff, #fefae0ff, #faedcdff, #d4a373ff);
$gradient-right: linear-gradient(90deg, #ccd5aeff, #e9edc9ff, #fefae0ff, #faedcdff, #d4a373ff);
$gradient-bottom: linear-gradient(180deg, #ccd5aeff, #e9edc9ff, #fefae0ff, #faedcdff, #d4a373ff);
$gradient-left: linear-gradient(270deg, #ccd5aeff, #e9edc9ff, #fefae0ff, #faedcdff, #d4a373ff);
$gradient-top-right: linear-gradient(45deg, #ccd5aeff, #e9edc9ff, #fefae0ff, #faedcdff, #d4a373ff);
$gradient-bottom-right: linear-gradient(135deg, #ccd5aeff, #e9edc9ff, #fefae0ff, #faedcdff, #d4a373ff);
$gradient-top-left: linear-gradient(225deg, #ccd5aeff, #e9edc9ff, #fefae0ff, #faedcdff, #d4a373ff);
$gradient-bottom-left: linear-gradient(315deg, #ccd5aeff, #e9edc9ff, #fefae0ff, #faedcdff, #d4a373ff);
$gradient-radial: radial-gradient(#ccd5aeff, #e9edc9ff, #fefae0ff, #faedcdff, #d4a373ff);

<!-- END: template-for-this-project -->
