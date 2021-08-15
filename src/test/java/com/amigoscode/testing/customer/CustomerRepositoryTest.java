package com.amigoscode.testing.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;


    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given

        UUID uuid = UUID.randomUUID();
        String phoneNumber = "0000";
        Customer customer = new Customer(uuid, "Abel", phoneNumber);

        // When
        underTest.save(customer);

        // Then
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToIgnoringGivenFields(customer);
                });
    }

    @Test
    void itNotShouldSelectCustomerByPhoneNumberWhenNumberDoesNotExists() {
        // Given

        String phoneNumber = "0000";

        // When

        // Then
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void itShouldSaveCustomer() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Abel", "0000");

        // When
        underTest.save(customer);

        // Then
        Optional<Customer> optionalCustomer = underTest.findById(id);

        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualTo(id);
//                    assertThat(c.getName()).isEqualTo("Abel");
//                    assertThat(c.getPhoneNumber()).isEqualTo("0000");
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });


    }

    @Test
    void itShouldNotSaveTheCustomerWhenNameIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "0000");

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);
       // assertThat(underTest.findById(id)).isNotPresent();

    }

    @Test
    void itShouldNotSaveTheCustomerWhenPhoneNumberIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Ioana", null);

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);
        // assertThat(underTest.findById(id)).isNotPresent();

    }

}