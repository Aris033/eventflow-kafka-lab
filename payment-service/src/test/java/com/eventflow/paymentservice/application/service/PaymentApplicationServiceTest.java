package com.eventflow.paymentservice.application.service;

import com.eventflow.paymentservice.domain.model.OutboxEvent;
import com.eventflow.paymentservice.domain.model.Payment;
import com.eventflow.paymentservice.domain.port.OutboxEventRepositoryPort;
import com.eventflow.paymentservice.domain.port.PaymentRepositoryPort;
import com.eventflow.paymentservice.domain.port.ProcessedEventRepositoryPort;
import com.eventflow.sharedevents.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentApplicationServiceTest {

    private final PaymentRepositoryPort paymentRepository = mock(PaymentRepositoryPort.class);
    private final ProcessedEventRepositoryPort processedEventRepository = mock(ProcessedEventRepositoryPort.class);
    private final OutboxEventRepositoryPort outboxEventRepository = mock(OutboxEventRepositoryPort.class);
    private final PaymentApplicationService service = new PaymentApplicationService(
            paymentRepository,
            processedEventRepository,
            outboxEventRepository,
            new ObjectMapper().findAndRegisterModules(),
            "fail-payment-processing"
    );

    @Test
    void processCompletedPaymentCreatesOutboxEvent() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.process(orderCreated(new BigDecimal("99.99")));

        verify(processedEventRepository).save(any(UUID.class), any());
        verify(outboxEventRepository).save(org.mockito.ArgumentMatchers.argThat(event ->
                event.eventType().equals("PAYMENT_COMPLETED")
                        && event.topic().equals("payments.events")
        ));
    }

    @Test
    void processFailedPaymentCreatesOutboxEvent() {
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.process(orderCreated(new BigDecimal("1500")));

        verify(outboxEventRepository).save(org.mockito.ArgumentMatchers.argThat(event ->
                event.eventType().equals("PAYMENT_FAILED")
                        && event.topic().equals("payments.events")
        ));
    }

    @Test
    void processDuplicateEventDoesNothing() {
        OrderCreatedEvent event = orderCreated(new BigDecimal("99.99"));
        when(processedEventRepository.existsByEventId(event.eventId())).thenReturn(true);

        service.process(event);

        verify(paymentRepository, never()).save(any());
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }

    private OrderCreatedEvent orderCreated(BigDecimal amount) {
        UUID orderId = UUID.randomUUID();
        return OrderCreatedEvent.create(UUID.randomUUID(), orderId, "customer-1", amount);
    }
}
