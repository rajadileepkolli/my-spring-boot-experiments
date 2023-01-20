package com.example.hibernatecache;

import static io.hypersistence.utils.jdbc.validator.SQLStatementCountValidator.assertInsertCount;
import static io.hypersistence.utils.jdbc.validator.SQLStatementCountValidator.assertSelectCount;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.hibernatecache.common.AbstractIntegrationTest;
import com.example.hibernatecache.entities.Customer;
import io.hypersistence.utils.jdbc.validator.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Test
    void contextLoads() throws Exception {

        Customer request =
                new Customer(
                        null,
                        "firstNameTest",
                        "lastName test",
                        "emailtest@junit.com",
                        "9876543211");
        SQLStatementCountValidator.reset();
        this.mockMvc
                .perform(
                        post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(request.getFirstName())));
        assertInsertCount(1);
        // For selecting next sequence value
        assertSelectCount(1);
        SQLStatementCountValidator.reset();
        for (int i = 0; i < 10; i++) {
            this.mockMvc
                    .perform(
                            get("/api/customers/search?firstName=firstNameTest")
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
        assertSelectCount(1);
    }
}
