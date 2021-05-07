package com.pql.fraudcheck.dto;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * Created by pasqualericupero on 05/05/2021.
 */
@Data
public class FraudCheckRequest {

    @NotNull(message = "Cannot be empty")
    @DecimalMin(value = "0", message = "Cannot be negative")
    private Double amount;

    @NotBlank(message = "Cannot be empty")
    private String currency;

    @NotBlank(message = "Cannot be empty")
    private String terminalId;

    @NotNull(message = "Cannot be empty")
    @Min(value = 0, message = "Must be in range [0:100]")
    @Max(value = 100, message = "Must be in range [0:100]")
    private Integer threatScore;

    @NotNull(message = "Cannot be empty")
    @Size(min=15, max=16, message="Must be 15 or 16 digits")
    private String cardNumber;

    @Override
    public String toString() {
        return "FraudCheckRequest(" +
                "amount=" + amount +
                ", currency=" + currency +
                ", terminalId=" + terminalId +
                ", threatScore=" + threatScore +
                ", cardNumber=* * * " + cardNumber.substring(12) +
                ')';
    }
}
