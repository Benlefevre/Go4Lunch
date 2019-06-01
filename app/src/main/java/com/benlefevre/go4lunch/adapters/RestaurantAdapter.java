package com.benlefevre.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.models.Restaurant;
import com.benlefevre.go4lunch.views.RestaurantViewHolder;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private List<Restaurant> mRestaurants;

    public RestaurantAdapter(List<Restaurant> restaurants) {
        mRestaurants = restaurants;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.restaurant_item,parent,false);
        RestaurantViewHolder holder = new RestaurantViewHolder(view,context);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.updateUi(mRestaurants.get(position));
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }
}
