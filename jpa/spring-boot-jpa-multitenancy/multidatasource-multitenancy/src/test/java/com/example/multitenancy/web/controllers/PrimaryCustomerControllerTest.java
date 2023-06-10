package com.example.multitenancy.web.controllers;

import static com.example.multitenancy.utils.AppConstants.PROFILE_TEST;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.multitenancy.config.multitenant.TenantIdentifierResolver;
import com.example.multitenancy.primary.controllers.PrimaryCustomerController;
import com.example.multitenancy.primary.entities.PrimaryCustomer;
import com.example.multitenancy.primary.services.PrimaryCustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PrimaryCustomerController.class)
@ActiveProfiles(PROFILE_TEST)
class PrimaryPrimaryCustomerControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private PrimaryCustomerService primaryCustomerService;
    @MockBean private TenantIdentifierResolver tenantIdentifierResolver;

    private List<PrimaryCustomer> primaryCustomerList;

    @BeforeEach
    void setUp() {
        this.primaryCustomerList = new ArrayList<>();
        this.primaryCustomerList.add(new PrimaryCustomer(1L, "text 1", 0L, "dbsystc"));
        this.primaryCustomerList.add(new PrimaryCustomer(2L, "text 2", 0L, "dbsystc"));
        this.primaryCustomerList.add(new PrimaryCustomer(3L, "text 3", 0L, "dbsystc"));
    }

    @Test
    void shouldFetchAllCustomers() throws Exception {
        given(primaryCustomerService.findAllCustomers()).willReturn(this.primaryCustomerList);

        this.mockMvc
                .perform(get("/api/customers/primary").header("X-tenantId", "primary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(primaryCustomerList.size())));
    }

    @Test
    void shouldFindCustomerById() throws Exception {
        Long customerId = 1L;
        PrimaryCustomer primaryCustomer = new PrimaryCustomer(customerId, "text 1", 0L, "dbsystc");
        given(primaryCustomerService.findCustomerById(customerId))
                .willReturn(Optional.of(primaryCustomer));

        this.mockMvc
                .perform(
                        get("/api/customers/primary/{id}", customerId)
                                .header("X-tenantId", "primary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(primaryCustomer.getText())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingCustomer() throws Exception {
        Long customerId = 1L;
        given(primaryCustomerService.findCustomerById(customerId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(
                        get("/api/customers/primary/{id}", customerId)
                                .header("X-tenantId", "primary"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewCustomer() throws Exception {
        given(primaryCustomerService.saveCustomer(any(PrimaryCustomer.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        PrimaryCustomer primaryCustomer = new PrimaryCustomer(1L, "some text", 0L, "dbsystc");
        this.mockMvc
                .perform(
                        post("/api/customers/primary")
                                .header("X-tenantId", "primary")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(primaryCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.text", is(primaryCustomer.getText())));
    }

    @Test
    void shouldReturn400WhenCreateNewCustomerWithoutText() throws Exception {
        PrimaryCustomer primaryCustomer = new PrimaryCustomer(null, null, 0L, "dbsystc");

        this.mockMvc
                .perform(
                        post("/api/customers/primary")
                                .header("X-tenantId", "primary")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(primaryCustomer)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andExpect(jsonPath("$.instance", is("/api/customers/primary")))
                .andReturn();
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        Long customerId = 1L;
        PrimaryCustomer primaryCustomer =
                new PrimaryCustomer(customerId, "Updated text", 0L, "dbsystc");
        given(primaryCustomerService.findCustomerById(customerId))
                .willReturn(Optional.of(primaryCustomer));
        given(primaryCustomerService.saveCustomer(any(PrimaryCustomer.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc
                .perform(
                        put("/api/customers/primary/{id}", primaryCustomer.getId())
                                .header("X-tenantId", "primary")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(primaryCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(primaryCustomer.getText())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingCustomer() throws Exception {
        Long customerId = 1L;
        given(primaryCustomerService.findCustomerById(customerId)).willReturn(Optional.empty());
        PrimaryCustomer primaryCustomer =
                new PrimaryCustomer(customerId, "Updated text", 0L, "dbsystc");

        this.mockMvc
                .perform(
                        put("/api/customers/primary/{id}", customerId)
                                .header("X-tenantId", "primary")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(primaryCustomer)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        Long customerId = 1L;
        PrimaryCustomer primaryCustomer =
                new PrimaryCustomer(customerId, "Some text", 0L, "dbsystc");
        given(primaryCustomerService.findCustomerById(customerId))
                .willReturn(Optional.of(primaryCustomer));
        doNothing().when(primaryCustomerService).deleteCustomerById(primaryCustomer.getId());

        this.mockMvc
                .perform(
                        delete("/api/customers/primary/{id}", primaryCustomer.getId())
                                .header("X-tenantId", "primary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(primaryCustomer.getText())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingCustomer() throws Exception {
        Long customerId = 1L;
        given(primaryCustomerService.findCustomerById(customerId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(
                        delete("/api/customers/primary/{id}", customerId)
                                .header("X-tenantId", "primary"))
                .andExpect(status().isNotFound());
    }
}
