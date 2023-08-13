package com.example.bootr2dbc;

import com.example.bootr2dbc.common.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=validate"})
class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Test
    void contextLoads() {}
}
