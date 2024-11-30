# java-libraries

This is common Java code (divided into separate libraries) that I want to reuse from project to project.

There are:
* [exception-handling](https://github.com/iakunin/java-libraries/tree/main/src/exception-handling) -- common classes for unifying the format of working with Exceptions.
* [feign-tracing](https://github.com/iakunin/java-libraries/tree/main/src/feign-tracing) -- improved tracing and logging for the feign http-client. Efficient logging of all HTTP interactions helps to reduce the time it takes to resolve incidents.
* [kafka-key-value-storage](https://github.com/iakunin/java-libraries/tree/main/src/kafka/kafka-key-value-storage) -- with that library you can use Apache Kafka as a key-value storage.
* [logging](https://github.com/iakunin/java-libraries/tree/main/src/logging) -- a general approach to logging HTTP requests and responses.
* [persistence](https://github.com/iakunin/java-libraries/tree/main/src/persistence) -- unification of work with the database layer.
* [tests](https://github.com/iakunin/java-libraries/tree/main/src/tests) -- unified approach to application integration testing.

## Using a library in your project

Each of the library is published into [GitHub Packages registry](https://github.com/iakunin?tab=packages&repo_name=java-libraries) for simplifying installation using both Maven and Gradle.

- [Installing a library using Maven](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#installing-a-package).
- [Installing a library using Gradle](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package).


## Setting up a project locally
1. Execute `cp gradle.properties.dist gradle.properties`
1. Run `./gradlew clean build` to build the project
