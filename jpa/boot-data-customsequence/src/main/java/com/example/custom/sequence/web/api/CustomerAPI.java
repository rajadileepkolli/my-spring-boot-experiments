package com.example.custom.sequence.web.api;

import com.example.custom.sequence.entities.Customer;
import com.example.custom.sequence.model.response.CustomerResponse;
import com.example.custom.sequence.model.response.PagedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface CustomerAPI {

    /**
     * GET /api/customers
     *
     * @param pageNo (optional, default to 0)
     * @param pageSize (optional, default to 10)
     * @param sortBy (optional, default to id)
     * @param sortDir (optional, default to asc)
     * @return OK (status code 200)
     */
    @Operation(
            operationId = "getAllCustomers",
            tags = {"customer-controller"},
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "OK",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PagedResult.class))
                        })
            })
    PagedResult<Customer> getAllCustomers(
            @Parameter(name = "pageNo", description = "", in = ParameterIn.QUERY) int pageNo,
            @Parameter(name = "pageSize", description = "", in = ParameterIn.QUERY) int pageSize,
            @Parameter(name = "sortBy", description = "", in = ParameterIn.QUERY) String sortBy,
            @Parameter(name = "sortDir", description = "", in = ParameterIn.QUERY) String sortDir);

    /**
     * GET /api/customers/{id}
     *
     * @param id (required)
     * @return OK (status code 200) or Bad Request (status code 400)
     */
    @Operation(
            operationId = "getCustomerById",
            tags = {"customer-controller"},
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "OK",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CustomerResponse.class))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class))
                        })
            })
    ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(name = "id", description = "", required = true, in = ParameterIn.PATH)
                    @PathVariable("id")
                    String id);

    /**
     * POST /api/customers
     *
     * @param customer (required)
     * @return Created (status code 201) or Bad Request (status code 400)
     */
    @Operation(
            operationId = "createCustomer",
            tags = {"customer-controller"},
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Created",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CustomerResponse.class))
                        }),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class))
                        })
            })
    CustomerResponse createCustomer(
            @Valid @RequestBody(description = "", required = true) Customer customer);
}
