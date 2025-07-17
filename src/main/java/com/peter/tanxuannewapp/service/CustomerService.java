package com.peter.tanxuannewapp.service;

import com.peter.tanxuannewapp.domain.Customer;
import com.peter.tanxuannewapp.domain.QCustomer;
import com.peter.tanxuannewapp.domain.criteria.CriteriaSearchCustomer;
import com.peter.tanxuannewapp.domain.resposne.Meta;
import com.peter.tanxuannewapp.domain.resposne.PaginationResponse;
import com.peter.tanxuannewapp.domain.resposne.ResCustomerDTO;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.CustomerRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private static final String CUSTOMER_EMAIL_ALREADY_EXIST = "Customer email already exist";
    private static final String CUSTOMER_NOT_FOUND = "Customer not found";

    public ResCustomerDTO convertCustomerToCustomerDTO(Customer customer) {
        return this.modelMapper.map(customer, ResCustomerDTO.class);
    }

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

    public PaginationResponse handleFilteredCustomers(Pageable pageable, CriteriaSearchCustomer criteriaSearchCustomer) {
        QCustomer qCustomer = QCustomer.customer;
        BooleanBuilder builder = new BooleanBuilder();
        if(criteriaSearchCustomer.getEmail() != null && !criteriaSearchCustomer.getEmail().isEmpty()){
            builder.and(qCustomer.email.contains(criteriaSearchCustomer.getEmail()));
        }
        if(criteriaSearchCustomer.getName() != null && !criteriaSearchCustomer.getName().isEmpty()){
            builder.and(qCustomer.name.contains(criteriaSearchCustomer.getName()));
        }
        if(criteriaSearchCustomer.getPhone() != null && !criteriaSearchCustomer.getPhone().isEmpty()){
            builder.and(qCustomer.phone.contains(criteriaSearchCustomer.getPhone()));
        }
        if(criteriaSearchCustomer.getAddress() != null && !criteriaSearchCustomer.getAddress().isEmpty()){
            builder.and(qCustomer.address.contains(criteriaSearchCustomer.getAddress()));
        }
        if(criteriaSearchCustomer.getCreatedAt() != null && !criteriaSearchCustomer.getCreatedAt().isEmpty()){
            LocalDate localDate = LocalDate.parse(criteriaSearchCustomer.getCreatedAt());
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            Instant startOfDay = localDate.atStartOfDay(zoneId).toInstant();
            Instant endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).minusNanos(1).toInstant();
            builder.and(qCustomer.createdAt.between(startOfDay, endOfDay));
        }

        Page<Customer> customerFilteredPages = this.customerRepository.findAll(builder, pageable);
        Page<ResCustomerDTO> customerFilteredDTOPages = customerFilteredPages.map(this::convertCustomerToCustomerDTO);

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(customerFilteredDTOPages.getTotalPages());
        meta.setTotal(customerFilteredDTOPages.getTotalElements());

        PaginationResponse paginationResponse = new PaginationResponse();
        paginationResponse.setMeta(meta);
        paginationResponse.setResult(customerFilteredPages.getContent());

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