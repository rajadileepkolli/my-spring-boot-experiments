package com.example.mongoes.common;

import static com.example.mongoes.utils.AppConstants.PROFILE_TEST;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.lifecycle.Startables;

@ActiveProfiles({PROFILE_TEST})
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest {

    protected static final MongoDBContainer MONGO_DB_CONTAINER =
            new MongoDBContainer("mongo:5.0.13")
                    .withExposedPorts(27017, 27018, 27019)
                    .withReuse(true);

    protected static final ElasticsearchContainer ELASTICSEARCH_CONTAINER =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.17.6")
                    .withEnv("discovery.type", "single-node")
                    .withReuse(true);

    static {
        Startables.deepStart(MONGO_DB_CONTAINER, ELASTICSEARCH_CONTAINER).join();
    }

    @DynamicPropertySource
    static void setApplicationProperties(DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
        propertyRegistry.add(
                "spring.elasticsearch.uris", ELASTICSEARCH_CONTAINER::getHttpHostAddress);
    }

    @Autowired protected WebTestClient webTestClient;
    @Autowired protected ObjectMapper objectMapper;
}
