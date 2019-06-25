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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.adapters.RestaurantAdapter;
import com.benlefevre.go4lunch.adapters.WorkmateAdapter;
import com.benlefevre.go4lunch.api.RestaurantHelper;
import com.benlefevre.go4lunch.api.UserHelper;
import com.benlefevre.go4lunch.controllers.activities.RestaurantActivity;
import com.benlefevre.go4lunch.models.Restaurant;
import com.benlefevre.go4lunch.models.User;
import com.benlefevre.go4lunch.views.RestaurantViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.benlefevre.go4lunch.utils.Constants.ID_LIST;
import static com.benlefevre.go4lunch.utils.Constants.ORIGIN;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_ACTIVITY;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_ID;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_NAME;
import static com.benlefevre.go4lunch.utils.Constants.WORKMATES;

public class RecyclerViewFragment extends Fragment {


    @BindView(R.id.recycler_fragment_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.recycler_fragment_swipe_layout)
    SwipeRefreshLayout mSwipeLayout;

    private Activity mActivity;
    private String origin;
    private List<String> mIdList;
    private String mRestaurantName;
    private String mRestaurantId;
    private List<Restaurant> mRestaurantList;
    private RestaurantAdapter mRestaurantAdapter;

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

    public static RecyclerViewFragment newInstance(String origin, String restaurantId) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(ORIGIN, origin);
        args.putString(RESTAURANT_ID, restaurantId);
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
        configureSwipeRefreshLayout();
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
                if (getArguments() != null)
                    mIdList = getArguments().getStringArrayList(ID_LIST);
                if (mIdList != null)
                    fetchRestaurantInFirestore(mIdList);
                break;
            case WORKMATES:
                configureRecyclerViewForWorkmates();
                break;
            case RESTAURANT_ACTIVITY:
                if (getArguments() != null) {
                    mRestaurantId = getArguments().getString(RESTAURANT_ID);
                    configureRecyclerViewForActivityRestaurant();
                }
                break;
        }
    }

    /**
     * Sets a FirestoreRecyclerOptions to configure the WorkmateAdapter to bind all changes into
     * the requested collection.
     */
    private void configureRecyclerViewForActivityRestaurant() {
        Query query = UserHelper.getUsersCollection().whereEqualTo("restaurantId", mRestaurantId);
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
        WorkmateAdapter workmateAdapter = new WorkmateAdapter(options);
        mRecyclerView.setAdapter(workmateAdapter);
    }


    /**
     * Sets a FirestoreRecyclerOptions to configure the WorkmateAdapter to bind all changes into
     * the requested collection.
     * Sets an OnItemClickListener to start RestaurantActivity if condition is true.
     */
    private void configureRecyclerViewForWorkmates() {
        Query query = UserHelper.getUsersCollection().orderBy("displayName", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
        WorkmateAdapter workmateAdapter = new WorkmateAdapter(options);
        mRecyclerView.setAdapter(workmateAdapter);
        workmateAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null && user.getRestaurantName() != null) {
                Intent intent = new Intent(mActivity, RestaurantActivity.class);
                intent.putExtra(RESTAURANT_NAME, user.getRestaurantName());
                intent.putExtra(RESTAURANT_ID,user.getRestaurantId());
                startActivity(intent);
            }
        });
    }

    /**
     * Fetches Restaurants in firestore according the restaurant'id and adds them in a List.
     * Calls configureRecyclerViewForRestaurant when all restaurants are fetched.
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
                    configureRecyclerViewForRestaurants(mRestaurantList);
                }
            });
        }
    }

    /**
     * Configures the RecyclerView with a  RestaurantAdapter and sets an ItemClickListener and it's action.
     */
    private void configureRecyclerViewForRestaurants(List<Restaurant> restaurants) {
        mRestaurantAdapter = new RestaurantAdapter(restaurants);
        mRestaurantAdapter.setOnItemClickListener(v -> {
            RestaurantViewHolder holder = (RestaurantViewHolder) v.getTag();
            int position = holder.getAdapterPosition();
            Intent intent = new Intent(mActivity, RestaurantActivity.class);
            intent.putExtra(RESTAURANT_NAME, restaurants.get(position).getName());
            intent.putExtra(RESTAURANT_ID,restaurants.get(position).getUid());
            startActivity(intent);
        });
        mRecyclerView.setAdapter(mRestaurantAdapter);
        mRestaurantAdapter.notifyDataSetChanged();
        mSwipeLayout.setRefreshing(false);
    }

    /**
     * Displays only the selected restaurant in Autocomplete widget into the RecyclerView;
     *
     * @param restaurantName the selected restaurant's Id to fetch it into mRestaurantList.
     */
    public void showSelectedRestaurant(String restaurantName) {
        List<Restaurant> restaurants = new ArrayList<>();
        for (Restaurant restaurant : mRestaurantList) {
            if (restaurant.getName().equals(restaurantName)) {
                restaurants.add(restaurant);
                configureRecyclerViewForRestaurants(restaurants);
            }
        }
    }

    /**
     * Defines the bahavior of mSwipeLayout according the fragment's origin.
     */
    private void configureSwipeRefreshLayout(){
        mSwipeLayout.setOnRefreshListener(() -> {
            if (origin.equals(RESTAURANT))
                fetchRestaurantInFirestore(mIdList);
            else
                mSwipeLayout.setRefreshing(false);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        Calls notifyDataSetChanged on the RestaurantAdapter when the user press the back button in
//        RestaurantActivity if he came in from RestaurantFragment.
        if (mRestaurantAdapter != null) {
            mRestaurantAdapter.notifyDataSetChanged();
        }
    }
}
