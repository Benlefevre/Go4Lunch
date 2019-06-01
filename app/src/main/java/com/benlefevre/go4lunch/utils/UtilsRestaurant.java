package com.benlefevre.go4lunch.utils;

import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UtilsRestaurant {

    /**
     * Sets a string as the formatted address when we save a restaurant in Firestore.
     *
     * @param formattedAddress  The human readable address returned by GoogleMaps server when we fetch
     *                          place's details.
     * @param addressComponents The list of address components returned by GoogleMaps server when
     *                          we fetch place's details
     * @return a String according to the desired format.
     */
    public static String formatAddress(String formattedAddress, List<AddressComponent> addressComponents) {
        String address = "";
        for (AddressComponent addressComponent : addressComponents) {
            if (addressComponent.getTypes().get(0).contains("street_number"))
                address += addressComponent.getName();
            if (addressComponent.getTypes().get(0).contains("route"))
                address += addressComponent.getName();
        }
        if (address.isEmpty() && formattedAddress == null)
            address = "No place's address found";
        else if (address.isEmpty())
            address = formattedAddress;
        return address;
    }

    /**
     * Creates a List<HashMap<String,String> from an OpeningHours object when we save a restaurant in Firestore.
     *
     * @param openingHours the OpeningHours returned by GoogleMaps server when we fetch place's details.
     * @return a List<HashMap<String,String> containing the values of OpeningHours object.
     */
    public static List<HashMap<String, String>> getOpeningHours(OpeningHours openingHours) {
        Locale locale = Locale.getDefault();
        if (openingHours == null)
            return null;
        List<HashMap<String, String>> hours = new ArrayList<>();
        for (Period period : openingHours.getPeriods()) {
            HashMap<String, String> map = new HashMap<>();
            if (period.getOpen() != null) {
                map.put("day", period.getOpen().getDay().toString());
                map.put("open", String.format(locale, "%02d", period.getOpen().getTime().getHours())
                        + ":" + String.format(locale, "%02d", period.getOpen().getTime().getMinutes()));
            }
            if (period.getClose() != null) {
                map.put("close", String.format(locale, "%02d", period.getClose().getTime().getHours())
                        + ":" + String.format(locale, "%02d", period.getClose().getTime().getMinutes()));
            }
            hours.add(map);
        }
        return hours;
    }
}
