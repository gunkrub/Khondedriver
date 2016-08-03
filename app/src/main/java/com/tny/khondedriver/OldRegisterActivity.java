package com.tny.khondedriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class OldRegisterActivity extends AppCompatActivity {
    ProgressDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Registering..");
        loadingDialog.setCancelable(false);


        TextView headerLabel = (TextView)findViewById(R.id.header_label);
        headerLabel.setText("ลงทะเบียน");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void clickRegister(View v) {

        new Vibrate(this).vibrate();

        if (((EditText) findViewById(R.id.nameEditText)).getText().toString().equals("")) {
            ((EditText) findViewById(R.id.nameEditText)).requestFocus();
            Toast.makeText(this, "กรุณากรอกเบอร์โทรศัพท์", Toast.LENGTH_SHORT).show();
        } else if (((EditText) findViewById(R.id.nameEditText)).getText().toString().length() < 9 || ((EditText) findViewById(R.id.nameEditText)).getText().toString().length() > 10) {
            Toast.makeText(this, "เบอร์โทรศัพท์ต้องเป็นตัวเลข 10 หลัก", Toast.LENGTH_SHORT).show();
        } else if (((EditText) findViewById(R.id.passwordEditText)).getText().toString().length() < 4) {
            Toast.makeText(this, "รหัสผ่านต้องมี 4 ตัวขึ้นไป", Toast.LENGTH_SHORT).show();
        } else if (!((EditText) findViewById(R.id.passwordEditText)).getText().toString().equals(((EditText) findViewById(R.id.passwordEditText2)).getText().toString())) {
            ((EditText) findViewById(R.id.passwordEditText)).setText("");
            ((EditText) findViewById(R.id.passwordEditText2)).setText("");
            ((EditText) findViewById(R.id.passwordEditText)).requestFocus();
            Toast.makeText(this, "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_SHORT).show();
        } else {
            EditText telEditText = (EditText)findViewById(R.id.nameEditText);
            EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);

            String tel = telEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            new RegisterTask().execute(tel,password);
            loadingDialog.show();

        }
    }

    public void makeToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }


    class RegisterTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        String registerResult = "";
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
                URL u = new URL(getString(R.string.website_url) + "register.php?tel=" + params[0] + "&password=" + params[1]);
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
                    tel = params[0];
                    registerResult = jReturn.getString("result");

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
                switch (registerResult){
                    case "tel_exist":
                        makeToast("เบอร์โทรศัพท์นี้มีอยู่ในระบบ");
                        break;
                    case "success":
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo",0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("tel", tel);
                        editor.commit();

                        Intent res = new Intent();
                        setResult(RESULT_OK, res);
                        finish();
                        break;
                }

            } else {
                makeToast(errorMsg);
            }
        }
    }

}
