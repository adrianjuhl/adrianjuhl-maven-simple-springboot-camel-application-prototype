# simple-springboot-camel-application-prototype

This springboot camel application is the prototype for simple-springboot-camel-archetype.

It is a starting point of a new springboot camel integration piece.

The simple-springboot-camel-archetype will generate projects that match the structure and content of this prototype. As the requirements for the structure and content that is needed in a newly generated springboot camel integration piece changes, so will the structure and content of this prototype.

## Running the application

Use the script:
```
$ ./bin/run-app-localdev.sh
```
... or with spring-boot:run
```
$ SPRING_APPLICATION_JSON=$(cat config/localdev.json) mvn -Penable-camel-developer-console clean spring-boot:run
```

## Verify

Local:
```
$ curl -v http://127.0.0.1:8080/api/ping
Should respond with a ping response.

$ curl -v http://127.0.0.1:8080/api/appVersionInfo | jq
Should respond with application and version information.

$ curl -v http://127.0.0.1:8080/actuator | jq
Should respond with information about actuator endpoints.

$ curl -v http://127.0.0.1:8080/actuator/health
Should respond with a HTTP 200 status and content: {"status":"UP"}

$ curl -v http://127.0.0.1:8080/actuator/camel | jq
Should respond with information about camel actuator endpoints.

$ curl -v http://127.0.0.1:8080/actuator/camel/route | jq
Should respond with related information from camel route actuator endpoint.
```

## Source Code

[https://github.com/adrianjuhl/adrianjuhl-maven-simple-springboot-camel-application-prototype](https://github.com/adrianjuhl/adrianjuhl-maven-simple-springboot-camel-application-prototype)

## License

MIT

## Author Information

[Adrian Juhl](http://github.com/adrianjuhl)
