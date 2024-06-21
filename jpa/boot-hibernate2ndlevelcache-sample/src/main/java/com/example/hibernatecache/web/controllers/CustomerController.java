package com.example.hibernatecache.web.controllers;

import com.example.hibernatecache.exception.CustomerNotFoundException;
import com.example.hibernatecache.model.query.FindCustomersQuery;
import com.example.hibernatecache.model.request.CustomerRequest;
import com.example.hibernatecache.model.response.CustomerResponse;
import com.example.hibernatecache.model.response.PagedResult;
import com.example.hibernatecache.services.CustomerService;
import com.example.hibernatecache.utils.AppConstants;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/customers")
class CustomerController {

    private final CustomerService customerService;

    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    PagedResult<CustomerResponse> getAllCustomers(
            @RequestParam(
                            value = "pageNo",
                            defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                            required = false)
                    int pageNo,
            @RequestParam(
                            value = "pageSize",
                            defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                            required = false)
                    int pageSize,
            @RequestParam(
                            value = "sortBy",
                            defaultValue = AppConstants.DEFAULT_SORT_BY,
                            required = false)
                    String sortBy,
            @RequestParam(
                            value = "sortDir",
                            defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                            required = false)
                    String sortDir) {
        FindCustomersQuery findCustomersQuery =
                new FindCustomersQuery(pageNo, pageSize, sortBy, sortDir);
        return customerService.findAllCustomers(findCustomersQuery);
    }

    @GetMapping("/{id}")
    ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return customerService
                .findCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @GetMapping("/search")
    public ResponseEntity<CustomerResponse> searchCustomer(@RequestParam String firstName) {
        return customerService
                .findCustomerByFirstName(firstName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    ResponseEntity<CustomerResponse> createCustomer(
            @RequestBody @Validated CustomerRequest customerRequest) {
        CustomerResponse response = customerService.saveCustomer(customerRequest);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/api/customers/{id}")
                        .buildAndExpand(response.customerId())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id, @RequestBody @Valid CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerRequest));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<CustomerResponse> deleteCustomer(@PathVariable Long id) {
        return customerService
                .findCustomerById(id)
                .map(
                        customer -> {
                            customerService.deleteCustomerById(id);
                            return ResponseEntity.ok(customer);
                        })
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }
}
