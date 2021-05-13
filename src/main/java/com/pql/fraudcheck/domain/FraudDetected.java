package com.pql.fraudcheck.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Getter @Setter @NoArgsConstructor
@Document(collection = "detected_frauds")
public class FraudDetected {

    @Version
    private Long version;

    // useful for debugging/monitoring - same value held in MDC
    @Id
    private String requestId;
    
    @CreatedDate
    private Date detectedOn;

    private String terminalId;
    private Double amount;
    private String currency;
    private Integer threatScore;
    private String maskedCardNumber;

    private String rejectionMessage;
    private Integer fraudScore;
}
