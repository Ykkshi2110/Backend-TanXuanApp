package com.peter.tanxuannewapp.controller;

import com.peter.tanxuannewapp.domain.Customer;
import com.peter.tanxuannewapp.domain.annotation.ApiMessage;
import com.peter.tanxuannewapp.domain.criteria.CriteriaSearchCustomer;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CustomerController {
    private final CustomerService customerService;
    @PostMapping("/customers/create")
    @ApiMessage("Create a customer")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer reqCreateCustomer) {
        Customer createdCustomer = this.customerService.handleCreateCustomer(reqCreateCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @PostMapping("/customers/update")
    @ApiMessage("Update a customer")
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer reqUpdateCustomer) {
        Customer updatedCustomer = this.customerService.handleUpdateCustomer(reqUpdateCustomer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/customers/delete/{id}")
    @ApiMessage("Delete a customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable int id) {
        this.customerService.handleDeleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/customers")
    @ApiMessage("Fetch all customers")
    public ResponseEntity<PaginationResponse> fetchAllCustomers(Pageable pageable) {
        return ResponseEntity.ok(this.customerService.handleFetchAllCustomers(pageable));
    }

    @PostMapping("/customers/filter")
    @ApiMessage("Filter customer with criteria")
    public ResponseEntity<PaginationResponse> fetchCustomerWithCriteria(Pageable pageable, @RequestBody CriteriaSearchCustomer criteriaSearchCustomer) {
        return ResponseEntity.ok().body(this.customerService.handleFilteredCustomers(pageable, criteriaSearchCustomer));
    }

    @GetMapping("/customers/{id}")
    @ApiMessage("Fetch a customer by id")
    public ResponseEntity<Customer> fetchCustomerById(@PathVariable int id) {
        return ResponseEntity.ok(this.customerService.handleFetchCustomerById(id));
    }
}
