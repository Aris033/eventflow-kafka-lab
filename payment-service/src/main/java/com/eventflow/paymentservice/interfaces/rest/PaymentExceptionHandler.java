package com.eventflow.paymentservice.interfaces.rest;

import com.eventflow.paymentservice.domain.exception.PaymentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    ProblemDetail handlePaymentNotFound(PaymentNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setTitle("Payment not found");
        return problemDetail;
    }
}
