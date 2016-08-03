package com.tny.khondedriver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Thitiphat on 10/28/2015.
 */
public class JobsAdapter extends ArrayAdapter<Job> {
    public JobsAdapter(Context context, ArrayList<Job> jobs) {
        super(context, 0, jobs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Job job = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_job, parent, false);
        }
        // Lookup view for data population
        TextView tvCity = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvProduct = (TextView) convertView.findViewById(R.id.tvFromTo);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
        TextView tvWeight = (TextView) convertView.findViewById(R.id.tvWeight);
        TextView tvPickup = (TextView) convertView.findViewById(R.id.tvPickup);
        TextView tvDropoff = (TextView) convertView.findViewById(R.id.tvDropoff);

        // Populate the data into the template view using the data object
        tvCity.setText(job.fromToCity);
        tvProduct.setText(job.product);
        tvPrice.setText(job.current_price);
        tvWeight.setText(job.weightCBM);
        tvPickup.setText(job.pickup_datetime);
        tvDropoff.setText(job.dropoff_datetime);


        // Return the completed view to render on screen
        return convertView;
    }
}