package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {


    @Mock
    private CustomerRepository customerRepository; // = mock(CustomerRepository.class);

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    // when phone number is not taken, customer is saved
    @Test
    void itShouldSaveNewCustomer() {
        // Given a phone number and a customer
        String phoneNumber = "000099";
        Customer actualCustomer = new Customer(UUID.randomUUID(), "Ramona", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(actualCustomer);

        // ... No customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // When
        underTest.registerNewCustomer(request);

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(actualCustomer);
    }


    @Test
    void itShouldSaveNewCustomerWhenTheIdIsNull() {
        // Given a phone number and a customer
        String phoneNumber = "000099";
        Customer actualCustomer = new Customer(null, "Ramona", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(actualCustomer);

        // ... No customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // When
        underTest.registerNewCustomer(request);

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(actualCustomer, "id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    // if phone number is taken by the same customer return
    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        // Given a customer
        String phoneNumber = "000011";
        UUID id = UUID.randomUUID();
        Customer actualCustomer = new Customer(id, "Ramona", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(actualCustomer);

        // ... an existing customer is returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(actualCustomer));

        // When
        underTest.registerNewCustomer((request));

        // Then
        then(customerRepository).should(never()).save(any());
       // then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
       // then(customerRepository).shouldHaveNoMoreInteractions();

    }

    // if phone number is not taken by the same customer thrown an exception
    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {

        // Given a customer
        String phoneNumber = "1111";
        Customer anotherCustomer = new Customer(UUID.randomUUID(), "Raluca", phoneNumber);
        Customer actualCustomer = new Customer(UUID.randomUUID(), "Roxana", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(actualCustomer);

        // ... an existing customer
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(anotherCustomer));

        // When
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is already taken", phoneNumber));

        // Finally
        then(customerRepository).should(never()).save(any(Customer.class));

    }
}