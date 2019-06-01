package com.benlefevre.go4lunch.utils;

import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.TimeOfWeek;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsRestaurantTest {

    @Test
    public void formatAddress(){
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

        assertEquals("10 Avenue Benoit",UtilsRestaurant.formatAddress(formattedAddress,addressComponents));

        when(address1.getTypes()).thenReturn(Collections.singletonList("locality"));
        when(address1.getName()).thenReturn("Paris");

        when(address2.getTypes()).thenReturn(Collections.singletonList("postal_code"));
        when(address2.getName()).thenReturn("75000");

        assertEquals("10 Avenue Benoit",UtilsRestaurant.formatAddress(formattedAddress,addressComponents));

        address1 = null;
        address2 = null;

        assertEquals("10 Avenue Benoit",UtilsRestaurant.formatAddress(formattedAddress,addressComponents));

        formattedAddress = null;

        assertEquals("No place's address found",UtilsRestaurant.formatAddress(formattedAddress,addressComponents));
    }

    @Test
    public void getOpeningHours(){
        OpeningHours openingHours = null;

        assertNull(UtilsRestaurant.getOpeningHours(openingHours));

        openingHours = mock(OpeningHours.class);
        Period period1 = mock(Period.class);
        Period period2 = mock(Period.class);
        when(openingHours.getPeriods()).thenReturn(Arrays.asList(period1,period2));
        when(period1.getOpen()).thenReturn(TimeOfWeek.newInstance(DayOfWeek.SUNDAY, LocalTime.newInstance(12,0)));
        when(period1.getClose()).thenReturn(TimeOfWeek.newInstance(DayOfWeek.SUNDAY,LocalTime.newInstance(18,30)));
        when(period2.getOpen()).thenReturn(TimeOfWeek.newInstance(DayOfWeek.MONDAY,LocalTime.newInstance(9,0)));
        when(period2.getClose()).thenReturn(TimeOfWeek.newInstance(DayOfWeek.MONDAY,LocalTime.newInstance(18,0)));

        List<HashMap<String,String>> hours = UtilsRestaurant.getOpeningHours(openingHours);

        assertEquals("SUNDAY",hours.get(0).get("day"));
        assertEquals("12:00",hours.get(0).get("open"));
        assertEquals("18:30",hours.get(0).get("close"));

        assertEquals("MONDAY",hours.get(1).get("day"));
        assertEquals("09:00",hours.get(1).get("open"));
        assertEquals("18:00",hours.get(1).get("close"));
    }

}