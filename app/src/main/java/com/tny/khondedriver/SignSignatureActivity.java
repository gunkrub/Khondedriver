package com.tny.khondedriver;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class SignSignatureActivity extends AppCompatActivity {

    DrawingView dv ;
    private Paint mPaint;
    ByteArrayOutputStream stream;
    String loc_id,job_id;
    ProgressDialog prgDialog;
    byte[] byte_arr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_signature);

        dv = new DrawingView(this);
        ((RelativeLayout)findViewById(R.id.signCanvas)).addView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);

        Intent intent = getIntent();
        loc_id = intent.getStringExtra("loc_id");
        job_id = intent.getStringExtra("job_id");
        ((TextView)findViewById(R.id.tosign_address)).setText(intent.getStringExtra("address"));
    }

    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint   mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);

            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawColor(Color.WHITE);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
            setDrawingCacheEnabled(true);

        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }

        public void clearDrawing()
        {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            setDrawingCacheEnabled(false);

            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            onSizeChanged(width, height, width, height);
            invalidate();

            setDrawingCacheEnabled(true);
        }

        public Bitmap get(){
            setDrawingCacheEnabled(false);
            setDrawingCacheEnabled(true);
            return this.getDrawingCache();
        }

    }

    public void clearDraw(View v){
        dv.clearDrawing();
    }

    public void saveSignature(View v){
        new AlertDialog.Builder(this)
                .setTitle("แจ้งเตือน")
                .setMessage("ยืนยันการรับ/ส่งสินค้า?")
                .setNegativeButton("ยกเลิก",null)
                .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        confirmSaveSignature();
                    }
                }).create().show();

    }

    public void confirmSaveSignature(){

        prgDialog = new ProgressDialog(this);
        prgDialog.setCancelable(false);
        prgDialog.setMessage("กำลังบันทึก...");

        Bitmap bm1 = dv.get();
        prgDialog.show();
        stream = new ByteArrayOutputStream();
        compressImage(200,bm1);

        new AsyncTask<Void, Void, String>() {
            String errorMsg = "";
            String line;
            StringBuilder buffer = new StringBuilder();
            protected void onPreExecute() {}

            @Override
            protected String doInBackground(Void... params) {

                byte_arr = stream.toByteArray();
                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
                nameValuePair.add(new BasicNameValuePair("signature", Base64.encodeToString(byte_arr, 0)));

                HttpClient httpClient = new DefaultHttpClient();
                // replace with your url
                HttpPost httpPost = new HttpPost(getString(R.string.website_url) + "/save_signature.php?loc_id=" + loc_id + "&job_id=" + job_id);

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
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    while((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    Log.e("RETURN: ",buffer.toString());

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
                else {
                    makeToast("บันทึกสำเร็จ");
                    Intent res = new Intent();
                    setResult(RESULT_OK, res);
                    finish();
                }
                prgDialog.dismiss();

            }
        }.execute(null, null, null);

    }
    public Bitmap compressImage(double maxLength,Bitmap bitmap1){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;

        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        float ratio;

        if(width > height) {
            if(bitmap1.getWidth()<maxLength){
                maxLength = bitmap1.getWidth();
            }
            ratio = (float) (width / maxLength);
            height = Math.round(height / ratio);
            width = (int)maxLength;
        } else {
            if(bitmap1.getHeight()<maxLength){
                maxLength = bitmap1.getHeight();
            }
            ratio = (float) (height /maxLength);
            width = Math.round(width / ratio);
            height = (int)maxLength;
        }

        Bitmap returnbitmap = Bitmap.createScaledBitmap(bitmap1,width,height,false);
        returnbitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);

        return returnbitmap;
    }

    public void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
