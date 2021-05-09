package com.pql.fraudcheck.util;

/**
 * Created by pasqualericupero on 09/05/2021.
 */
public class CardUtil {

    public static String getCardLast4Digit(String pan) {
        if (pan != null && pan.length() > 4) {
            int len = pan.length();

            return pan.substring(len-4, len);
        } else {
            return pan;
        }
    }

    public static String getMaskedPan(String pan) {
        if (pan != null && pan.length() > 4) {
            StringBuilder maskedNumber = new StringBuilder();
            int len = pan.length();

            for (int i = 0; i < len; i++) {
                if (i == 0 || (i > pan.length() - 5)) {
                    maskedNumber.append(pan.charAt(i));
                } else {
                    maskedNumber.append('*');
                }
            }

            return maskedNumber.toString();
        } else {
            return "";
        }
    }
}
