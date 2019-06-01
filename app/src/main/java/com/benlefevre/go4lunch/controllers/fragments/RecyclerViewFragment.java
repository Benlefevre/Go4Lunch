package com.benlefevre.go4lunch.controllers.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.adapters.RestaurantAdapter;
import com.benlefevre.go4lunch.api.RestaurantHelper;
import com.benlefevre.go4lunch.controllers.activities.RestaurantActivity;
import com.benlefevre.go4lunch.models.Restaurant;
import com.benlefevre.go4lunch.views.RestaurantViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.benlefevre.go4lunch.utils.Constants.ID_LIST;
import static com.benlefevre.go4lunch.utils.Constants.ORIGIN;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerViewFragment extends Fragment {


    @BindView(R.id.recycler_fragment_recyclerview)
    RecyclerView mRecyclerView;

    private Activity mActivity;
    private String origin;
    private List<String> mIdList;
    private List<Restaurant> mRestaurantList;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    public static RecyclerViewFragment newInstance(String origin, List<String> idList) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(ORIGIN, origin);
        args.putStringArrayList(ID_LIST, (ArrayList<String>) idList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        if (getArguments() != null) {
            origin = getArguments().getString(ORIGIN);
        }
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        return view;
    }

    /**
     * Initializes the RecyclerView and calls a method to configure the RecyclerView according to origin.
     */
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mActivity, linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        switch (origin) {
            case RESTAURANT:
                if (getArguments() != null) {
                    mIdList = getArguments().getStringArrayList(ID_LIST);
                    fetchRestaurantInFirestore(mIdList);
                }
                break;
        }
    }

    /**
     * Fetches Restaurants in firestore according the restaurant'id and adds them in a List.
     *
     * @param idList a List of restaurant's id from HomeActivity.
     */
    private void fetchRestaurantInFirestore(List<String> idList) {
        mRestaurantList = new ArrayList<>();
        for (String restaurantId : idList) {
            RestaurantHelper.getRestaurant(restaurantId).addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                    mRestaurantList.add(restaurant);
                }
//               When all restaurant are fetched, we configure the RecyclerView's adapter.
                if (mRestaurantList.size() == mIdList.size()) {
                    configureRecyclerViewForRestaurants();
                }
            });
        }
    }


    /**
     * Configures the RecyclerView with a  RestaurantAdapter and sets an ItemClickListener and it's action.
     */
    private void configureRecyclerViewForRestaurants() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(mRestaurantList);
        restaurantAdapter.setOnItemClickListener(v -> {
            RestaurantViewHolder holder = (RestaurantViewHolder) v.getTag();
            int position = holder.getAdapterPosition();
            Intent intent = new Intent(mActivity, RestaurantActivity.class);
            intent.putExtra(RESTAURANT_NAME, mRestaurantList.get(position).getName());
            startActivity(intent);
        });
        mRecyclerView.setAdapter(restaurantAdapter);
        restaurantAdapter.notifyDataSetChanged();
    }

}
