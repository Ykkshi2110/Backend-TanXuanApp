package com.peter.tanxuannewapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.peter.tanxuannewapp.domain.Customer;
import com.peter.tanxuannewapp.domain.resposne.Meta;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String CUSTOMER_EMAIL_ALREADY_EXIST = "Customer email already exist";
    private static final String CUSTOMER_NOT_FOUND = "Customer not found";

    public Customer handleCreateCustomer(Customer reqCreateCustomer) {
        if (this.customerRepository.existsByEmail(reqCreateCustomer.getEmail()))
            throw new ResourceAlreadyExistsException(CUSTOMER_EMAIL_ALREADY_EXIST);

        reqCreateCustomer.setPassword(passwordEncoder.encode(reqCreateCustomer.getPassword()));

        return this.customerRepository.save(reqCreateCustomer);
    }

    public Customer handleUpdateCustomer(Customer reqUpdateCustomer) {
        Customer currentCustomer = this.customerRepository.findById(reqUpdateCustomer.getId())
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND));
        currentCustomer.setName(reqUpdateCustomer.getName());
        currentCustomer.setEmail(reqUpdateCustomer.getEmail());
        currentCustomer.setPhone(reqUpdateCustomer.getPhone());
        currentCustomer.setAddress(reqUpdateCustomer.getAddress());
        return this.customerRepository.save(currentCustomer);
    }

    public void handleDeleteCustomer(int customerId) {
        Customer currentCustomer = this.customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND));
        this.customerRepository.delete(currentCustomer);
    }

    public PaginationResponse handleFetchAllCustomers(Pageable pageable) {
        Page<Customer> customers = this.customerRepository.findAll(pageable);
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(customers.getTotalPages());
        meta.setTotal(customers.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(customers.getContent());

        return paginationResponse;
    }

    public Customer handleFetchCustomerById(int customerId) {
        return this.customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND));
    }

    public Customer handleGetCustomerByEmail(String email) {
        return this.customerRepository.findByEmail(email).orElse(null);
    }

    public void setRefreshTokenInCustomerDB(String refreshToken, String email) {
        Customer currentCustomer = this.handleGetCustomerByEmail(email);
        if (currentCustomer != null) {
            currentCustomer.setRefreshToken(refreshToken);
            this.customerRepository.save(currentCustomer);
        }
    }

    public boolean checkEmailExists(String email) {
        return this.customerRepository.existsByEmail(email);
    }

    public Customer handleGetCustomerByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.customerRepository.findByRefreshTokenAndEmail(refreshToken, email)
                .orElse(null);
    }

}