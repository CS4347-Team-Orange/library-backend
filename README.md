# library-backend

This is a Spring 2.5.2 REST API, built with Java 11 and Gradle, using [Spring Initializr](https://start.spring.io/).  The project uses Spring's PostgreSQL Driver and Spring REST Framework.

It serves as the business tier of the library application.

## Environment Setup

### Install SDKMan

[SDKMan](https://sdkman.io/install) can manage and install multiple java versions for you.

```
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk version
```

This Should install sdkman, start it up, and report the sdkman verion number.

### Install Java 8

This installs Java 11.0.11, HotSpot VM, from AdoptOpenJDK.

```
sdk install java 11.0.11.hs-adpt
```

### Gradle, build & run the project

From the project's root dir, `library-backend`, run `./gradlew bootRun`.  This will start the development server on http://localhost:8080

Once the development server is running, you can use `curl` to test it.  Examples are below.

## Building a controller





