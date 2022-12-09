# spring-boot-strategy-plugin

The strategy design pattern is a behavioral design pattern that allows an object to change its behavior based on different strategies or algorithms. This pattern separates the actual behavior or algorithm from the object that uses it, so that the same object can use different strategies depending on the situation. This allows for more flexibility and reuse of code, as the object can easily switch between strategies without having to change its internal implementation.

### Run tests
`$ ./gradlew clean build`

### Run locally
```
$ docker-compose -f docker-compose.yml up -d
$ ./gradlew bootRun -Plocal
```

### Useful Links
* Swagger UI: http://localhost:8080/swagger-ui.html
* Actuator Endpoint: http://localhost:8080/actuator
* Prometheus: http://localhost:9090/
* Grafana: http://localhost:3000/ (user/password)
