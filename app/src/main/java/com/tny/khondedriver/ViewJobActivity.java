package com.tny.khondedriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

public class ViewJobActivity extends AppCompatActivity {
    ArrayList<String> gps_coor = new ArrayList<String>();
    ArrayList<String> address = new ArrayList<String>();
    ArrayList<String> datetime = new ArrayList<String>();
    ArrayList<Integer> contact_view = new ArrayList<Integer>();
    ArrayList<String> toSignLocId = new ArrayList<String>();
    ArrayList<String> toSignAddress = new ArrayList<String>();
    ArrayList<Integer> toSignChecked = new ArrayList<Integer>();
    DecimalFormat formatter = new DecimalFormat("#,###,###");

    ProgressDialog loadingDialog;
    JSONObject job_info;
    boolean bid_is_ended = false;
    int countdown_hr=0,countdown_min=0,countdown_sec=0,job_id=0,current_bid=0,job_index;
    String tel;

    @Override
    public void onResume(){super.onResume();    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job);

        SharedPreferences preferences = this.getSharedPreferences("UserInfo", this.MODE_PRIVATE);
        tel=preferences.getString("tel", "");

        Intent intent = getIntent();

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading..");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        GetJobInfoTask a = new GetJobInfoTask();
        job_id = intent.getIntExtra("job_id", 0);
        job_index = intent.getIntExtra("job_index",0);
        a.execute("" + job_id);


/*
        LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
        int i;
        for(i=1;i<=3;i++) {
            View starting_point = View.inflate(getApplicationContext(), R.layout.job_starting_point, layout);
            TextView tv1 = (TextView) starting_point.findViewById(R.id.starting_123);
            tv1.setText("A" + i);
            tv1.setId(tv1.getId() + i);

            View the_layout = starting_point.findViewById(R.id.the_layout);
            the_layout.setTag(i - 1);
            the_layout.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            String uri = String.format(Locale.ENGLISH, "geo:" + gps_coor[(int) v.getTag()] + "?q=" + gps_coor[(int) v.getTag()]);

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        }
                    }
            );
            the_layout.setId(the_layout.getId() + i*100);


        }

*/
        /*
        WebView mWebView;
        mWebView=(WebView)findViewById(R.id.webView);
        mWebView.getSettings();
        mWebView.setBackgroundColor(Color.parseColor("#86A1BF"));

        mWebView.loadUrl("http://www.thitiphat.net/Khondee/view_job.php");
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mWebView.setHapticFeedbackEnabled(false);
        */
    }

    public void updateCurrentBid(){
        if(!bid_is_ended) {
            GetCurrentBid a = new GetCurrentBid();
            a.execute();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_job, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class GetJobInfoTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            try {
                URL u = new URL(getString(R.string.website_url) + "view_job.php?job_id=" + params[0]);
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

                    job_info = new JSONObject(buffer.toString());

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
                    LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);

                    //((TextView)findViewById(R.id.header_label)).setText(job_info.getString("fromToCity"));
                    ((TextView)findViewById(R.id.header_label_right)).setText(job_info.getString("job_no"));

                    /* Details of job                                          */
                    JSONObject details_info = job_info;

                    View details_layout = View.inflate(getApplicationContext(), R.layout.job_details, layout);
                    ((TextView) details_layout.findViewById(R.id.details_product)).setText(details_info.getString("product"));
                    ((TextView) details_layout.findViewById(R.id.details_truck_type)).setText(details_info.getString("truck_type"));
                    ((TextView) details_layout.findViewById(R.id.details_details)).setText(details_info.getString("details"));
                    //((TextView) details_layout.findViewById(R.id.details_weight)).setText(details_info.getString("weight") + " ตัน");
                    //((TextView) details_layout.findViewById(R.id.details_cbm)).setText(details_info.getString("cbm") + " ลบ.ม.");

                    View details_layout2 = details_layout.findViewById(R.id.details_layout);
                    details_layout2.setOnClickListener(
                            new View.OnClickListener() {
                                public void onClick(View v) {
                                    showMaps("");
                                }
                            }
                    );

                    /* Destinations                                          */

                    JSONArray destinations = job_info.getJSONArray("locations");
                    for(int i=0;i<destinations.length();i++){
                        final JSONObject destination = destinations.getJSONObject(i);

                        View dests = View.inflate(getApplicationContext(), R.layout.job_destination, layout);
                        TextView tv1 = (TextView) dests.findViewById(R.id.destination_datetime);

                        tv1.setId(View.generateViewId());

                        TextView tv2 = (TextView) dests.findViewById(R.id.destination_address);
                        tv2.setText(destination.getString("address"));
                        address.add(destination.getString("address"));

                        tv2.setId(View.generateViewId());

                        View dest_layout = dests.findViewById(R.id.dest_layout);

                        gps_coor.add(destination.getString("gps_coor"));

                        int checkedViewId = View.generateViewId();
                        ((ImageView)dest_layout.findViewById(R.id.checked_img2)).setId(checkedViewId);

                        if(destination.getString("status").equals("0")){
                            toSignAddress.add(destination.getString("address"));
                            toSignLocId.add(destination.getString("loc_id"));
                            ((ImageView)dest_layout.findViewById(checkedViewId)).setVisibility(View.GONE);
                            toSignChecked.add(checkedViewId);
                        }

                        dest_layout.setOnClickListener(
                                new View.OnClickListener() {
                                    public void onClick(View v) {
                                        /*
                                        String uri = null;
                                        try {
                                            uri = String.format(Locale.ENGLISH, "geo:" + destination.getString("gps_coor") + "?q=" + destination.getString("gps_coor"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getApplicationContext().startActivity(intent);
                                        */
                                        try {
                                            showMaps(destination.getString("gps_coor"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        );

                        dest_layout.setId(View.generateViewId());

                        View.inflate(getApplicationContext(), R.layout.job_contact, layout);
                        RelativeLayout contact0 = (RelativeLayout) findViewById(R.id.contact_layout);
                        ((RelativeLayout)contact0.findViewById(R.id.contact_layout_bg)).setBackground(getDrawable(R.drawable.job_contact_red));
                        ((TextView)contact0.findViewById(R.id.contact_textview)).setText("ติดต่อคุณ " + destination.getString("name") + " " + destination.getString("tel"));

                        contact0.setOnClickListener(
                                new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        try {
                                            intent.setData(Uri.parse("tel:" + destination.getString("tel")));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(intent);
                                    }
                                }
                        );

                        contact0.setVisibility(View.GONE);
                        contact0.setId(View.generateViewId());
                        contact_view.add(contact0.getId());

                    }

                    /*         Current Bid           */

                    View.inflate(getApplicationContext(), R.layout.job_current_bid, layout);
                    String countdowns[] = job_info.getString("end_in").split(":");
                    countdown_hr = Integer.parseInt(countdowns[0]);
                    countdown_min = Integer.parseInt(countdowns[1]);
                    countdown_sec = Integer.parseInt(countdowns[2]);

                    View.inflate(getApplicationContext(), R.layout.job_end_price, layout);
                    findViewById(R.id.end_price_layout).setVisibility(View.GONE);

                    countDown();
                    updateCurrentBid();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                makeToast(errorMsg);
            }

        }
    }
    class GetCurrentBid extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        JSONObject fetch_info;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            try {
                URL u = new URL(getString(R.string.website_url) + "view_job.php?only_bid&job_id=" + job_id);
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

                    fetch_info = new JSONObject(buffer.toString());
                    return true;

                }
                else {
                    Log.e("", "HTTP Error");
                }
            } catch (MalformedURLException e) {
                Log.e("", "URL Error");
            } catch (IOException e) {
                Log.e("", "กรุณาเชื่อมต่ออินเตอร์เน็ต");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {

                try {
                    String countdowns[];
                    countdowns = fetch_info.getString("end_in").split(":");

                    if(countdown_hr!=Integer.parseInt(countdowns[0]) || countdown_min!=Integer.parseInt(countdowns[1]) || Math.abs(Integer.parseInt(countdowns[2]) - countdown_sec) >=3) {
                        countdown_hr = Integer.parseInt(countdowns[0]);
                        countdown_min = Integer.parseInt(countdowns[1]);
                        countdown_sec = Integer.parseInt(countdowns[2]);
                        ((TextView) findViewById(R.id.ending_countdown)).setText(fetch_info.getString("end_in"));
                    }

                    current_bid = Integer.parseInt(fetch_info.getString("current_bid"));
                    ((TextView) findViewById(R.id.current_bid)).setText(formatter.format(current_bid));
                    ((TextView) findViewById(R.id.end_price)).setText(formatter.format(current_bid) + " บาท");

                    job_info.put("user_tel",fetch_info.getString("user_tel"));

                    if(fetch_info.getString("user_tel").equals(tel)){
                        findViewById(R.id.current_bid).setBackgroundColor(Color.parseColor("#6cb13b"));
                    } else {
                        findViewById(R.id.current_bid).setBackgroundColor(Color.parseColor("#ed6b66"));
                    }

                    ((RelativeLayout)findViewById(R.id.newbid_layout)).setVisibility(View.VISIBLE);
                    if(countdown_hr+countdown_min+countdown_sec==0) {
                        bid_ended();

                    } else{
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        updateCurrentBid();
                                    }
                                },
                                3000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                makeToast(errorMsg);
            }
            if(loadingDialog!=null)
            loadingDialog.dismiss();

        }
    }
    class PlacebidTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            try {
                URL u = new URL(getString(R.string.website_url) + "place_bid.php?job_id=" + job_id + "&tel=" + tel + "&bid=" + params[0]);
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

                    errorMsg = "";

                    JSONObject returnMsg = new JSONObject(buffer.toString());
                    errorMsg = returnMsg.getString("errorMsg");

                    if(errorMsg.equals(""))
                        current_bid = Integer.parseInt(params[0]);

                    return true;

                }
                else {
                    Log.e("", "HTTP Error");
                }
            } catch (MalformedURLException e) {
                Log.e("", "URL Error");
            } catch (IOException e) {
                Log.e("", "กรุณาเชื่อมต่ออินเตอร์เน็ต");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result && errorMsg.equals("")) {
                ((TextView) findViewById(R.id.current_bid)).setText(formatter.format(current_bid));
                ((TextView) findViewById(R.id.end_price)).setText(formatter.format(current_bid) + " บาท");
                findViewById(R.id.current_bid).setBackgroundColor(Color.parseColor("#6cb13b"));
                Tab2.adapter.getItem(job_index).current_price = formatter.format(current_bid);
                makeToast("ประมูลสำเร็จ");
            } else {
                makeToast(errorMsg);
            }
            loadingDialog.dismiss();
            scrollDown();


        }
    }

    public void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    public void countDown(){

        if(countdown_hr+countdown_min+countdown_sec==0) {
            updateCurrentBid();
            return;
        }

        countdown_sec--;

        if(countdown_sec==-1) {
            countdown_min--;
            countdown_sec = 59;
        }

        if(countdown_min==-1) {
            countdown_hr--;
            countdown_min = 59;
        }

        int hr = countdown_hr;
        int min = countdown_min;
        int sec = countdown_sec;


        if(min==60)
            min=0;
        if(sec==60)
            sec=0;

        ((TextView)findViewById(R.id.ending_countdown)).setText(String.format("%02d", hr) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec));

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        countDown();
                    }
                },
                1000);
    }

    public void showMaps(String focus_coor){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putStringArrayListExtra("gps_coor",gps_coor);
        intent.putStringArrayListExtra("address", address);
        intent.putStringArrayListExtra("datetime", datetime);
        intent.putExtra("focus_coor",focus_coor);
        startActivity(intent);
    }

    public void correctWidth(TextView textView, int desiredWidth)
    {
        Paint paint = new Paint();
        Rect bounds = new Rect();

        paint.setTypeface(textView.getTypeface());
        float textSize = textView.getTextSize();
        paint.setTextSize(textSize);
        String text = textView.getText().toString();
        paint.getTextBounds(text, 0, text.length(), bounds);

        while (bounds.width() > desiredWidth)
        {
            textSize--;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public void bid(View view){
        loadingDialog.show();
        PlacebidTask a = new PlacebidTask();
        a.execute(((EditText) findViewById(R.id.newBid)).getText().toString());
    }

    public void bid_ended(){

        ((TextView)findViewById(R.id.ending_in_label)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.ending_countdown)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.current_bid_label)).setText("ราคาจบประมูล");
        ((RelativeLayout)findViewById(R.id.newbid_layout)).setVisibility(View.GONE);

        try {
            if(job_info.getString("user_tel").equals(tel)){
                showToUser();
            } else {
                scrollDown();
            }
            bid_is_ended=true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showToUser(){
        for(int i=0;i<contact_view.size();i++){
            findViewById(contact_view.get(i)).setVisibility(View.VISIBLE);
        }
        ((RelativeLayout) findViewById(R.id.current_bid_layout)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.end_price_layout)).setVisibility(View.VISIBLE);
/*
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        p.addRule(RelativeLayout.BELOW, R.id.end_price_layout);
*/

        if(toSignLocId.isEmpty())
            ((Button)findViewById(R.id.sign_button)).setVisibility(View.GONE);
        else
            ((Button)findViewById(R.id.sign_button)).setVisibility(View.VISIBLE);

    }

    public void scrollDown(){
        final ScrollView scroll = (ScrollView)findViewById(R.id.scroll1);
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void startSignActivity(View v){
        Intent intent = new Intent(getApplicationContext(),SignSignatureActivity.class);
        intent.putExtra("address",toSignAddress.get(0));
        intent.putExtra("loc_id",toSignLocId.get(0));
        intent.putExtra("job_id","" + job_id);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                toSignLocId.remove(0);
                toSignAddress.remove(0);
                findViewById(toSignChecked.get(0)).setVisibility(View.VISIBLE);
                toSignChecked.remove(0);

                if(toSignLocId.isEmpty()) {
                    ((Button)findViewById(R.id.sign_button)).setVisibility(View.GONE);
                    Log.e("toSign","IS EMPTY");
                }
            }
        }
    }

}
