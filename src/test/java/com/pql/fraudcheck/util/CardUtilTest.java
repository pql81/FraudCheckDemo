package com.pql.fraudcheck.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by pasqualericupero on 09/05/2021.
 */
@RunWith(MockitoJUnitRunner.class)
public class CardUtilTest {

    @Test
    public void testCardLast4() throws Exception {
        String[] cardArray = {"5555444455551230", "5555444455551231", "5555444455551232",
                "4433443344331230", "4433443344331231", "4433443344331232",
                "343455553431230", "343455553431231", "343455553431232",
                "1234567", "12345678901234567890"};

        Arrays.stream(cardArray)
                .forEach(card -> {
                    assertTrue(card.endsWith(CardUtil.getCardLast4Digit(card)));
                    assertEquals(4, CardUtil.getCardLast4Digit(card).length());
                });
    }

    @Test
    public void testMaskPan() throws Exception {
        String[] cardArray = {"5555444455551230", "5555444455551231", "5555444455551232",
                "4433443344331230", "4433443344331231", "4433443344331232",
                "343455553431230", "343455553431231", "343455553431232",
                "1234567", "12345678901234567890"};

        Arrays.stream(cardArray)
                .forEach(card -> {
                    assertNotEquals(card, CardUtil.getMaskedPan(card));
                    assertTrue(card.endsWith(CardUtil.getMaskedPan(card).substring(card.length() -4, card.length())));
                    assertTrue(card.startsWith(CardUtil.getMaskedPan(card).substring(0, 1)));
                });
    }
}
