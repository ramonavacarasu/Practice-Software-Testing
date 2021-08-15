package com.amigoscode.testing.customer;

<<<<<<<<< Temporary merge branch 1
=========
import org.springframework.beans.factory.annotation.Autowired;
>>>>>>>>> Temporary merge branch 2
import org.springframework.stereotype.Service;

@Service
public class CustomerRegistrationService {

<<<<<<<<< Temporary merge branch 1
    public void registerNewCustomer(CustomerRegistrationRequest request) {

=========

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {
>>>>>>>>> Temporary merge branch 2
    }
}
