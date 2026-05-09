package com.eventflow.paymentservice.application.usecase;

import com.eventflow.paymentservice.domain.model.Payment;

import java.util.UUID;

public interface GetPaymentUseCase {

    Payment getPaymentByOrderId(UUID orderId);
}
