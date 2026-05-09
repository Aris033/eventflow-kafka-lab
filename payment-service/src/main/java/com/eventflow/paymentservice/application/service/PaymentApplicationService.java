package com.eventflow.paymentservice.application.service;

import com.eventflow.paymentservice.application.usecase.GetPaymentUseCase;
import com.eventflow.paymentservice.application.usecase.ListPaymentsUseCase;
import com.eventflow.paymentservice.application.usecase.ProcessPaymentUseCase;
import com.eventflow.paymentservice.domain.exception.PaymentNotFoundException;
import com.eventflow.paymentservice.domain.model.Payment;
import com.eventflow.paymentservice.domain.model.PaymentStatus;
import com.eventflow.paymentservice.domain.port.PaymentEventPublisherPort;
import com.eventflow.paymentservice.domain.port.PaymentRepositoryPort;
import com.eventflow.paymentservice.domain.port.ProcessedEventRepositoryPort;
import com.eventflow.sharedevents.OrderCreatedEvent;
import com.eventflow.sharedevents.PaymentCompletedEvent;
import com.eventflow.sharedevents.PaymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentApplicationService implements ProcessPaymentUseCase, GetPaymentUseCase, ListPaymentsUseCase {

    private static final Logger log = LoggerFactory.getLogger(PaymentApplicationService.class);

    private final PaymentRepositoryPort paymentRepository;
    private final ProcessedEventRepositoryPort processedEventRepository;
    private final PaymentEventPublisherPort paymentEventPublisher;

    public PaymentApplicationService(
            PaymentRepositoryPort paymentRepository,
            ProcessedEventRepositoryPort processedEventRepository,
            PaymentEventPublisherPort paymentEventPublisher
    ) {
        this.paymentRepository = paymentRepository;
        this.processedEventRepository = processedEventRepository;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    @Override
    @Transactional
    public void process(OrderCreatedEvent event) {
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
            paymentEventPublisher.publish(completedEvent);
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
            paymentEventPublisher.publish(failedEvent);
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
}
