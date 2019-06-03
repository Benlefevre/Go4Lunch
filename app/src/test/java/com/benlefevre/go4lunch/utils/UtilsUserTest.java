package com.benlefevre.go4lunch.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilsUserTest {

    @Test
    public void compareDate(){
        Date date = null;
        Calendar calendar = Calendar.getInstance();

        assertFalse(UtilsUser.compareDate(date));

        calendar.set(Calendar.HOUR_OF_DAY,10);
        date = calendar.getTime();

        assertTrue(UtilsUser.compareDate(date));

        calendar.add(Calendar.DAY_OF_YEAR,-1);
        date = calendar.getTime();

        assertFalse(UtilsUser.compareDate(date));
    }
}
