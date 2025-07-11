package org.springboot.udemy.initial.category.payload;

import lombok.Data;

@Data
public class StripePaymentDto {
    private Long amount;
    private String currency;
}
