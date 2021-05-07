package com.pql.fraudcheck.dto;

import lombok.Data;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
@Data
public class FraudRuleScore {

    private final Integer score;
    private final String message;
}
