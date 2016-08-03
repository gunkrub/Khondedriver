package com.tny.khondedriver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;

public class register_tab2 extends Fragment {

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =inflater.inflate(R.layout.fragment_register_tab2, container, false);

        ((TextView)v.findViewById(R.id.truck_type_label)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String truck_type = ((TextView)v.findViewById(R.id.truck_type_label)).getText().toString();
                v.findViewById(R.id.roof_type_layout).setVisibility(View.VISIBLE);
                v.findViewById(R.id.extras_layout).setVisibility(View.GONE);

                if(truck_type.equals("รถกระบะ") || truck_type.equals("รถบรรทุก4ล้อใหญ่")){
                        ((TextView) v.findViewById(R.id.roof_type_label0)).setText("ชนิดตัวถัง");
                        ((TextView) v.findViewById(R.id.roof_type_label)).setText("เลือกชนิดตัวถัง");

                    RegisterActivity.truck_type_group = 1;
                }else if(truck_type.equals("รถเทรลเลอร์2เพลา") || truck_type.equals("รถเทรลเลอร์3เพลา")){
                        ((TextView) v.findViewById(R.id.roof_type_label0)).setText("ชนิดหาง");
                        ((TextView) v.findViewById(R.id.roof_type_label)).setText("เลือกชนิดหาง");

                    RegisterActivity.truck_type_group=3;
                } else {
                        ((TextView) v.findViewById(R.id.roof_type_label0)).setText("ชนิดตัวถัง");
                        ((TextView) v.findViewById(R.id.roof_type_label)).setText("เลือกชนิดตัวถัง");

                    RegisterActivity.isAttachedTruck = true;
                    if(!truck_type.equals("รถบรรทุก10ล้อพ่วง")){
                        v.findViewById(R.id.trunk_width_layout).setVisibility(View.VISIBLE);
                        RegisterActivity.isAttachedTruck = false;

                    } else {
                        v.findViewById(R.id.trunk_width_layout).setVisibility(View.GONE);
                    }
                    RegisterActivity.truck_type_group =2;

                }
            }
        });
        return v;
    }




}
