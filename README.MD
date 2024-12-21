## SSO login with reactive Spring & Kotlin coroutines

- Reactive Spring Boot.
- H2 + R2DBC.
- Kotlin coroutines with WebFlux.
- Google auth/login with Spring Security.
- GitHub auth/login.
- Facebook auth/login (not verified, hard app registration process, business account required).

### How to setup Google auth:
- doc: https://developers.google.com/identity/openid-connect/openid-connect
- api keys: https://console.cloud.google.com/apis/credentials

### GitHub setup:
https://github.com/settings/developers [add OAuth Apps]
<br> setup homepage & callback URL:

- http://localhost:8080
- http://localhost:8080/login/oauth2/code/github
