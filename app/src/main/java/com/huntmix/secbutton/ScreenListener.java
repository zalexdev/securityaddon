package com.huntmix.secbutton;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenListener extends BroadcastReceiver {
public TinyDB tinydb;
public DevicePolicyManager mDMP;
    @Override
    public void onReceive(Context context, Intent intent) {
        tinydb = new TinyDB(context);
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON) && tinydb.getBoolean("blocked")) {
            mDMP = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            mDMP.lockNow();
        }
    }
}