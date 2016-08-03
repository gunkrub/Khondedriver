package com.tny.khondedriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Thitiphat on 9/20/2015.
 */
public class LoginorregisterActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_loginorregister);

            TextView headerLabel = (TextView)findViewById(R.id.header_label);
            headerLabel.setText("Khondee");

        }

    public void startLoginActivity(View v){
        new Vibrate(this).vibrate();
        Intent intent = new Intent(this,LoginActivity.class);
        startActivityForResult(intent, 2);

    }

    public void startRegisterActivity(View v){
        new Vibrate(this).vibrate();

        Intent intent = new Intent(this,RegisterActivity.class);
        startActivityForResult(intent, 3);


        //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_url)));
        //startActivity(browserIntent);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2 || requestCode==3) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK,data);
            finish();
            }
        }
    }
}
