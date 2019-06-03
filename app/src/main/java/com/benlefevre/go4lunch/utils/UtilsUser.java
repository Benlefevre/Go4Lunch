package com.benlefevre.go4lunch.utils;

import java.util.Calendar;
import java.util.Date;

public class UtilsUser {

    public static boolean compareDate(Date choiceDate){
        if (choiceDate == null)
            return false;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,-1);
        calendar.set(Calendar.HOUR_OF_DAY,12);
        calendar.set(Calendar.MINUTE,0);

        return choiceDate.after(calendar.getTime());
    }
}
