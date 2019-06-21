package com.benlefevre.go4lunch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.models.AutoCompleteItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<AutoCompleteItem> {


    public AutoCompleteAdapter(Context context, List<AutoCompleteItem> objects) {
        super(context, 0, objects);
    }

    /**
     * Defines how autoCompleteItem's data are bind in the dropdown list.
     */
    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.auto_complete_item,parent,false);

        TextView name = convertView.findViewById(R.id.auto_complete_name_txt);
        TextView address = convertView.findViewById(R.id.auto_complete_address_txt);

        AutoCompleteItem autoCompleteItem = getItem(position);

        if (autoCompleteItem != null){
            name.setText(autoCompleteItem.getName());
            address.setText(autoCompleteItem.getAddress());
        }
        return convertView;
    }
}
