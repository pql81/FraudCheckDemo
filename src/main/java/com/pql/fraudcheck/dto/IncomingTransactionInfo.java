package com.pql.fraudcheck.dto;

import lombok.Data;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Data
public class IncomingTransactionInfo {

    private final Double amount;
    private final String currency;
    private final Integer threatScore;
    private final Integer recentCardTransactionNumber;
    private final Double cardLastLocationLat;
    private final Double cardLastLocationLong;
    private final Integer recentTerminalTransactionNumber;
    private final Double terminalLat;
    private final Double terminalLong;
}
