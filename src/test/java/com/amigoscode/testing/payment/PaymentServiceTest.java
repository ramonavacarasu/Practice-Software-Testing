package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();

        // ... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        Currency.USD,
                        "card123xx",
                        "Donation"));

        // ... Card is charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription())).willReturn(new CardPaymentCharge(true));

        // When
        underTest.chargeCard(customerId, paymentRequest);


        // Then
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();

        assertThat(paymentArgumentCaptorValue)
                .isEqualToIgnoringGivenFields(paymentRequest.getPayment(), "customerId");

        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() {

        // Given
        UUID customerId = UUID.randomUUID();

        // ... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        Currency.USD,
                        "CARD123xx",
                        "Donation"
                )
        );

        // ... Card is not charged successfully
        Payment payment = paymentRequest.getPayment();
        given(cardPaymentCharger.chargeCard(
                payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription()
        )).willReturn(new CardPaymentCharge(false));

        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Card not debited for customer %s", customerId));

        // Then
        //then(paymentRepository).should(never()).save(any(Payment.class));
        then(paymentRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    void itShouldNotChargeCurrencyWhenCurrencyNotSupported() {

        // Given
        UUID customerId = UUID.randomUUID();

        // ... Customer Exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        // ... Payment request
        Currency eur = Currency.EUR;
        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        eur,
                        "cardRamo",
                        "Donation"
                )
        );



        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Currency[%s] not supported", eur));

        // Then
        // ... No interaction CardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoMoreInteractions();
        //then(paymentRepository).should(never()).save(any(Payment.class));
        // ... No interaction paymentRepository
        then(paymentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldNotChargeThrowWhenCustomerNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();

        // When customer not found
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)

                .hasMessageContaining(String.format("Customer with id [%s] not found", customerId));
        then(cardPaymentCharger).shouldHaveNoMoreInteractions();
        then(paymentRepository).shouldHaveNoMoreInteractions();

    }
}