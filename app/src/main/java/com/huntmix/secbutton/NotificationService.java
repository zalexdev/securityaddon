package com.huntmix.secbutton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.List;

public class NotificationService extends NotificationListenerService {
    Context context;
    public TinyDB tinydb;
    public String text;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.e("notif","i works");
        tinydb = new TinyDB(this);
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        if (extras.getCharSequence("android.text") !=null){
            text = extras.getCharSequence("android.text").toString();
        }
        Log.e("Package",pack);
        Log.i("Text",text);
        List<String> pkgs = tinydb.getListString("notiflist");
        String keynotif = tinydb.getString("keynotif");
        if (pkgs.contains(pack) && text.contains(keynotif) && keynotif.length() >3){
            if (!pack.equals("com.huntmix.newdesign")){
            Intent serviceIntent = new Intent(context, StartActions.class);
            serviceIntent.putExtra("from","notif");
            context.startService(serviceIntent);
            Log.e("notif","i detected");
            Log.e("notif", String.valueOf(pkgs));
            Log.e("notif2", keynotif);}
            tinydb.putString("notiflist","");
            tinydb.putString("keynotif","");
        }

    }
}