### GraphQL endpoint configuration

The app reads the GraphQL base URL from `GRAPHQL_SERVER_URL` and injects it into Apollo/Koin at startup.

- Android: `GRAPHQL_SERVER_URL` is resolved from Gradle property first, then environment variable, then default URL.
- iOS: `GRAPHQL_SERVER_URL` is read from process environment at app startup, then falls back to default URL.

Examples:

```zsh
# Pass as a Gradle property (Android build)
./gradlew :androidApp:assembleDebug -PGRAPHQL_SERVER_URL=https://api.example.com/graphql

# Pass as an environment variable (Android build)
GRAPHQL_SERVER_URL=https://api.example.com/graphql ./gradlew :androidApp:assembleDebug

# Pass as an environment variable (iOS app run from Xcode scheme/env)
GRAPHQL_SERVER_URL=https://api.example.com/graphql
```

