package com.tny.khondedriver;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

public class AnnouncementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        TextView headerLabel = (TextView)findViewById(R.id.header_label);
        headerLabel.setText("ประกาศ");

        GetAnnouncementTask g1 = new GetAnnouncementTask();
        g1.execute();
    }

    class GetAnnouncementTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        JSONObject announcement;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            try {
                URL u = new URL(getString(R.string.website_url) + "get_announcement.php");
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

                    Log.e("RETURN",buffer.toString());
                    announcement = new JSONObject(buffer.toString());

                    boolean emptyResult = announcement.getBoolean("emptyResult");

                    if(emptyResult) {
                        errorMsg = "ไม่มีประกาศ";
                        return false;
                    }

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
                try {

                    LinearLayout layout = (LinearLayout) findViewById(R.id.announcement_layout);

                    JSONArray announces = announcement.getJSONArray("announcement");

                    /* Announcements                                          */

                    for(int i=0;i<announces.length();i++){
                        final JSONObject announce_detail = announces.getJSONObject(i);

                        View announce = View.inflate(getApplicationContext(), R.layout.announcement_card, layout);

                        TextView tv2 = (TextView) announce.findViewById(R.id.textview_content);
                        tv2.setText(announce_detail.getString("content"));

                        TextView tv1 = (TextView) announce.findViewById(R.id.textview_title);
                        tv1.setText(announce_detail.getString("title"));

                        tv1.setId(View.generateViewId());
                        tv2.setId(View.generateViewId());

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                makeToast(errorMsg);
            }
            findViewById(R.id.annoucementLoading).setVisibility(View.GONE);


        }
    }
    public void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
