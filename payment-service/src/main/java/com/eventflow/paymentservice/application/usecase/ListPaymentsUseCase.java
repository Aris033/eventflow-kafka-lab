package com.eventflow.paymentservice.application.usecase;

import com.eventflow.paymentservice.domain.model.Payment;

import java.util.List;

public interface ListPaymentsUseCase {

    List<Payment> listPayments();
}
