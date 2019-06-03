package com.benlefevre.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.controllers.activities.HomeActivity;
import com.benlefevre.go4lunch.models.User;
import com.benlefevre.go4lunch.views.WorkmateViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class WorkmateAdapter extends FirestoreRecyclerAdapter<User, WorkmateViewHolder> {

    private OnItemClickListener mOnClickListener;

    public WorkmateAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmateViewHolder workmateViewHolder, int i, @NonNull User user) {
        workmateViewHolder.updateUi(user);
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.workmate_item, parent, false);
        WorkmateViewHolder holder = new WorkmateViewHolder(view, context);
        if (context instanceof HomeActivity) {
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                mOnClickListener.onItemClick(getSnapshots().getSnapshot(position), position);
            });
        }
        return holder;
    }

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

}
