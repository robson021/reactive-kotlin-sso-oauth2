## SSO login with reactive Spring & Kotlin coroutines

- Reactive Spring Boot.
- H2 + R2DBC.
- Kotlin coroutines with WebFlux.
- Google auth/login with Spring Security.
- Facebook auth/login (not verified, hard registration process).

### How to setup Google auth:
https://developers.google.com/identity/openid-connect/openid-connect

### GitHub setup:

https://github.com/settings/developers [add OAuth Apps]
<br> setup homepage & callback URL:

- http://localhost:8080
- http://localhost:8080/login/oauth2/code/github
