# Simple Springboot Camel Application Prototype

A springboot camel application that helps with the development of the springboot camel archetype.
[TODO expand this description]

## Running the application

Use the script:
```
$ ./bin/run-app-localdev.sh
```
... or with spring-boot:run
```
$ mvn clean spring-boot:run -Dspring.main.banner-mode=off -Dlogging.level.root=INFO
```
... or package and run:
```
$ mvn clean package && java -Dspring.main.banner-mode=off -Dlogging.level.root=INFO -jar target/simple-springboot-prototype.jar
```

## Verify

Local:
```
$ curl -v http://127.0.0.1:8080/actuator/health
Should respond with a HTTP 200 status and content: {"status":"UP"}

$ curl -v http://127.0.0.1:8080/api/ping
Should respond with a ping response.
```

## Source Code

[https://github.com/adrianjuhl/adrianjuhl-maven-simple-springboot-camel-application-prototype](https://github.com/adrianjuhl/adrianjuhl-maven-simple-springboot-camel-application-prototype)
