package com.tny.khondedriver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.theartofdev.edmodo.cropper.CropImageHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
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

public class RegisterActivity extends AppCompatActivity {
    private ViewPager pager;
    private int currentPage;
    ProgressDialog prgDialog;

    static int truck_type_group=0;
    static boolean isAttachedTruck = false;
    final int NO_EXTRA = 0, CRANE=1,DUMP=2,HYDROLIC=3,DUCK=4,OPEN_SIDE=5,HEAVY_LOAD=6;

    static Bitmap croppedImage;
    static Bitmap thumbnailImage;
    static String[] encodedImage = {"","","","","","","",""};
    static int currentImageIndex = 0;

    int[] image_layouts = {
            R.id.straight_photo_image_layout,
            R.id.idcard_image_layout,
            R.id.drivercard_image_layout,
            R.id.licensebook_image_layout,
            R.id.insurancepaper_image_layout,
            R.id.truck_front_image_layout,
            R.id.truck_side_image_layout,
            R.id.truck_back_image_layout
    };

    int[] image_views = {
            R.id.image_straight_photo,
            R.id.image_idcard,
            R.id.image_drivercard,
            R.id.image_licensebook,
            R.id.image_insurancepaper,
            R.id.image_truck_front,
            R.id.image_truck_side,
            R.id.image_truck_back
    };

    static int currentCropImage;

    RequestParams params = new RequestParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        /** Getting a reference to the ViewPager defined the layout file */
        pager = (ViewPager) findViewById(R.id.pager);

        /** Getting fragment manager */
        FragmentManager fm = getSupportFragmentManager();

        /** Instantiating FragmentPagerAdapter */
        RegisterFragmentPagerAdapter pagerAdapter = new RegisterFragmentPagerAdapter(fm);

