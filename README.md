Micronaut Order
====
[![Java CI with Gradle (Groovy)](https://github.com/daniel-samson/micronaut-order/actions/workflows/gradle.yml/badge.svg)](https://github.com/daniel-samson/micronaut-order/actions/workflows/gradle.yml)

A micro service to manage orders, written with the Micronaut Framework.


## Requirements
- Java 22 Runtime / SDK
- Gradle
- Docker Compose
- Liquibase

### Getting Started
Before running the applications, you will need to start up the databases via docker compose:
```bash
docker compose up -d
```

To run the tests, run the gradle command:
```bash
gradle test
```

To start the application, run the gradle command
```bash
gradle run
```


## Micronaut 4.5.1 Documentation

- [User Guide](https://docs.micronaut.io/4.5.1/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.5.1/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.5.1/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)

---

- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
- [GraalVM Gradle Plugin documentation](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)

## Feature jdbc-hikari documentation

- [Micronaut Hikari JDBC Connection Pool documentation](https://micronaut-projects.github.io/micronaut-sql/latest/guide/index.html#jdbc)

## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)

## Feature validation documentation

- [Micronaut Validation documentation](https://micronaut-projects.github.io/micronaut-validation/latest/guide/)

## Feature micronaut-aot documentation

- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)

## Feature test-resources documentation

- [Micronaut Test Resources documentation](https://micronaut-projects.github.io/micronaut-test-resources/latest/guide/)

## Feature github-workflow-ci documentation

- [https://docs.github.com/en/actions](https://docs.github.com/en/actions)

## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#nettyHttpClient)


