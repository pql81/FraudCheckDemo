package com.pql.fraudcheck.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Entity
@Getter @Setter @NoArgsConstructor
@Table(name="carts")
public class FraudDetected {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String terminalId;
    private BigDecimal amount;
    private String currency;
    private Integer threatScore;
    private String lastCardDigits;

    private String rejectionMessage;
    private Integer fraudScore;
}
