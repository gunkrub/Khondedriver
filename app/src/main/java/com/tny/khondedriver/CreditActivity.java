package com.tny.khondedriver;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CreditActivity extends AppCompatActivity {
    JobsAdapter adapter;
    DecimalFormat formatter = new DecimalFormat("#,###,###");

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        TextView headerLabel = (TextView)findViewById(R.id.header_label);
        headerLabel.setText("การเงิน");

        ArrayList<Job> creditHistory = new ArrayList<Job>();
        adapter = new JobsAdapter(this,creditHistory);

        ListView listView = (ListView)findViewById(R.id.creditListView);
        listView.setAdapter(adapter);

        GetCreditTask g1 = new GetCreditTask();
        g1.execute();
    }

    public class GetCreditTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        JSONArray array1 = new JSONArray();
        String credit_left="";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;

            try {

                URL u = new URL(getString(R.string.website_url) + "credit_history.php?tel=" + MainActivity.tel);

                HttpURLConnection h = (HttpURLConnection)u.openConnection();
                h.setRequestMethod("GET");
                h.setDoOutput(true);

                h.connect();


                int response = h.getResponseCode();
                if (response == 200) {
                    reader = new BufferedReader(new InputStreamReader(h.getInputStream(),"UTF-8"));
                    while((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    JSONObject return1 = new JSONObject(buffer.toString());
                    array1 = return1.getJSONArray("credit_history");
                    credit_left = return1.getString("credit");

                    return true;

                }
                else {
                    Log.e("", "HTTP Error");
                }
            } catch (MalformedURLException e) {
                Log.e("", "URL Error");
            } catch (IOException e) {
                Log.e("", "กรุณาเชื่อมต่ออินเตอร์เน็ต");
                errorMsg = "กรุณาเชื่อมต่ออินเตอร์เน็ต";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {
                adapter.clear();
                ArrayList<Job> newHistory = Job.creditHistoryfromJson(array1);
                adapter.addAll(newHistory);

                TextView subHeaderLabel = (TextView)findViewById(R.id.sub_header_label);
                subHeaderLabel.setText("เงินคงเหลือ "+credit_left+" บาท");
            } else {
                if(!errorMsg.equals(""))
                    makeToast(errorMsg);
            }

                findViewById(R.id.creditHistoryLoading).setVisibility(View.GONE);

        }

    }
    public void makeToast(String msg){

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
