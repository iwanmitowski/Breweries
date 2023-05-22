package com.example.breweries.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.breweries.R;

import java.util.List;

public class BreweryAdapter extends ArrayAdapter<Brewery> {
    public BreweryAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Brewery> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Brewery dataModel = getItem(position);

        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater
                .from(getContext())
                .inflate(
                    com.example.breweries.R.layout.activity_list_view_fragment,
                    parent,
                    false);
        }

        TextView textViewId = currentItemView.findViewById(R.id.textViewId);
        TextView textViewName = currentItemView.findViewById(R.id.textViewName);
        TextView textViewCity = currentItemView.findViewById(R.id.textViewCity);
        TextView textViewCountry = currentItemView.findViewById(R.id.textViewCountry);
        TextView textViewLatitude = currentItemView.findViewById(R.id.textViewLat);
        TextView textViewLongitude = currentItemView.findViewById(R.id.textViewLong);

        textViewId.setText(dataModel.getId());
        textViewName.setText(dataModel.getName());
        textViewCity.setText(dataModel.getCity());
        textViewCountry.setText(dataModel.getCountry());
        textViewLatitude.setText(dataModel.getLatitude().toString());
        textViewLongitude.setText(dataModel.getLongitude().toString());

        return currentItemView;
    }
}