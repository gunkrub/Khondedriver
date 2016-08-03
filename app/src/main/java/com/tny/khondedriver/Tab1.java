package com.tny.khondedriver;

/**
 * Created by Thitiphat on 9/17/2015.
 */
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab1 extends Fragment {

    TextView driver_name1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1, container, false);

        driver_name1 = (TextView)v.findViewById(R.id.driver_name);

        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("UserInfo", getActivity().getApplicationContext().MODE_PRIVATE);
        ((TextView)v.findViewById(R.id.driver_name)).setText(preferences.getString("driver_name", ""));
        ((TextView)v.findViewById(R.id.tel_no)).setText(preferences.getString("tel", ""));
        //((TextView)v.findViewById(R.id.license_plate)).setText(preferences.getString("license", "") + " " + preferences.getString("license_province", ""));

        ((TextView)v.findViewById(R.id.credit_left_label)).setText("ยอดเงินคงเหลือ " + preferences.getInt("credit",0) + " บาท");
        ((TextView)v.findViewById(R.id.no_review)).setText("(" + preferences.getInt("rating_count",0) +")");

        int rating = preferences.getInt("rating",0);
        int rating_count = preferences.getInt("rating_count",0);
        if(rating>0)
            v.findViewById(R.id.star1).setVisibility(v.VISIBLE);
        if(rating>1)
            v.findViewById(R.id.star2).setVisibility(v.VISIBLE);
        if(rating>2)
            v.findViewById(R.id.star3).setVisibility(v.VISIBLE);
        if(rating>3)
            v.findViewById(R.id.star4).setVisibility(v.VISIBLE);
        if(rating>4)
            v.findViewById(R.id.star5).setVisibility(v.VISIBLE);

        if(rating==0 && rating_count==0){
            v.findViewById(R.id.star1).setVisibility(v.VISIBLE);
            v.findViewById(R.id.star2).setVisibility(v.VISIBLE);
            v.findViewById(R.id.star3).setVisibility(v.VISIBLE);
            v.findViewById(R.id.star4).setVisibility(v.VISIBLE);
            v.findViewById(R.id.star5).setVisibility(v.VISIBLE);
        }
        return v;
    }

    public void setDriver_name(String name){
        if(driver_name1!=null) {
            driver_name1.setText(name);
        }
    }






}