package com.amigoscode.testing.customer;

import com.amigoscode.testing.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {


    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository,
                                       PhoneNumberValidator phoneNumberValidator) {
        this.customerRepository = customerRepository;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {

        String actualPhoneNumber = request.getCustomer().getPhoneNumber();

        // TODO: Validate the phone number is valid
        if (!phoneNumberValidator.test(actualPhoneNumber)) {
            throw new IllegalStateException("Phone Number " + actualPhoneNumber + " is not valid.");
        }
        Optional<Customer> customerOptional = customerRepository
                .selectCustomerByPhoneNumber(actualPhoneNumber);

        if(customerOptional.isPresent()) {
            Customer customer = customerOptional.get();

            if (customer.getName().equals(request.getCustomer().getName())) {
                return;
            }
            throw new IllegalStateException(String.format("phone number [%s] is already taken", actualPhoneNumber));

        }

        if(request.getCustomer().getId() == null) {
            request.getCustomer().setId(UUID.randomUUID());
        }

        customerRepository.save(request.getCustomer());

    }
}
