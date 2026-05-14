import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        let graphqlUrl = ProcessInfo.processInfo.environment["GRAPHQL_SERVER_URL"] ?? "https://api.brindawan.com/graphql"
        let keycloakRealmUrl = ProcessInfo.processInfo.environment["KEYCLOAK_REALM_URL"] ?? "https://auth.brindawan.com/realms/brindawan"
        let keycloakClientId = ProcessInfo.processInfo.environment["KEYCLOAK_CLIENT_ID"] ?? "brindawan_mobile"
        let keycloakRedirectUri = ProcessInfo.processInfo.environment["KEYCLOAK_REDIRECT_URI"] ?? "com.brindawan.mobile://auth/callback"
        let keycloakScope = ProcessInfo.processInfo.environment["KEYCLOAK_SCOPE"] ?? "openid profile email"
        let keycloakClientSecret = ProcessInfo.processInfo.environment["KEYCLOAK_CLIENT_SECRET"] ?? "qbbFYlSA0Ol0A66vJcvp7PH33NWTNNsE"

        KoinKt.doInitKoin(
            graphqlUrl: graphqlUrl,
            keycloakRealmUrl: keycloakRealmUrl,
            keycloakClientId: keycloakClientId,
            keycloakRedirectUri: keycloakRedirectUri,
            keycloakScope: keycloakScope,
            keycloakClientSecret: keycloakClientSecret
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    _ = AuthDeepLinkHandlerKt.handleAuthRedirect(url: url.absoluteString)
                }
        }
    }
}
