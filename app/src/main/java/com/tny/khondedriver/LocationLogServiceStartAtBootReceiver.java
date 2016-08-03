package com.tny.khondedriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Thitiphat on 8/4/2015.
 */
public class LocationLogServiceStartAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, LocationLogService.class);
            context.startService(serviceIntent);
        }

    }
}