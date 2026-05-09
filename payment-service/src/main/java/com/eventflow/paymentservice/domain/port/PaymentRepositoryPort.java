package com.eventflow.paymentservice.domain.port;

import com.eventflow.paymentservice.domain.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {

    Payment save(Payment payment);

    Optional<Payment> findByOrderId(UUID orderId);

    List<Payment> findAll();
}
