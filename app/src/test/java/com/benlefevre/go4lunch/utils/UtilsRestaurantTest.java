package com.benlefevre.go4lunch.utils;

import android.content.Context;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.models.Restaurant;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.TimeOfWeek;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsRestaurantTest {

    @Test
    public void formatAddress() {
        String formattedAddress = "10 Avenue Benoit, 75000 Paris, France";

        AddressComponent address1 = mock(AddressComponent.class);
        when(address1.getTypes()).thenReturn(Collections.singletonList("street_number"));
        when(address1.getName()).thenReturn("10");

        AddressComponent address2 = mock(AddressComponent.class);
        when(address2.getTypes()).thenReturn(Collections.singletonList("route"));
        when(address2.getName()).thenReturn("Avenue Benoit");

        List<AddressComponent> addressComponents = new ArrayList<>();
        addressComponents.add(address1);
        addressComponents.add(address2);

        assertEquals("10 Avenue Benoit", UtilsRestaurant.formatAddress(formattedAddress, addressComponents));

        when(address1.getTypes()).thenReturn(Collections.singletonList("locality"));
        when(address1.getName()).thenReturn("Paris");

        when(address2.getTypes()).thenReturn(Collections.singletonList("postal_code"));
        when(address2.getName()).thenReturn("75000");

        assertEquals("10 Avenue Benoit", UtilsRestaurant.formatAddress(formattedAddress, addressComponents));

        address1 = null;
        address2 = null;

        assertEquals("10 Avenue Benoit", UtilsRestaurant.formatAddress(formattedAddress, addressComponents));

        formattedAddress = null;

        assertEquals("No place's address found", UtilsRestaurant.formatAddress(formattedAddress, addressComponents));
    }

    @Test
    public void getOpeningHours() {
        OpeningHours openingHours = null;

        assertNull(UtilsRestaurant.getOpeningHours(openingHours));

        openingHours = mock(OpeningHours.class);
        Period period1 = mock(Period.class);
        Period period2 = mock(Period.class);
        when(openingHours.getPeriods()).thenReturn(Arrays.asList(period1, period2));
        when(period1.getOpen()).thenReturn(TimeOfWeek.newInstance(DayOfWeek.SUNDAY, LocalTime.newInstance(12, 0)));
        when(period1.getClose()).thenReturn(TimeOfWeek.newInstance(DayOfWeek.SUNDAY, LocalTime.newInstance(18, 30)));
        when(period2.getOpen()).thenReturn(TimeOfWeek.newInstance(DayOfWeek.MONDAY, LocalTime.newInstance(9, 0)));
        when(period2.getClose()).thenReturn(TimeOfWeek.newInstance(DayOfWeek.MONDAY, LocalTime.newInstance(18, 0)));

        List<HashMap<String, String>> hours = UtilsRestaurant.getOpeningHours(openingHours);

        assertEquals("SUNDAY", hours.get(0).get("day"));
        assertEquals("12:00", hours.get(0).get("open"));
        assertEquals("18:30", hours.get(0).get("close"));

        assertEquals("MONDAY", hours.get(1).get("day"));
        assertEquals("09:00", hours.get(1).get("open"));
        assertEquals("18:00", hours.get(1).get("close"));
    }

    @Test
    public void displayOpeningHour() {
        Context context = mock(Context.class);
        when(context.getString(R.string.no_info_hours)).thenReturn("There is no information about opening hours");
        when(context.getString(R.string.closing_soon)).thenReturn("Closing soon");
        when(context.getString(R.string.open_until)).thenReturn("Open until ");
        when(context.getString(R.string.closed)).thenReturn("Closed");
        Restaurant restaurant = new Restaurant();

        assertEquals("There is no information about opening hours", UtilsRestaurant.displayOpeningHours(restaurant, context));

        List<HashMap<String, String>> hours = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        map.put("day", "SUNDAY");
        map.put("close", "22:00");
        map.put("open", "09:00");
        hours.add(0, map);
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("day", "MONDAY");
        map1.put("close", "22:00");
        map1.put("open", "09:00");
        hours.add(1, map1);
        HashMap<String, String> map2 = new HashMap<>();
        map2.put("day", "TUESDAY");
        map2.put("close", "22:00");
        map2.put("open", "09:00");
        hours.add(2, map2);
        HashMap<String, String> map3 = new HashMap<>();
        map3.put("day", "WEDNESDAY");
        map3.put("close", "22:00");
        map3.put("open", "09:00");
        hours.add(3, map3);
        HashMap<String, String> map4 = new HashMap<>();
        map4.put("day", "THURDAY");
        map4.put("close", "22:00");
        map4.put("open", "09:00");
        hours.add(4, map4);
        HashMap<String, String> map5 = new HashMap<>();
        map5.put("day", "FRIDAY");
        map5.put("close", "22:00");
        map5.put("open", "09:00");
        hours.add(5, map5);
        HashMap<String, String> map6 = new HashMap<>();
        map6.put("day", "SATURDAY");
        map6.put("close", "22:00");
        map6.put("open", "09:00");
        hours.add(6, map6);
        restaurant.setOpeningHours(hours);

        Calendar now = Calendar.getInstance();
        Calendar close = Calendar.getInstance();
        close.set(Calendar.HOUR_OF_DAY, 22);
        close.set(Calendar.MINUTE, 0);
        Calendar open = Calendar.getInstance();
        open.set(Calendar.HOUR_OF_DAY,9);
        open.set(Calendar.MINUTE,0);
        long beforeClose = 30 * 60 * 1000;
        boolean isOpen = now.before(close) && now.after(open);

        if (isOpen) {
            if (close.getTimeInMillis() - now.getTimeInMillis() < beforeClose)
                assertEquals("Closing soon", UtilsRestaurant.displayOpeningHours(restaurant, context));
            else {
                if (Locale.getDefault().getLanguage().equals("en"))
                    assertEquals("Open until 10:00 PM",UtilsRestaurant.displayOpeningHours(restaurant,context));
                else
                assertEquals("Open until 22:00", UtilsRestaurant.displayOpeningHours(restaurant, context));
            }
        } else
            assertEquals("Closed", UtilsRestaurant.displayOpeningHours(restaurant, context));
    }

}