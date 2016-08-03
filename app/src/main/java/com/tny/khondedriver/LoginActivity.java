package com.tny.khondedriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Thitiphat on 9/19/2015.
 */
public class LoginActivity extends AppCompatActivity {
    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Logging in..");
        loadingDialog.setCancelable(false);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void clickLogin(View v){
        new Vibrate(this).vibrate();
        EditText telEditText = (EditText)findViewById(R.id.nameEditText);
        EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);

        String tel = telEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        LoginTask t1 = new LoginTask();
        t1.execute(tel,password);
        loadingDialog.show();
                /*
        ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        dialog.setCancelable(true);
        */
/*
        Intent res = new Intent();
        setResult(RESULT_OK,res);
        finish();
        */

    }

    public void enterWrongPassword(){
        Toast.makeText(this, "รหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
        EditText p1 = (EditText)findViewById(R.id.passwordEditText);
        p1.setText("");
        p1.requestFocus();
    }

    public void makeToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    class LoginTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        boolean loginResult = false;
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
                URL u = new URL(getString(R.string.website_url) + "login.php?tel=" + params[0] + "&password=" + params[1]);
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

                    return true;

                }
                else {
                    Log.e("","HTTP Error");
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
                if(loginResult){
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo",0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("tel", tel);
                    editor.commit();

                    Intent res = new Intent();
                    setResult(RESULT_OK,res);
                    finish();
                } else {
                    enterWrongPassword();
                }
            } else {
                makeToast(errorMsg);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
