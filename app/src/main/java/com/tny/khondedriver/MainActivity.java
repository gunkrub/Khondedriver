package com.tny.khondedriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean isReceiverRegistered;
    static Bitmap profilePic;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    //CharSequence Titles[]={"ตัวฉัน","งาน","รายการ","อื่นๆ"};
    CharSequence Titles[]={"งาน","รายการ","อื่นๆ"};
    int Numboftabs =3;
    static String tel;
    ProgressDialog loadingDialog;
    String route_starting,route_destination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(MainActivity.this, LocationLogService.class));

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading..");
        loadingDialog.setCancelable(false);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        // Creating The Toolbar and setting it as the Toolbar for the activity
/*
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
*/

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs,getApplicationContext());

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setCustomTabView(R.layout.custom_tab, R.id.textView0,R.id.imageView);
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(android.R.color.white);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        SharedPreferences preferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
        tel = preferences.getString("tel","");

        if(tel.equals("")) {
            startLoginorregisterActivity();
        } else {
            getUserInfo();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("Khondee", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        Tab2 tab2 = (Tab2)pager.getAdapter().instantiateItem(pager,0);
        tab2.refreshJobList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                SharedPreferences preferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
                tel = preferences.getString("tel","");


                Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show();
                getUserInfo();
                if (findViewById(R.id.profile_icon) != null) {
                    Picasso.with(this).load(getString(R.string.website_url) + "get_document_pic.php?type_id=1&tel=" + MainActivity.tel).transform(new CircleTransform()).into((ImageView) findViewById(R.id.profile_icon),
                            new Callback() {
                                @Override
                                public void onSuccess() {
                                    findViewById(R.id.profile_icon_loading).setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {

                                }
                            });

                }
            } else {
                finish();
            }
        } else if(requestCode==4){
            if (resultCode == RESULT_OK) {

            } else {
                finish();
            }
        } else if(requestCode==5){
            if(resultCode == RESULT_OK){
                Tab2 tab2 = (Tab2)pager.getAdapter().instantiateItem(pager,0);
                route_starting = data.getStringExtra("starting");
                route_destination = data.getStringExtra("destination");
                tab2.getJobList(route_starting, route_destination);
                tab2.swipeContainer.setRefreshing(true);
                tab2.adapter.clear();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void click1(View v) {
        new Vibrate(this).vibrate();

        switch(v.getId()){
            case R.id.news_button:
                Intent intent0 = new Intent(this,AnnouncementActivity.class);
                startActivity(intent0);
                break;
            case R.id.profile_button:
                Intent intent = new Intent(this,ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.topup_button:
                Intent intent2 = new Intent(this,HowToTopupActivity.class);
                startActivity(intent2);
                break;
            case R.id.money_button:
                Intent intent3 = new Intent(this,CreditActivity.class);
                startActivity(intent3);
                break;
            case R.id.contact_button:
                Intent intent4 = new Intent(this,ContactUsActivity.class);
                startActivity(intent4);
                break;
            case R.id.portfolio_button:
                Intent intent5 = new Intent(this,PortfolioActivity.class);
                startActivity(intent5);
                break;
        }
        /*
        Tab1 a = (Tab1) pager.getAdapter().instantiateItem(pager, 0);
        a.setDriver_name("Thitiphat");
        */
    }

    public void click2(View v){
        new Vibrate(this).vibrate();

    }


    public void startChooserouteActivity(View v){
        new Vibrate(this).vibrate();

        Intent intent = new Intent(this,SearchjobActivity.class);
        intent.putExtra("starting",route_starting);
        intent.putExtra("destination",route_destination);
        startActivityForResult(intent,5);
    }

    public void getUserInfo(){
        //loadingDialog.show();
        GetInfoTask g1 = new GetInfoTask();
        g1.execute(tel);
    }

    public void updateUserInfo(){
        Tab1 tab1 = (Tab1)pager.getAdapter().instantiateItem(pager,0);
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (tab1.isAdded()){
            ft.detach(tab1);
            ft.attach(tab1);
            ft.commitAllowingStateLoss();
        }

        //findViewById(R.id.mainLayout).setVisibility(View.VISIBLE);

    }

    public void validateUserStatus(){

        SharedPreferences preferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
        int user_status = preferences.getInt("status",0);

        if(user_status==0){
            new AlertDialog.Builder(this)
                    .setTitle("รอการตรวจสอบ")
                    .setMessage("ข้อมูลของท่านยังไม่ได้รับการตรวจสอบ\nท่านจะเห็นงานเมื่อข้อมูลได้รับการตรวจสอบแล้ว")
                    .setPositiveButton("ตกลง", null)
                    .setCancelable(false)
                    .create().show();
        }
            //startApplicationActivity();
    }
    public void startApplicationActivity(){
        SharedPreferences preferences = getSharedPreferences("UserInfo",MODE_PRIVATE);
        String name = preferences.getString("driver_name","");
        String birthday = preferences.getString("birthday","0000-00-00");

        Intent intent = new Intent(this, ApplicationActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("birthday",birthday);
        startActivityForResult(intent, 4);
    }

    public void startLoginorregisterActivity(){
        Intent intent = new Intent(this, LoginorregisterActivity.class);
        startActivityForResult(intent, 1);
    }

    class GetInfoTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        boolean loginResult = false;
        String driver_name="",birthday="",license="",license_province="",id_no="",driver_no="";
        int rating=0,status=0,credit=0,rating_count=0;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            try {
                URL u = new URL(getString(R.string.website_url) + "get_info.php?tel=" + params[0]);
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

                    JSONObject jReturn = new JSONObject(buffer.toString());
                    boolean result = jReturn.getBoolean("result");

                    if(!result){
                        return true;
                    } else {
                        loginResult = true;
                        tel = params[0];
                    }

                    driver_name = jReturn.getString("name");
                    license = jReturn.getString("license");
                    license_province = jReturn.getString("license_province");
                    birthday = jReturn.getString("birthday");

                    status = jReturn.getInt("status");
                    credit = jReturn.getInt("credit");
                    rating = jReturn.getInt("rating");
                    rating_count = jReturn.getInt("rating_count");
                    driver_no = jReturn.getString("driver_no");
                    id_no = jReturn.getString("id_no");

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
                if (loginResult) {

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo", 0);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("driver_name", driver_name);
                    editor.putString("birthday",birthday);
                    editor.putString("license", license);
                    editor.putString("license_province", license_province);
                    editor.putString("id_no", id_no);
                    editor.putString("driver_no", driver_no);

                    editor.putInt("rating", rating);
                    editor.putInt("credit", credit);
                    editor.putInt("status", status);
                    editor.putInt("rating_count", rating_count);

                    editor.commit();

                    validateUserStatus();

                    //loadingDialog.dismiss();
                    //updateUserInfo();

                } else {
                    //cannot find tel no.
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo", 0);
                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("tel", "");
                    editor.commit();

                    startLoginorregisterActivity();
                }
            } else {
                loadingDialog.dismiss();
                makeToast(errorMsg);
            }

            //validateUserStatus();
        }
    }
    public void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
