package com.tny.khondedriver;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImageHelper;

public class HowToTopupActivity extends AppCompatActivity {

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_topup);

        ((TextView)findViewById(R.id.header_label)).setText("วิธีเติมเงิน");
/*
        Picasso.with(this).load("http://khondee.tk/howto1.jpg").into((ImageView) findViewById(R.id.imageLoad),
                new Callback() {
                    @Override
                    public void onSuccess() {
                        findViewById(R.id.loadPictureProgress).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
  */
    }

    public void reportTransfer(View view){
        CropImageHelper.startPickImageActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImageHelper.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImageHelper.getPickImageResultUri(this, data);

            SelectCropImage.imageToCrop = imageUri;
                ConfirmCropImage.croppedImageUri = imageUri;
                Intent intent = new Intent(this,ConfirmCropImage.class);
            intent.putExtra("requestCode", 2);
            startActivityForResult(intent, 2);

        } else if(requestCode==2 && resultCode== Activity.RESULT_OK){
            new AlertDialog.Builder(this)
                    .setTitle("")
                    .setMessage("แจ้งเติมเงินสำเร็จ กรุณารอการตรวจสอบ")
                    .setCancelable(false)
                    .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    }).create().show();
        }
    }
}
