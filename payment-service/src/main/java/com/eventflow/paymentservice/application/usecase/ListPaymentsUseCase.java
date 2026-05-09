/*
 * Copyright (c) 2026 Ignacio Aristi.
 *
 * This project is licensed under the MIT License.
 * See the [LICENSE](./LICENSE) file for details.
 *
 * GitHub: [Aris033](https://github.com/Aris033)
 */

package com.eventflow.paymentservice.application.usecase;

import com.eventflow.paymentservice.domain.model.Payment;

import java.util.List;

public interface ListPaymentsUseCase {

    List<Payment> listPayments();
}
