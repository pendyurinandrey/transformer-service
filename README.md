# How to run in Docker
* Install Java 21 (the solution was tested on Amazon Corretto)
* Build `./mvnw package k8s:build`
* Run `docker run -p 8080:8080 transformer-service:0.0.1-SNAPSHOT`

# How to test

Run unit tests only `./mvnw verify`

Run unit tests + integration tests `./mvnw verify -Pintegration-test`

The service exposes Swagger UI by the following path `http://localhost:8080/swagger-ui.html`. It can be useful for manual testing.

# Design notes

* I’ll use Java 21, Spring Boot 3, Spring 6, and Maven.
* I’ll implement the REST endpoint with JSON serialization.
* The endpoint will accept a JSON array of JSON objects (elements). The array can contain from 1 to 20 elements. While the task does not specify an exact maximum number of elements per request, it’s generally better to avoid allowing infinite arrays.
* Each element must contain from 1 to 20 transformers.
* Each transformer can have from 0 to 20 properties.
* Each property is a pair: \<string key\> -> \<string value\>. Any other JSON types will not be supported for properties.
* Each transformer class must be annotated with a custom annotation and implement a custom interface. This may not be ideal from a contributor's perspective, but this design will provide compile-time checks, helping the contributor identify that the transformer’s signature is valid.
* For Greek letter translation, I’ll use a third-party library where ISO 843 (without accents) has already been implemented.
* Russian letters are converted according to ICAO DOC 9303.
* The endpoint response will contain the original string and the final transformed value. No intermediate values will be exposed.
* The service will be wrapped in Docker. The Docker image will not be published to any public registry.