package com.pql.fraudcheck.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Entity
@Getter @Setter @NoArgsConstructor
@Table(name="detected_frauds")
@Document(collection = "detected_frauds")
public class FraudDetected {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date detectedOn;

    // useful for debugging/monitoring - same value held in MDC
    private String requestId;

    private String terminalId;
    private Double amount;
    private String currency;
    private Integer threatScore;
    private String maskedCardNumber;

    private String rejectionMessage;
    private Integer fraudScore;
}
