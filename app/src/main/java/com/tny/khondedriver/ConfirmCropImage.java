package com.tny.khondedriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

public class ConfirmCropImage extends AppCompatActivity {
    static Bitmap croppedImage;
    static Uri croppedImageUri;
    Bitmap returnbitmap;
    BitmapFactory.Options options = null;
    ByteArrayOutputStream stream;
    ProgressDialog prgDialog;
    byte[] byte_arr = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_crop_image);
        ((ImageView) findViewById(R.id.image_croppedImage)).setImageBitmap(croppedImage);

        if(RegisterActivity.currentCropImage!=R.id.image_straight_photo){
            ((ImageView) findViewById(R.id.image_croppedImage)).setImageURI(croppedImageUri);
            croppedImage = ((BitmapDrawable)((ImageView) findViewById(R.id.image_croppedImage)).getDrawable()).getBitmap();
        }
        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
        prgDialog.setMessage("Please Wait...");
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void confirmCroppedImage(View view){
        prgDialog.show();

        Intent intent = getIntent();
        int reqCode = intent.getIntExtra("requestCode",0);

        if(reqCode==2){
            new AsyncTask<Void, Void, String>() {
                String errorMsg = "";
                protected void onPreExecute() {}

                @Override
                protected String doInBackground(Void... params) {

                    stream = new ByteArrayOutputStream();
                    compressImage(750.0);
                    byte_arr = stream.toByteArray();
                    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                    nameValuePair.add(new BasicNameValuePair("slip_picture",Base64.encodeToString(byte_arr, 0)));

                    HttpClient httpClient = new DefaultHttpClient();
                    // replace with your url
                    HttpPost httpPost = new HttpPost(getString(R.string.website_url) + "/report_transfer.php?tel=" + MainActivity.tel);

                    //Encoding POST data
                    try {
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, HTTP.UTF_8));
                    } catch (UnsupportedEncodingException e) {
                        // log exception
                        e.printStackTrace();
                    }

                    //making POST request.
                    try {
                        HttpResponse response = httpClient.execute(httpPost);

                    } catch (ClientProtocolException e) {
                        // Log exception
                        e.printStackTrace();
                    } catch (IOException e) {
                        // Log exception
                        Log.e("", "กรุณาเชื่อมต่ออินเตอร์เน็ต");
                        errorMsg = "กรุณาเชื่อมต่ออินเตอร์เน็ต";
                        e.printStackTrace();
                    }
                    return "";
                }

                @Override
                protected void onPostExecute(String msg) {
                    if (!errorMsg.equals(""))
                        makeToast(errorMsg);

                    prgDialog.hide();
                    Intent res = new Intent();
                    setResult(RESULT_OK, res);
                    finish();

                }
            }.execute(null, null, null);


        } else {
            new AsyncTask<Void, Void, String>() {
                protected void onPreExecute() {}

                @Override
                protected String doInBackground(Void... params) {

                    stream = new ByteArrayOutputStream();
                    if(RegisterActivity.currentImageIndex==0)
                        RegisterActivity.croppedImage = compressImage(300.0);
                    else
                        RegisterActivity.croppedImage = compressImage(1000.0);
                    byte_arr = stream.toByteArray();
                    RegisterActivity.encodedImage[RegisterActivity.currentImageIndex] = Base64.encodeToString(byte_arr, 0);

                    RegisterActivity.thumbnailImage = compressImage(150.0);


                    return "";
                }

                @Override
                protected void onPostExecute(String msg) {
                    prgDialog.hide();
                    Intent res = new Intent();
                    setResult(RESULT_OK, res);
                    finish();

                }
            }.execute(null, null, null);
        }
    }

    public Bitmap compressImage(double maxLength){
        options = new BitmapFactory.Options();
        options.inSampleSize = 3;

        int width = croppedImage.getWidth();
        int height = croppedImage.getHeight();
        float ratio;

        if(width > height) {
            if(croppedImage.getWidth()<maxLength){
                maxLength = croppedImage.getWidth();
            }
            ratio = (float) (width / maxLength);
            height = Math.round(height / ratio);
            width = (int)maxLength;
        } else {
            if(croppedImage.getHeight()<maxLength){
                maxLength = croppedImage.getHeight();
            }
            ratio = (float) (height /maxLength);
            width = Math.round(width / ratio);
            height = (int)maxLength;
        }

        returnbitmap = Bitmap.createScaledBitmap(croppedImage,width,height,false);
        returnbitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);

        return returnbitmap;
    }
    public void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
