package com.benlefevre.go4lunch.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.models.Restaurant;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        StringBuilder addressBuilder = new StringBuilder();
        for (AddressComponent addressComponent : addressComponents) {
            if (addressComponent.getTypes().get(0).contains("street_number"))
                addressBuilder.append(addressComponent.getName()).append(" ");
            if (addressComponent.getTypes().get(0).contains("route"))
                addressBuilder.append(addressComponent.getName());
        }
        String address = addressBuilder.toString();
        if (address.isEmpty() && formattedAddress == null)
            address = "No place's address found";
        else if (address.isEmpty())
            address = formattedAddress.substring(0, formattedAddress.indexOf(","));
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

    /**
     * Displays a string indicating if the restaurant is open or close.
     *
     * @param restaurant The selected restaurant
     * @param context    the context is need to fetch the string resources
     * @return the opening hours information according to the current day.
     */
    public static String displayOpeningHours(Restaurant restaurant, Context context) {
        if (restaurant.getOpeningHours() == null)
            return context.getString(R.string.no_info_hours);

        int day = getDay();
        String closeHour = restaurant.getOpeningHours().get(day).get("close");
        String openHour = restaurant.getOpeningHours().get(day).get("open");
        Calendar now = Calendar.getInstance();
        Calendar close = Calendar.getInstance();
        if (closeHour != null) {
            close.set(Calendar.HOUR_OF_DAY, Integer.parseInt(closeHour.substring(0, closeHour.indexOf(":"))));
            close.set(Calendar.MINUTE, Integer.parseInt(closeHour.substring(closeHour.indexOf(":") + 1)));
        }
        Calendar open = Calendar.getInstance();
        if (openHour != null) {
            open.set(Calendar.HOUR_OF_DAY, Integer.parseInt(openHour.substring(0, openHour.indexOf(":"))));
            open.set(Calendar.MINUTE, Integer.parseInt(openHour.substring(openHour.indexOf(":") + 1)));
        }
        if (close.before(open))
            close.add(Calendar.DAY_OF_YEAR, 1);

        boolean isOpen = now.before(close) && now.after(open);
        long beforeClose = 30 * 60 * 1000;
        if (isOpen) {
            if (close.getTimeInMillis() - now.getTimeInMillis() < beforeClose)
                return context.getString(R.string.closing_soon);
            else {
                if (Locale.getDefault().getLanguage().equals("en")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    return context.getString(R.string.open_until) + " " + simpleDateFormat.format(close.getTime());
                } else
                    return context.getString(R.string.open_until) + closeHour;
            }
        } else
            return context.getString(R.string.closed);
    }

    /**
     * Convert the day of week into an int.
     *
     * @return an int corresponding to the restaurant's openingHours keys.
     */
    private static int getDay() {
        Date date = new Date();
        String formattedDate;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E", Locale.US);
        formattedDate = simpleDateFormat.format(date);
        switch (formattedDate) {
            case "Sun":
            case "Dim":
                return 0;
            case "Mon":
            case "Lun":
                return 1;
            case "Tue":
            case "Mar":
                return 2;
            case "Wed":
            case "Mer":
                return 3;
            case "Thu":
            case "Jeu":
                return 4;
            case "Fri":
            case "Ven":
                return 5;
            case "Sat":
            case "Sam":
                return 6;
            default:
                return 7;
        }
    }

    /**
     * Sets the ImageView's image resources according to the rating's value.
     *
     * @param rating the restaurant's rating returned by GoogleMaps server
     * @param star1  the first ImageView
     * @param star2  the second ImageView
     * @param star3  the third ImageView
     */
    public static void updateUiAccordingToRating(double rating, ImageView star1, ImageView star2, ImageView star3) {
        rating /= 5.0d;
        rating *= 3.0d;
        if (rating > 0 && rating <= 0.5d) {
            star1.setImageResource(R.drawable.ic_star_half_24dp);
            star2.setVisibility(View.GONE);
            star3.setVisibility(View.GONE);
        } else if (rating > 0.5d && rating <= 1.0d) {
            star1.setImageResource(R.drawable.ic_star_24dp);
            star2.setVisibility(View.GONE);
            star3.setVisibility(View.GONE);
        } else if (rating > 1.0d && rating <= 1.5d) {
            star2.setImageResource(R.drawable.ic_star_24dp);
            star1.setImageResource(R.drawable.ic_star_half_24dp);
            star3.setVisibility(View.GONE);
        } else if (rating > 1.5d && rating <= 2.0d) {
            star2.setImageResource(R.drawable.ic_star_24dp);
            star1.setImageResource(R.drawable.ic_star_24dp);
            star3.setVisibility(View.GONE);
        } else if (rating > 2.0d && rating <= 2.5d) {
            star3.setImageResource(R.drawable.ic_star_24dp);
            star2.setImageResource(R.drawable.ic_star_24dp);
            star1.setImageResource(R.drawable.ic_star_half_24dp);
        } else {
            star3.setImageResource(R.drawable.ic_star_24dp);
            star2.setImageResource(R.drawable.ic_star_24dp);
            star1.setImageResource(R.drawable.ic_star_24dp);
        }
    }
}