        /** Setting the pagerAdapter to the pager object */
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        ((TextView) findViewById(R.id.register_step)).setText("ขั้นตอนที่ " + (position + 1));
                        ((ImageView) findViewById(R.id.dot_step1)).setImageResource(R.drawable.dot_switcher);
                        ((ImageView) findViewById(R.id.dot_step2)).setImageResource(R.drawable.dot_switcher);
                        ((ImageView) findViewById(R.id.dot_step3)).setImageResource(R.drawable.dot_switcher);
                        ((ImageView) findViewById(R.id.dot_step4)).setImageResource(R.drawable.dot_switcher);
                        currentPage = position;
                        switch (position + 1) {
                            case 1:
                                ((ImageView) findViewById(R.id.dot_step1)).setImageResource(R.drawable.dot_switcher_white);
                                break;
                            case 2:
                                ((ImageView) findViewById(R.id.dot_step2)).setImageResource(R.drawable.dot_switcher_white);
                                break;
                            case 3:
                                ((ImageView) findViewById(R.id.dot_step3)).setImageResource(R.drawable.dot_switcher_white);
                                break;
                            case 4:
                                ((ImageView) findViewById(R.id.dot_step4)).setImageResource(R.drawable.dot_switcher_white);
                                break;
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                }
        );

        pager.setOffscreenPageLimit(4);

    }

    public void selectTruckChoice(View v){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        String[] items = null;
        TextView label_tv = null;
        String title="";
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        boolean choosingRoofType = false;
        DecimalFormat decFormat=new DecimalFormat("#.#");

        switch (v.getId()){
            case R.id.truck_license_province_layout:
                label_tv = (TextView) findViewById(R.id.truck_license_province_label);
                title = "เลือกจังหวัด";
                items = new String[] {
                        "กรุงเทพมหานคร","กระบี่","กาญจนบุรี","กาฬสินธุ์","กำแพงเพชร","ขอนแก่น","จันทบุรี","ฉะเชิงเทรา","ชลบุรี","ชัยนาท",
                        "ชัยภูมิ","ชุมพร","เชียงใหม่","เชียงราย","ตรัง","ตราด","ตาก","นครนายก","นครปฐม","นครพนม","นครราชสีมา",
                        "นครศรีธรรมราช","นครสวรรค์","นนทบุรี","นราธิวาส","น่าน","บึงกาฬ","บุรีรัมย์","ปทุมธานี","ประจวบคีรีขันธ์","ปราจีนบุรี",
                        "ปัตตานี","พระนครศรีอยุธยา","พะเยา","พังงา","พัทลุง","พิจิตร","พิษณุโลก","เพชรบุรี","แพร่","เพชรบูรณ์","ภูเก็ต",
                        "มหาสารคาม","มุกดาหาร","แม่ฮ่องสอน","ยโสธร","ยะลา","ร้อยเอ็ด","ระนอง","ระยอง","ราชบุรี","ลพบุรี","ลำปาง","ลำพูน",
                        "เลย","ศรีสะเกษ","สกลนคร","สงขลา","สตูล","สมุทรปราการ","สมุทรสงคราม","สมุทรสาคร","สระแก้ว","สระบุรี","สิงห์บุรี",
                        "สุโขทัย","สุพรรณบุรี","สุราษฎร์ธานี","สุรินทร์","หนองคาย","หนองบัวลำภู","อ่างทอง","อำนาจเจริญ","อุดรธานี","อุตรดิตถ์",
                        "อุทัยธานี","อุบลราชธานี"
                };
                break;
            case R.id.truck_type_layout:
                label_tv = (TextView) findViewById(R.id.truck_type_label);
                title = "เลือกชนิดรถ";
                items = new String[]{"รถกระบะ","รถบรรทุก4ล้อใหญ่","รถบรรทุก6ล้อ","รถบรรทุก10ล้อ","รถบรรทุก12ล้อ","รถบรรทุก10ล้อพ่วง","รถเทรลเลอร์2เพลา","รถเทรลเลอร์3เพลา"};
                break;
            case R.id.roof_type_layout:
                label_tv = (TextView) findViewById(R.id.roof_type_label);
                title = "เลือกชนิดตัวถัง";
                choosingRoofType = true;

                switch (truck_type_group){
                    case 1:
                        items = new String[]{"คอกผ้าใบ","ตู้ทึบ","กระบะ","ตู้เย็น"};
                        break;
                    case 2:
                        items = new String[]{"คอกผ้าใบ","ตู้ทึบ","กระบะเตี้ย","กระบะขนดินหิน","พื้นเรียบ","ตู้เย็น"};
                        break;
                    case 3:
                        title="เลือกชนิดหาง";
                        items = new String[]{"หางก้างปลา","หางเรียบ","หางตู้ทึบ","หางกระบะ","หางคอกผ้าใบ","หางโลว์เบด","หางตู้เย็น"};

                        break;
                }

                break;

            case R.id.trunk_width_layout:
                label_tv = (TextView) findViewById(R.id.trunk_width_label);
                title = "เลือกความยาวกระบะท้าย";
                items = new String[19];
                double trunk_width = 3;
                int i=0;
                while(trunk_width<=12){
                    items[i] = ""+decFormat.format(trunk_width) + " ม.";
                    i++;
                    trunk_width += 0.5;
                }

                break;
            case R.id.max_weight_layout:
                label_tv = (TextView) findViewById(R.id.max_weight_label);
                title = "เลือกน้ำหนักบรรทุกสูงสุด";
                items = new String[119];
                double max_weight = 1;
                int j=0;
                while(max_weight<=60){
                    items[j] = ""+decFormat.format(max_weight) + " ตัน";
                    max_weight += 0.5;
                }
                break;


        }

        for(int i=0;i<items.length;i++){
            adapter.add(items[i]);
        }

        final String[]items2 = items;
        final TextView label_tv2 = label_tv;
        final boolean choosingRoofType1 = choosingRoofType;
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                String selected_string = items2[item].toString();
                label_tv2.setText(selected_string);

                if(choosingRoofType1) {
                    filterExtras(selected_string);
                }
                dialog.dismiss();
            }
        });

        builder.setTitle(title+"\n");
        final android.app.AlertDialog choiceDialog = builder.create();

        choiceDialog.show();
    }

    public void filterExtras(String selected_string){

        RadioGroup layout = (RadioGroup) findViewById(R.id.extras_radio);
        int selected_extra=0;

        for(int i=1;i<layout.getChildCount();i++){
            layout.getChildAt(i).setVisibility(View.GONE);
            if(((RadioButton)layout.getChildAt(i)).isChecked())
                selected_extra = i;
        }
        if (truck_type_group ==1){
            layout.getChildAt(HEAVY_LOAD).setVisibility(View.VISIBLE);
        } else if (truck_type_group == 2) {
            if(selected_string.equals("คอกผ้าใบ")){
                layout.getChildAt(HYDROLIC).setVisibility(View.VISIBLE);
                layout.getChildAt(OPEN_SIDE).setVisibility(View.VISIBLE);
                layout.getChildAt(DUMP).setVisibility(View.VISIBLE);
            }else if(selected_string.equals("ตู้ทึบ")){
                layout.getChildAt(HYDROLIC).setVisibility(View.VISIBLE);
                layout.getChildAt(OPEN_SIDE).setVisibility(View.VISIBLE);
            }else if(selected_string.equals("กระบะเตี้ย")) {
                layout.getChildAt(CRANE).setVisibility(View.VISIBLE);
                layout.getChildAt(OPEN_SIDE).setVisibility(View.VISIBLE);
                layout.getChildAt(DUMP).setVisibility(View.VISIBLE);
            }else if(selected_string.equals("กระบะขนดินหิน")){
                layout.getChildAt(DUMP).setVisibility(View.VISIBLE);
            }else if(selected_string.equals("พื้นเรียบ")){
                layout.getChildAt(CRANE).setVisibility(View.VISIBLE);
                layout.getChildAt(DUCK).setVisibility(View.VISIBLE);
            }else {
                layout.getChildAt(HYDROLIC).setVisibility(View.VISIBLE);
                layout.getChildAt(OPEN_SIDE).setVisibility(View.VISIBLE);
            }

            if(isAttachedTruck){
                layout.getChildAt(CRANE).setVisibility(View.GONE);
                layout.getChildAt(HYDROLIC).setVisibility(View.GONE);
            }

        } else if(truck_type_group ==3){

                if(selected_string.equals("หางเรียบ")){
                    layout.getChildAt(DUCK).setVisibility(View.VISIBLE);
                } else if(selected_string.equals("หางตู้ทึบ")){
                    layout.getChildAt(OPEN_SIDE).setVisibility(View.VISIBLE);
                } else if(selected_string.equals("หางกระบะ")){
                    layout.getChildAt(OPEN_SIDE).setVisibility(View.VISIBLE);
                    layout.getChildAt(DUMP).setVisibility(View.VISIBLE);
                } else if(selected_string.equals("หางคอกผ้าใบ")){
                    layout.getChildAt(OPEN_SIDE).setVisibility(View.VISIBLE);
                    layout.getChildAt(DUMP).setVisibility(View.VISIBLE);
                } else if(selected_string.equals("หางโลว์เบด")){
                    layout.getChildAt(DUCK).setVisibility(View.VISIBLE);
                }

        }

        ((RadioButton)layout.getChildAt(0)).setChecked(true);
        findViewById(R.id.extras_layout).setVisibility(View.VISIBLE);
        if(selected_string.equals("หางก้างปลา") || selected_string.equals("หางตู้เย็น")){
            findViewById(R.id.extras_layout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if(currentPage>0){
            pager.setCurrentItem(currentPage-1);
        } else{
            new AlertDialog.Builder(this)
                    .setTitle("ยกเลิกการสมัครใช้งาน")
                    .setMessage("ข้อมูลของท่านจะไม่ได้รับการบันทึก\nยืนยันการยกเลิกสมัครใช้งาน?")
                    .setNegativeButton("ไม่ใช่", null)
                    .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    }).create().show();
        }
    }

    public void goNext(View view){
        pager.setCurrentItem(currentPage + 1);
    }

    public void loadImagefromGallery(View view) {
        switch (view.getId()){
            case R.id.straight_photo_add:
                currentImageIndex = 0;
                break;
            case R.id.idcard_add:
                currentImageIndex =1;
                break;
            case R.id.drivercard_add:
                currentImageIndex =2;
                break;
            case R.id.licensebook_add:
                currentImageIndex =3;
                break;
            case R.id.insurancepaper_add:
                currentImageIndex =4;
                break;
            case R.id.truck_front_add:
                currentImageIndex =5;
                break;
            case R.id.truck_side_add:
                currentImageIndex =6;
                break;
            case R.id.truck_back_add:
                currentImageIndex =7;
                break;
        }
        CropImageHelper.startPickImageActivity(this);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImageHelper.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImageHelper.getPickImageResultUri(this, data);

            SelectCropImage.imageToCrop = imageUri;

            if(currentImageIndex==0) {
                Intent cropAct = new Intent(this, SelectCropImage.class);
                startActivityForResult(cropAct, 1);
            } else {
                ConfirmCropImage.croppedImageUri = imageUri;
                Intent intent = new Intent(this,ConfirmCropImage.class);
                intent.putExtra("requestCode", 1);
                startActivityForResult(intent, 1);
            }

        } else if(requestCode == 1 && resultCode == RESULT_OK) {
            ((ImageView)findViewById(image_views[currentImageIndex])).setImageBitmap(thumbnailImage);
            (findViewById(image_layouts[currentImageIndex])).setVisibility(View.VISIBLE);
        }
    }

    public void submitRegistration(View view){
        int[] requiredText = {
                R.id.edittext_tel,
                R.id.edittext_name,
                R.id.edittext_id_no,
                R.id.edittext_license_plate,
                R.id.edittext_password
        };

        for (int viewID: requiredText) {
            if(((EditText)findViewById(viewID)).getText().toString().equals("")){
                int page = 0;

                if(viewID==R.id.edittext_license_plate)
                    page =1;

                showErrorDialog(page, "กรณากรอก " + ((EditText) findViewById(viewID)).getHint().toString());
                findViewById(viewID).requestFocus();
                return;
            }
        }

        int[] requiredTruckChoice = {
                R.id.truck_license_province_label,
                R.id.truck_type_label,
                R.id.trunk_width_label,
                R.id.max_weight_label,
                R.id.roof_type_label
        };

        for (int viewID: requiredTruckChoice) {
            TextView textView = ((TextView)findViewById(viewID));
            RelativeLayout layout = (RelativeLayout)textView.getParent();
            if(textView.getText().toString().length()>5 && layout.getVisibility()==View.VISIBLE) {
                if (textView.getText().toString().substring(0, 5).equals("เลือก")) {
                    showErrorDialog(1, "กรณาเลือก " + ((TextView)layout.getChildAt(0)).getText().toString());
                    return;
                }
            }

        }

        if (((EditText) findViewById(R.id.edittext_tel)).getText().toString().length() < 9 || ((EditText) findViewById(R.id.edittext_tel)).getText().toString().length() > 10){
            showErrorDialog(0,"เบอร์โทรศัพท์ต้องเป็นตัวเลข 10 หลัก");
            findViewById(R.id.edittext_tel).requestFocus();
            return;
        }

        if (((EditText) findViewById(R.id.edittext_password)).getText().toString().length() < 4){
            showErrorDialog(0,"รหัสผ่านต้องมี 4 ตัวขึ้นไป");
            findViewById(R.id.edittext_password).requestFocus();
            return;
        }

        if(!((EditText)findViewById(R.id.edittext_password)).getText().toString().equals(((EditText)findViewById(R.id.edittext_password2)).getText().toString())){
            showErrorDialog(0,"กรณากรอกรหัสผ่านให้ตรงกัน");
            ((EditText) findViewById(R.id.edittext_password)).setText("");
            ((EditText) findViewById(R.id.edittext_password2)).setText("");
            findViewById(R.id.edittext_password).requestFocus();
            return;
        }

        for(String en1 : encodedImage){
            if(en1.equals("")){
                showErrorDialog(2,"กรุณาใส่รูปให้ครบ");
                return;
            }

        }

        if(!((RadioButton)findViewById(R.id.radio_agreement)).isChecked()) {
            showErrorDialog(3,"กรุณายอมรับเงื่อนไขตามข้อกำหนด");
            return;
        }

        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

        nameValuePair.add(new BasicNameValuePair("tel",((TextView)findViewById(R.id.edittext_tel)).getText().toString()));
        nameValuePair.add(new BasicNameValuePair("name",((TextView)findViewById(R.id.edittext_name)).getText().toString()));
        nameValuePair.add(new BasicNameValuePair("id_no",((TextView)findViewById(R.id.edittext_id_no)).getText().toString()));
        nameValuePair.add(new BasicNameValuePair("password",((TextView)findViewById(R.id.edittext_password)).getText().toString()));
        nameValuePair.add(new BasicNameValuePair("referer",((TextView)findViewById(R.id.edittext_referer)).getText().toString()));
        nameValuePair.add(new BasicNameValuePair("license_plate",((TextView)findViewById(R.id.edittext_license_plate)).getText().toString()));
        nameValuePair.add(new BasicNameValuePair("license_plate_province",((TextView)findViewById(R.id.truck_license_province_label)).getText().toString()));
        nameValuePair.add(new BasicNameValuePair("truck_type",((TextView)findViewById(R.id.truck_type_label)).getText().toString()));
        nameValuePair.add(new BasicNameValuePair("trunk_width",((TextView)findViewById(R.id.trunk_width_label)).getText().toString()));
        nameValuePair.add(new BasicNameValuePair("max_weight",((TextView)findViewById(R.id.max_weight_label)).getText().toString()));
        nameValuePair.add(new BasicNameValuePair("roof_type",((TextView)findViewById(R.id.roof_type_label)).getText().toString()));

        int radioButtonID = ((RadioGroup)findViewById(R.id.extras_radio)).getCheckedRadioButtonId();
        nameValuePair.add(new BasicNameValuePair("extra",((RadioButton)findViewById(radioButtonID)).getText().toString()));
        radioButtonID = ((RadioGroup)findViewById(R.id.radio_out_country)).getCheckedRadioButtonId();
        nameValuePair.add(new BasicNameValuePair("out_country",((RadioButton)findViewById(radioButtonID)).getText().toString()));

        makeRegisterPost(nameValuePair);
    }

    private void showErrorDialog(int page,String errorMsg){
        pager.setCurrentItem(page);
        AlertDialog errorDialog = new AlertDialog.Builder(this)
                .setTitle("แจ้งเตือน")
                .setPositiveButton("ตกลง", null)
                .create();

        errorDialog.setMessage(errorMsg);
        errorDialog.show();

    }

    private void makeRegisterPost(final List<NameValuePair> nameValuePair0) {
        new AsyncTask<Void, Void, String>() {
            String errorMsg = "";
            protected void onPreExecute() {
                prgDialog = new ProgressDialog(RegisterActivity.this);
                prgDialog.setCancelable(false);
                prgDialog.setMessage("กำลังบันทึกข้อมูล..");
                prgDialog.show();
            };

            @Override
            protected String doInBackground(Void... params) {
                final List<NameValuePair>nameValuePair = nameValuePair0;
                String line;
                StringBuilder buffer = new StringBuilder();
                HttpClient httpClient = new DefaultHttpClient();
                // replace with your url
                HttpPost httpPost = new HttpPost(getString(R.string.website_url) + "/register.php");

                //Encoding POST data
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair,HTTP.UTF_8));
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

                    JSONObject json = new JSONObject(buffer.toString());
                    errorMsg = json.getString("errorMsg");
                    Log.e("Http Post Response:", buffer.toString());
                } catch (ClientProtocolException e) {
                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    // Log exception
                    Log.e("", "กรุณาเชื่อมต่ออินเตอร์เน็ต");
                    errorMsg = "กรุณาเชื่อมต่ออินเตอร์เน็ต";
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                prgDialog.hide();
                if(!errorMsg.equals(""))
                    if(errorMsg.equals("tel_exists")){
                        showErrorDialog(0,"เบอร์โทรนี้ซ้ำกับในระบบ");
                    } else if (errorMsg.equals("id_no_exists")){
                        showErrorDialog(0,"เลขประชาชนนี้ซ้ำกับในระบบ");
                    }else {
                        makeToast(errorMsg);
                    }
                else
                    uploadPictures();
            }
        }.execute(null, null, null);

    }

    private void uploadPictures() {
        int index = 0;
        prgDialog = new ProgressDialog(RegisterActivity.this);
        prgDialog.setCancelable(false);
        prgDialog.setMessage("กำลังบันทึกรูป...");
        prgDialog.setProgressPercentFormat(null);
        prgDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        prgDialog.setProgress(0);
        prgDialog.setMax(image_views.length);
        prgDialog.show();

        final String tel = ((TextView)findViewById(R.id.edittext_tel)).getText().toString();

        for (String en1 : encodedImage) {
            final List<NameValuePair> nameValuePair0 = new ArrayList<NameValuePair>();
            nameValuePair0.add(new BasicNameValuePair("document_type",""+(index+1)));
            nameValuePair0.add(new BasicNameValuePair("document_blob",en1));
            final List<NameValuePair> nameValuePair = nameValuePair0;
            new AsyncTask<Void, Void, String>() {
                String errorMsg = "";
                String line;
                StringBuilder buffer = new StringBuilder();

                protected void onPreExecute() {
                }

                @Override
                protected String doInBackground(Void... params) {
                    HttpClient httpClient = new DefaultHttpClient();
                    // replace with your url
                    HttpPost httpPost = new HttpPost(getString(R.string.website_url) + "/add_document.php?tel=" + tel);

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
                        String json = buffer.toString();
                        // write response to log

                        Log.e("Http Post Response:", json);
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
                    else
                        prgDialog.setProgress(prgDialog.getProgress()+1);

                    if(prgDialog.getProgress()==image_views.length) {
                        prgDialog.hide();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserInfo",0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("tel", tel);
                        editor.commit();

                        Intent res = new Intent();
                        setResult(RESULT_OK, res);
                        finish();
                    }
                }
            }.execute(null, null, null);
            index++;
        }

    }

    public void makeToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
