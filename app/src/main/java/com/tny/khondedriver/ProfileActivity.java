package com.tny.khondedriver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private SimpleAdapter sa;
    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView headerLabel = (TextView)findViewById(R.id.header_label);
        headerLabel.setText("ตัวฉัน");

        SharedPreferences preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        ((TextView)findViewById(R.id.name_label)).setText(preferences.getString("driver_name", ""));
        ((TextView)findViewById(R.id.tel_label)).setText(preferences.getString("tel", ""));
        ((TextView)findViewById(R.id.license_label)).setText(preferences.getString("license", "") + " " + preferences.getString("license_province", ""));

        ((TextView)findViewById(R.id.id_label)).setText(preferences.getString("id_no", ""));
        ((TextView)findViewById(R.id.driver_id_label)).setText(preferences.getString("driver_no", ""));

        int rating = preferences.getInt("rating",0);
        int rating_count = preferences.getInt("rating_count",0);
        if(rating>0)
            findViewById(R.id.star1).setVisibility(View.VISIBLE);
        if(rating>1)
            findViewById(R.id.star2).setVisibility(View.VISIBLE);
        if(rating>2)
            findViewById(R.id.star3).setVisibility(View.VISIBLE);
        if(rating>3)
            findViewById(R.id.star4).setVisibility(View.VISIBLE);
        if(rating>4)
            findViewById(R.id.star5).setVisibility(View.VISIBLE);

        if(rating==0 && rating_count==0){
            findViewById(R.id.star1).setVisibility(View.VISIBLE);
            findViewById(R.id.star2).setVisibility(View.VISIBLE);
            findViewById(R.id.star3).setVisibility(View.VISIBLE);
            findViewById(R.id.star4).setVisibility(View.VISIBLE);
            findViewById(R.id.star5).setVisibility(View.VISIBLE);
        }
/*
        String[][] data1=
                {
                        {"test1","11"},
                        {"test2","22"}
                };
        HashMap<String,String> item;
        for(int i=0;i<data1.length;i++){
            item = new HashMap<String,String>();
            item.put( "line1", data1[i][0]);
            item.put( "line2", data1[i][1]);
            list.add( item );
        }
        sa = new SimpleAdapter(this, list,
                android.R.layout.simple_list_item_2 ,
                new String[] { "line1","line2" },
                new int[] {findViewById(R.id.tvTitle).getId(), findViewById(R.id.tvInfo).getId()});
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(sa);
        */
        //((ImageView) findViewById(R.id.imageView6)).setImageBitmap(MainActivity.profilePic);
        Picasso.with(this).load(getString(R.string.website_url) +  "get_document_pic.php?type_id=1&tel=" + MainActivity.tel).into((ImageView) findViewById(R.id.imageView6));

    }


}
