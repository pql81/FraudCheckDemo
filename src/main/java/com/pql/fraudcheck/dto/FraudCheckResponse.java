package com.pql.fraudcheck.dto;

import lombok.Data;

/**
 * Created by pasqualericupero on 05/05/2021.
 */
@Data
public class FraudCheckResponse {

    public enum RejStatus {
        ALLOWED,
        REJECTED
    }

    private final RejStatus rejectionStatus;
    private final String rejectionMessage;
    private final Integer fraudScore;
}
