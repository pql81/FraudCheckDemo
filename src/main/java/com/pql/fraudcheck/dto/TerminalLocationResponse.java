package com.pql.fraudcheck.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Data
public class TerminalLocationResponse {

    private final Double latitude;
    private final Double longitude;
}
