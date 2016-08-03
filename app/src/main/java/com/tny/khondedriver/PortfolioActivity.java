package com.tny.khondedriver;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class PortfolioActivity extends AppCompatActivity {
    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading..");
        loadingDialog.setCancelable(true);


        new AsyncTask<String, Void, Boolean>() {
            String errorMsg = "";
            JSONArray month_jobs = new JSONArray();
            String this_week="",this_month="",this_year="",month_cost;
            protected void onPreExecute() {
                loadingDialog.show();
            };

            @Override
            protected Boolean doInBackground(String... params) {
                BufferedReader reader;
                StringBuilder buffer = new StringBuilder();
                String line;

                try {

                    URL u = new URL(getString(R.string.website_url) + "portfolio.php?tel=" + MainActivity.tel);

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
                        month_jobs = return1.getJSONArray("month_jobs");
                        this_month = return1.getString("this_month");
                        this_year = return1.getString("this_year");
                        this_week = return1.getString("this_week");
                        month_cost = return1.getString("month_cost");

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

                if(!errorMsg.equals("")){
                    makeToast(errorMsg);
                } else {
                    ((TextView)findViewById(R.id.portfolio_textview_this_week)).setText(this_week);
                    ((TextView)findViewById(R.id.portfolio_textview_this_month)).setText(this_month);
                    ((TextView)findViewById(R.id.portfolio_textview_this_year)).setText(this_year);
                    ((TextView)findViewById(R.id.portfolio_textview_month_cost)).setText("฿ " + month_cost);
                }
                loadingDialog.hide();
            }
        }.execute();
    }
    public void makeToast(String msg){

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
