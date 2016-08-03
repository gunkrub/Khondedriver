package com.tny.khondedriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ApplicationActivity extends AppCompatActivity {
    ProgressDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_application);

        TextView headerLabel = (TextView)findViewById(R.id.header_label);
        headerLabel.setText("โปรดกรอกข้อมูล");

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("กำลังบันทึกข้อมูล..");
        loadingDialog.setCancelable(false);

        ((EditText)findViewById(R.id.nameEditText)).setText(getIntent().getStringExtra("name"));
        String[] birthday = getIntent().getStringExtra("birthday").split("-");

        ((NumberPicker) findViewById(R.id.birthdate)).setMinValue(1);
        ((NumberPicker)findViewById(R.id.birthdate)).setMaxValue(31);
        ((NumberPicker)findViewById(R.id.birthdate)).setValue(Integer.parseInt(birthday[2]));

        ((NumberPicker)findViewById(R.id.birthmonth)).setMinValue(1);
        ((NumberPicker)findViewById(R.id.birthmonth)).setMaxValue(12);
        ((NumberPicker)findViewById(R.id.birthmonth)).setValue(Integer.parseInt(birthday[1]));

        ((NumberPicker)findViewById(R.id.birthyear)).setMinValue(2478);
        ((NumberPicker)findViewById(R.id.birthyear)).setMaxValue(2540);
        ((NumberPicker)findViewById(R.id.birthyear)).setValue(Integer.parseInt(birthday[0]) + 543);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    public void saveNameAndBirthday(View v){

        loadingDialog.show();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo", 0);
        int birthyear = (((NumberPicker)findViewById(R.id.birthyear)).getValue() - 543);
        int birthmonth = ((NumberPicker)findViewById(R.id.birthmonth)).getValue();
        int birthdate = ((NumberPicker)findViewById(R.id.birthdate)).getValue();
        String birthday = birthyear + "-" + birthmonth + "-" + birthdate;
        try {
            String name = URLEncoder.encode(((EditText)findViewById(R.id.nameEditText)).getText().toString(), "UTF-8");
            new SavingInfoTask().execute(pref.getString("tel",""),name,birthday);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void startApplicationActivity2(){
        /*
        Transition slideLeft = new Slide(Gravity.LEFT);
        slideLeft.excludeTarget(android.R.id.statusBarBackground, true);
        slideLeft.excludeTarget(android.R.id.navigationBarBackground, true);
        slideLeft.excludeTarget(R.id.tool_bar, true);
        getWindow().setReenterTransition(slideLeft);
        getWindow().setExitTransition(slideLeft);


        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        Intent intent = new Intent(this, ApplicationActivity2.class);
        startActivityForResult(intent, 99, options.toBundle());
*/
        Intent intent = new Intent(this, ApplicationActivity2.class);
        startActivityForResult(intent,99);
    }


    public void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    class SavingInfoTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        boolean returnResult = false;
        String tel="";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;
            try {
                URL u = new URL(getString(R.string.website_url) + "save_info.php?tel=" + params[0] + "&key=" + md5(params[0] + "1") + "&name=" + params[1] + "&birthday=" + params[2]);
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
                        returnResult = true;
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
            loadingDialog.dismiss();
            if(result){
                if(returnResult){
                    startApplicationActivity2();
                } else {
                    makeToast("พบข้อผิดพลาด ไม่สามารถบันทึกข้อมูลได้");
                }
            } else {
                makeToast(errorMsg);
            }
        }
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
