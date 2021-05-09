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
    @Pattern(regexp = "^[A-Z]{3}", message="Must be ISO code")
    private String currency; // string type as it is decoupled from the util enum

    @NotBlank(message = "Cannot be empty")
    private String terminalId;

    @NotNull(message = "Cannot be empty")
    @Min(value = 0, message = "Must be in range [0:100]")
    @Max(value = 100, message = "Must be in range [0:100]")
    private Integer threatScore;

    @NotNull(message = "Cannot be empty")
//    @Pattern(regexp = "^[0-9]{13,16}", message="Must be between 13 and 16 digits")
    private String cardNumber;

    @Override
    public String toString() {
        return "FraudCheckRequest(" +
                "amount=" + amount +
                ", currency=" + currency +
                ", terminalId=" + terminalId +
                ", threatScore=" + threatScore +
                ", cardNumber=****" +
                ')';
    }
}
