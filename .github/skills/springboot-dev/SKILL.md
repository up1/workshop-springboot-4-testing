---
name: springboot-dev
description: Developing and testing REST API with Spring Boot 4 and test containers
---

## Workflow
1. Read and analyze the API specification and requirements from user's documentation
2. Plan the implementation of the API endpoints based on the analyzed requirements
3. Implement the API endpoints in the Spring Boot application with best practices and coding standards
4. Write and execute tests following from user's test cases using JUnit, SpringBootTest, TestRestTemplate, and TestContainers
5. Run tests to ensure the API endpoints function correctly and meet the specified requirements
  5.1 If test fails, debug the issues and fix until the tests pass successfully

## Technical Stack
* Spring Boot 4
* PostgreSQL
* JUnit 6
* SpringBootTest
* TestRestTemplate
* TestContainers with PostgreSQL and HTTP server

## Project structure with features-base

Example with feature: demo
```
src
└── main
    └── java
        └── com
            └── api
                └── demo
                    ├── controller
                        └── DemoController.java
                        └── DemoControllerAdvice.java
                    ├── dto
                        └── DemoRequest.java
                        └── DemoResponse.java
                    ├── repository
                        └── DemoRepository.java
                    ├── service
                        └── DemoService.java
                    ├── exception
                        └── DemoException.java
                    └── model
                        └── Demo.java
└── test
    └── java
        └── com
            └── api
                └── demo
                    ├── controller
                        └── DemoControllerSuccessIntegrationTest.java
                        └── DemoControllerFailureIntegrationTest.java
                    ├── service
                        └── DemoServiceSuccessUnitTest.java
                        └── DemoServiceFailureUnitTest.java
```

## Best Practices with modern java development
1. Use the latest stable version of Java to take advantage of new language features and performance improvements.
2. Follow the standard Java naming conventions for classes, methods, and variables.
3. Write clean, readable, and maintainable code by adhering to SOLID principles and design patterns.
4. Use dependency injection to manage dependencies and promote testability.
5. Leverage Java's built-in features such as streams, optionals, and the new date and time API for more concise and expressive code.
6. Use java records for immutable data carriers to simplify code and improve readability.
7. Utilize modern Java features such as pattern matching, sealed classes, and text blocks to write more expressive and concise code.
8. Take advantage of the new switch expressions for more readable and maintainable conditional logic.
9. Use text blocks for multi-line strings to improve readability and reduce boilerplate code.

## Best Practices for Spring Boot and Spring Boot testing
1. Follow the standard project structure for Spring Boot applications, separating controllers, services, repositories, and models.
2. Write unit tests for individual components and integration tests for the overall application behavior.
3. Use TestContainers to create isolated and reproducible test environments for PostgreSQL and HTTP server.
4. Ensure that tests are independent and can be executed in any order without affecting each other.
5. Use meaningful test case names and provide clear assertions to verify the expected behavior.
6. Regularly run tests during development to catch issues early and maintain code quality.