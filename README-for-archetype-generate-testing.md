# Simple Springboot Camel Application Prototype

A springboot camel application that helps with the development of the springboot camel archetype.

# Development

## How to run the application

Use the script:
```
$ ./bin/run-app-localdev.sh
```
... or with spring-boot:run
```
$ SPRING_APPLICATION_JSON=$(cat config/localdev.json) mvn clean spring-boot:run
```

## How to verify the application

Local:
```
$ curl -v http://127.0.0.1:8080/actuator/health
Should respond with a HTTP 200 status and content: {"status":"UP"}

$ curl -v http://127.0.0.1:8080/api/ping
Should respond with a ping response.
```

# Source Code

[https://example.com/path/to/application/source](https://example.com/path/to/application/source)

# License

[TODO add license]

# Author

[Author name](http://example.com/author/url)
