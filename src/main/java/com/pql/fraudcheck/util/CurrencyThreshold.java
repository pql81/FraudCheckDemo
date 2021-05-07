package com.pql.fraudcheck.util;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
// As this is a demo, currencies and their transaction amount thresholds are held in this enum
// It can be a DB table loaded at startup, but those data are supposed to live in memory as they are frequently accessed
public enum CurrencyThreshold {
    EUR(10000),
    DKK(70000),
    USD(11000),
    GBP(8000),
    JPY(1300000);

    public final int threshold;

    CurrencyThreshold(int threshold) {
        this.threshold = threshold;
    }
}
