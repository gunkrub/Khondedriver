package com.tny.khondedriver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Thitiphat on 9/20/2015.
 */
public class SearchjobActivity extends AppCompatActivity {
    int defaultStarting = 0;
    int defaultDestination = 0;
    final CharSequence[] items = {
            " ทั่วประเทศ",
            " ภาคกลางทั้งหมด",
            "        กรุงเทพฯ", "        กำแพงเพชร", "        ชัยนาท", "        นครนายก",
            "        นครปฐม", "        นครสวรรค์", "        นนทบุรี", "        ปทุมธานี",
            "        พระนครศรีอยุธยา", "        พิจิตร", "        พิษณุโลก", "        เพชรบูรณ์",
            "        ลพบุรี", "        สมุทรปราการ", "        สมุทรสงคราม", "        สมุทรสาคร",
            "        สิงห์บุรี", "        สุโขทัย", "        สุพรรณบุรี", "        สระบุรี",
            "        อ่างทอง", "        อุทัยธานี",
            " ภาคเหนือทั้งหมด",
            "        เชียงราย", "        เชียงใหม่", "        น่าน", "        พะเยา",
            "        แพร่", "        แม่ฮ่องสอน", "        ลำปาง", "        ลำพูน",
            "        อุตรดิตถ์",
            " ภาคอีสานทั้งหมด",
            "        กาฬสินธุ์", "        ขอนแก่น", "        ชัยภูมิ", "        นครพนม",
            "        นครราชสีมา", "        บึงกาฬ", "        บุรีรัมย์", "        มหาสารคาม",
            "        มุกดาหาร", "        ยโสธร", "        ร้อยเอ็ด", "        เลย",
            "        สกลนคร", "        สุรินทร์", "        ศรีสะเกษ", "        หนองคาย",
            "        หนองบัวลำภู", "        อุดรธานี", "        อุบลราชธานี", "        อำนาจเจริญ",
            " ภาคใต้ทั้งหมด",
            "        กระบี่", "        ชุมพร", "        ตรัง", "        นครศรีธรรมราช",
            "        นราธิวาส", "        ปัตตานี", "        พังงา", "        พัทลุง", "        ภูเก็ต",
            "        ระนอง", "        สตูล", "        สงขลา", "        สุราษฎร์ธานี", "        ยะลา",
            " ภาคตะวันออกทั้งหมด",
            "        จันทบุรี", "        ฉะเชิงเทรา", "        ชลบุรี", "        ตราด", "        ปราจีนบุรี",
            "        ระยอง", "        สระแก้ว",
            " ภาคตะวันตกทั้งหมด",
            "        กาญจนบุรี", "        ตาก", "        ประจวบคีรีขันธ์", "        เพชรบุรี", "        ราชบุรี"
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchjob);
        TextView headerLabel = (TextView) findViewById(R.id.header_label);
        headerLabel.setText("เลือกเส้นทาง");

        Intent intent = getIntent();
        String starting = intent.getStringExtra("starting");
        String destination = intent.getStringExtra("destination");

        for (int i=0;i<items.length;i++) {
            if(items[i].toString().equals(starting)) {
                defaultStarting = i;
                ((TextView) findViewById(R.id.select_starting_label)).setText(items[i].toString());
            }

            if(items[i].toString().equals(destination)) {
                defaultDestination = i;
                ((TextView) findViewById(R.id.select_destination_label)).setText(items[i].toString());
            }
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void showStartingDialog(View v) {
        vibrate();
        AlertDialog startingDialog = null;

        // Creating and Building the Dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("เลือกต้นทาง\n");
        builder.setSingleChoiceItems(items, defaultStarting, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                defaultStarting = item;
                ((TextView) findViewById(R.id.select_starting_label)).setText(items[item].toString());
                dialog.dismiss();
            }
        });

        startingDialog = builder.create();
        startingDialog.show();
    }

    public void showDestinationDialog(View v) {
        vibrate();
        AlertDialog startingDialog = null;

        // Creating and Building the Dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("เลือกปลายทาง\n");

        builder.setSingleChoiceItems(items, defaultDestination, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                defaultDestination = item;
                ((TextView) findViewById(R.id.select_destination_label)).setText(items[item].toString());
                dialog.dismiss();
            }
        });

        startingDialog = builder.create();
        startingDialog.show();
    }

    public void clickSearch(View v) {
        Intent intent = new Intent();
        intent.putExtra("starting", ((TextView) findViewById(R.id.select_starting_label)).getText());
        intent.putExtra("destination", ((TextView) findViewById(R.id.select_destination_label)).getText());
        setResult(RESULT_OK, intent);

        finish();
    }

    public void vibrate() {
        Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        v.vibrate(10);
    }
/*
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Searchjob Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.thitiphat.khondee/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Searchjob Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.thitiphat.khondee/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    */
}
