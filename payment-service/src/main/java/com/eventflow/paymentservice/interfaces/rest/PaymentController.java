/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.interfaces.rest;

import com.eventflow.paymentservice.application.usecase.GetPaymentUseCase;
import com.eventflow.paymentservice.application.usecase.ListPaymentsUseCase;
import com.eventflow.paymentservice.interfaces.rest.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments")
public class PaymentController {

    private final GetPaymentUseCase getPaymentUseCase;
    private final ListPaymentsUseCase listPaymentsUseCase;

    public PaymentController(GetPaymentUseCase getPaymentUseCase, ListPaymentsUseCase listPaymentsUseCase) {
        this.getPaymentUseCase = getPaymentUseCase;
        this.listPaymentsUseCase = listPaymentsUseCase;
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Gets a payment by order id")
    public PaymentResponse getPaymentByOrderId(@PathVariable("orderId") UUID orderId) {
        return PaymentResponse.from(getPaymentUseCase.getPaymentByOrderId(orderId));
    }

    @GetMapping
    @Operation(summary = "Lists processed payments")
    public List<PaymentResponse> listPayments() {
        return listPaymentsUseCase.listPayments().stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
