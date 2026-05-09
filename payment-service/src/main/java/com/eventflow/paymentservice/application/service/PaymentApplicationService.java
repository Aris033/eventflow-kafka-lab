/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.application.service;

import com.eventflow.paymentservice.application.usecase.GetPaymentUseCase;
import com.eventflow.paymentservice.application.usecase.ListPaymentsUseCase;
import com.eventflow.paymentservice.application.usecase.ProcessPaymentUseCase;
import com.eventflow.paymentservice.domain.exception.PaymentNotFoundException;
import com.eventflow.paymentservice.domain.model.Payment;
import com.eventflow.paymentservice.domain.model.PaymentStatus;
import com.eventflow.paymentservice.domain.model.OutboxEvent;
import com.eventflow.paymentservice.domain.port.PaymentRepositoryPort;
import com.eventflow.paymentservice.domain.port.OutboxEventRepositoryPort;
import com.eventflow.paymentservice.domain.port.ProcessedEventRepositoryPort;
import com.eventflow.sharedevents.EventTopics;
import com.eventflow.sharedevents.OrderCreatedEvent;
import com.eventflow.sharedevents.PaymentCompletedEvent;
import com.eventflow.sharedevents.PaymentFailedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentApplicationService implements ProcessPaymentUseCase, GetPaymentUseCase, ListPaymentsUseCase {

    private static final Logger log = LoggerFactory.getLogger(PaymentApplicationService.class);

    private final PaymentRepositoryPort paymentRepository;
    private final ProcessedEventRepositoryPort processedEventRepository;
    private final OutboxEventRepositoryPort outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final String failOnCustomerIdPrefix;

    public PaymentApplicationService(
            PaymentRepositoryPort paymentRepository,
            ProcessedEventRepositoryPort processedEventRepository,
            OutboxEventRepositoryPort outboxEventRepository,
            ObjectMapper objectMapper,
            @Value("${eventflow.payment.simulation.fail-on-customer-id-prefix:fail-payment-processing}") String failOnCustomerIdPrefix
    ) {
        this.paymentRepository = paymentRepository;
        this.processedEventRepository = processedEventRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.failOnCustomerIdPrefix = failOnCustomerIdPrefix;
    }

    @Override
    @Transactional
    public void process(OrderCreatedEvent event) {
        if (shouldFailProcessing(event.customerId())) {
            throw new IllegalStateException("Controlled payment processing failure for customerId " + event.customerId());
        }

        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.info(
                    "Duplicate OrderCreatedEvent ignored: eventId={}, correlationId={}, orderId={}",
                    event.eventId(),
                    event.correlationId(),
                    event.orderId()
            );
            return;
        }

        Payment payment = paymentRepository.save(Payment.process(event.orderId(), event.totalAmount()));
        processedEventRepository.save(event.eventId(), event.eventType());

        if (payment.status() == PaymentStatus.COMPLETED) {
            PaymentCompletedEvent completedEvent = PaymentCompletedEvent.create(
                    event.correlationId(),
                    payment.id(),
                    payment.orderId(),
                    payment.amount()
            );
            saveOutboxEvent(payment.id(), completedEvent.eventId(), completedEvent.eventType().name(), EventTopics.PAYMENTS_EVENTS,
                    payment.orderId().toString(), serialize(completedEvent));
            log.info(
                    "Payment completed: eventId={}, correlationId={}, orderId={}, paymentId={}",
                    event.eventId(),
                    event.correlationId(),
                    payment.orderId(),
                    payment.id()
            );
        } else {
            PaymentFailedEvent failedEvent = PaymentFailedEvent.create(
                    event.correlationId(),
                    payment.id(),
                    payment.orderId(),
                    payment.amount(),
                    payment.failureReason()
            );
            saveOutboxEvent(payment.id(), failedEvent.eventId(), failedEvent.eventType().name(), EventTopics.PAYMENTS_EVENTS,
                    payment.orderId().toString(), serialize(failedEvent));
            log.info(
                    "Payment failed: eventId={}, correlationId={}, orderId={}, paymentId={}, reason={}",
                    event.eventId(),
                    event.correlationId(),
                    payment.orderId(),
                    payment.id(),
                    payment.failureReason()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> listPayments() {
        return paymentRepository.findAll();
    }

    private boolean shouldFailProcessing(String customerId) {
        return failOnCustomerIdPrefix != null
                && !failOnCustomerIdPrefix.isBlank()
                && customerId != null
                && customerId.startsWith(failOnCustomerIdPrefix);
    }

    private void saveOutboxEvent(UUID aggregateId, UUID eventId, String eventType, String topic, String messageKey, String payload) {
        outboxEventRepository.save(OutboxEvent.pending(aggregateId, "PAYMENT", eventId, eventType, topic, messageKey, payload));
    }

    private String serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize payment event", ex);
        }
    }
}
