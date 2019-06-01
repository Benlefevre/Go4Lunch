package com.benlefevre.go4lunch;

import android.content.Context;
import android.widget.ImageView;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.benlefevre.go4lunch.utils.UtilsRestaurant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class UtilsRestaurantTest {

    private Context mContext;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private double rating;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        star1 = new ImageView(mContext);
        star2 = new ImageView(mContext);
        star3 = new ImageView(mContext);
        rating = 0.0;
    }

    @Test
    public void updateRating(){
        rating = 0.83;
        UtilsRestaurant.updateUiAccordingToRating(rating,star1,star2,star3);
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_half_24dp).getConstantState(),star1.getDrawable().getConstantState());
        assertEquals(ViewMatchers.Visibility.GONE.getValue(),star2.getVisibility());
        assertEquals(ViewMatchers.Visibility.GONE.getValue(),star3.getVisibility());

        rating = 1;
        UtilsRestaurant.updateUiAccordingToRating(rating,star1,star2,star3);
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_24dp).getConstantState(),star1.getDrawable().getConstantState());
        assertEquals(ViewMatchers.Visibility.GONE.getValue(),star2.getVisibility());
        assertEquals(ViewMatchers.Visibility.GONE.getValue(),star3.getVisibility());

        rating = 1.83;
        UtilsRestaurant.updateUiAccordingToRating(rating, star1, star2, star3);
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_24dp).getConstantState(), star2.getDrawable().getConstantState());
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_half_24dp).getConstantState(), star1.getDrawable().getConstantState());
        assertEquals(ViewMatchers.Visibility.GONE.getValue(), star3.getVisibility());

        rating = 2.66;
        UtilsRestaurant.updateUiAccordingToRating(rating, star1, star2, star3);
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_24dp).getConstantState(), star2.getDrawable().getConstantState());
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_24dp).getConstantState(), star1.getDrawable().getConstantState());
        assertEquals(ViewMatchers.Visibility.GONE.getValue(), star3.getVisibility());

        rating = 3.50;
        UtilsRestaurant.updateUiAccordingToRating(rating, star1, star2, star3);
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_24dp).getConstantState(), star3.getDrawable().getConstantState());
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_24dp).getConstantState(), star2.getDrawable().getConstantState());
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_half_24dp).getConstantState(), star1.getDrawable().getConstantState());

        rating = 4.33;
        UtilsRestaurant.updateUiAccordingToRating(rating, star1, star2, star3);
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_24dp).getConstantState(), star3.getDrawable().getConstantState());
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_24dp).getConstantState(), star2.getDrawable().getConstantState());
        assertEquals(mContext.getResources().getDrawable(R.drawable.ic_star_24dp).getConstantState(), star3.getDrawable().getConstantState());

    }
}
