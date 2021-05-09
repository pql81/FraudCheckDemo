package com.pql.fraudcheck.util;

import lombok.extern.log4j.Log4j2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by pasqualericupero on 08/05/2021.
 */
@Log4j2
public class LogHelper {

    public static void logResult(String action, boolean isSuccess, String errorCause) {
        // it logs the final status - success or failure refers to the call status, if it was possible or not to complete
        // not to the fraud check result or input validation (bad request is still success in our case)
        Map<String, String> logMap = new LinkedHashMap<>();
        logMap.put("Operation", action);
        logMap.put("Status", isSuccess?"SUCCESS":"FAILURE");
        if (errorCause != null) {
            logMap.put("ErrorMsg", errorCause);
        }

        log.info(logMap.keySet().stream()
                .map(key -> key + "=" + logMap.get(key))
                .collect(Collectors.joining(" | ")));
    }
}
