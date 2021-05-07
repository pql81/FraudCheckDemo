package com.pql.fraudcheck.dto;

import lombok.Data;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Data
public class CardResponse {

    private final Double lastLocationLat;
    private final Double lastLocationLong;
}
