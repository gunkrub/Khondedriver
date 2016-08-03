package com.tny.khondedriver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Thitiphat on 10/28/2015.
 */
public class Job {
    public int job_id;
    public String pickup_datetime;
    public String dropoff_datetime;
    public String fromToCity;
    public String weightCBM;
    public String product;
    public String current_price;

    public Job(JSONObject object){
        try {
            this.job_id = object.getInt("job_id");
            this.pickup_datetime = object.getString("pickup_datetime");
            this.dropoff_datetime = object.getString("dropoff_datetime");
            this.fromToCity = object.getString("fromToCity");
            this.weightCBM=object.getString("weightCBM");
            this.product = object.getString("product");
            this.current_price = object.getString("current_price");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Job> fromJson(JSONArray jsonObjects) {
        ArrayList<Job> jobs = new ArrayList<Job>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                jobs.add(new Job(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jobs;
    }

    public static ArrayList<Job> creditHistoryfromJson(JSONArray jsonObjects) {
        ArrayList<Job> jobs = new ArrayList<Job>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                JSONObject converted = new JSONObject();
                JSONObject fromServer = jsonObjects.getJSONObject(i);
                converted.put("job_id", i);

                if(fromServer.getString("title").equals("")) {
                    converted.put("fromToCity", "เติมเงิน");
                    converted.put("product","เติมเงิน " + fromServer.getString("amount") + " บาท");
                }else {
                    converted.put("fromToCity", fromServer.getString("title"));
                    converted.put("product","ค่าธรรมเนียม " + fromServer.getString("amount") + " บาท");

                }

                converted.put("weightCBM","ยอดล่าสุด " + fromServer.getString("last_amount") + " บาท");
                converted.put("pickup_datetime", fromServer.getString("reference_no"));
                converted.put("dropoff_datetime","");
                converted.put("current_price", fromServer.getString("datetime"));

                Job jobConverted = new Job(converted);


                jobs.add(jobConverted);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jobs;
    }
}
