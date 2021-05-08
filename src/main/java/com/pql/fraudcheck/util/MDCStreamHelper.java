package com.pql.fraudcheck.util;

import org.slf4j.MDC;
import java.util.Map;

/**
 * Created by pasqualericupero on 07/05/2021.
 */
public class MDCStreamHelper {

    private final Map<String, String> mdcCopy;


    private MDCStreamHelper() {
        this.mdcCopy = MDC.getCopyOfContextMap();
    }


    public static MDCStreamHelper getCurrentMdc() {
        return new MDCStreamHelper();
    }

    public void setMdc() {
        MDC.clear();
        if (mdcCopy != null) {
            MDC.setContextMap(mdcCopy);
        }
    }
}
