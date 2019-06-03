package com.benlefevre.go4lunch.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.workmate_item_user_name_txt)
    TextView mUserName;
    @BindView(R.id.workmate_item_user_photo)
    ImageView mUserPhoto;

    private Context mContext;

    public WorkmateViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mContext = context;
        ButterKnife.bind(this, itemView);
        itemView.setTag(this);
    }

    public void updateUi(User user) {
        if (user.getUrlPhoto() != null)
            Glide.with(mContext).load(user.getUrlPhoto()).apply(RequestOptions.circleCropTransform()).into(mUserPhoto);
        else
            Glide.with(mContext).load(mContext.getString(R.string.default_profil_phot_url)).apply(RequestOptions.circleCropTransform()).into(mUserPhoto);

        if (user.getRestaurantName() != null) {
            mUserName.setText(mContext.getString(R.string.chose, user.getDisplayName(), user.getRestaurantName()));
            mUserName.setTextColor(mContext.getResources().getColor(R.color.black));
        } else {
            mUserName.setText(mContext.getString(R.string.no_decided, user.getDisplayName()));
            mUserName.setTextColor(mContext.getResources().getColor(R.color.greyText));
        }
    }
}
