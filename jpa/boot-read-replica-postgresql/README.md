# Boot Read-Replica with PostgreSQL
This project is an example to show how we can separate read and write operations to primary and replica databases using spring boot and postgresql database.

A read replica in Postgres is a database instance that receives data from a primary database instance and serves it to clients. Read replicas are useful for scaling database workloads, as they can offload read operations from the primary instance, allowing it to focus on more resource-intensive tasks such as writing data. This can improve the performance of the overall database system. Read replicas can also be useful for providing high availability, as they can take over read operations if the primary instance becomes unavailable for any reason.

 - All reads will go to reader instance and writes will go to writer instance
 - Switching between master and replica can be observed in docker compose logs or application logs when datasourceproxy is enabled.

 ![](../../images/replica.png)

 Architecture Image Credit : [Vlad](https://twitter.com/vlad_mihalcea)

---

## Key Concepts

1. **Primary-Replica Setup**: Writes go to the primary database, while reads occur on the replica.
2. **Performance Optimization**: Minimizes load on the primary, improving overall throughput.
3. **LazyConnectionDataSourceProxy**: Ensures a single `DataSource` bean but dynamically determines read vs. write at runtime.

---

## Liquibase
Use below script for generating the difference in the database

```shell
./diff.sh
```

### Run tests
```shell
./mvnw clean verify
```

### Run locally
```shell
docker-compose -f docker/docker-compose.yml up -d
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Useful Links
* Swagger UI: http://localhost:8080/swagger-ui.html
* Actuator Endpoint: http://localhost:8080/actuator
* PgAdmin : http://localhost:5050(pgadmin4@pgadmin.org/admin)

### Reference
 - https://stackoverflow.com/questions/25911359/how-to-split-read-only-and-read-write-transactions-with-jpa-and-hibernate/26026237#26026237
