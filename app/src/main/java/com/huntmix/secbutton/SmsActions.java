package com.huntmix.secbutton;


import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


public class SmsActions extends BroadcastReceiver{
    public TinyDB tinydb;
    public String keyword;
    public String geocommand;
    public String blockcommand;
    public String wipecommand;
    public ArrayList<String> number = new ArrayList<>();
    public ArrayList<String> number2 = new ArrayList<>();
    public Boolean mode;
    public Boolean anti;
    public String msg;
    public String sender;
    public GPS gps;
    public DevicePolicyManager mDMP;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            tinydb = new TinyDB(context);
            keyword = tinydb.getString("keyword");
            number = tinydb.getListString("list5");
            mode = tinydb.getBoolean("privatemode");
            anti = tinydb.getBoolean("enableat");
            geocommand = tinydb.getString("geocommand");
            blockcommand = tinydb.getString("blockcommand");
            wipecommand = tinydb.getString("wipecommand");
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            if (bundle != null){
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        sender = msgs[i].getOriginatingAddress();
                        msg = msgs[i].getMessageBody();
                        for(int i2 = 0;i2<number.size();i2++){
                            String  item = number.get(i2);
                            item = item.replace("-","");
                            item = item.replace(" ","");
                            item = item.replace("(","");
                            item = item.replace(")","");
                            item = item.replace("  ","");
                            item =  "+"+item;
                            number2.add(item);
                        }
                        number = number2;}
                if (anti  == Boolean.TRUE && msg.contains(geocommand) && geocommand.length() > 3){
                    gps = new GPS(context);
                    if(gps.canGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        String geo = String.valueOf(latitude)+" "+String.valueOf(longitude);
                        sendsms(sender,geo);
                    }

                }
                if (anti  == Boolean.TRUE && msg.contains(blockcommand) && !tinydb.getBoolean("blocked") && blockcommand.length() > 3){
                    mDMP = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    mDMP.lockNow();
                    tinydb.putBoolean("blocked",true);

                }
                else if (anti  == Boolean.TRUE && msg.contains(blockcommand) && tinydb.getBoolean("blocked") && blockcommand.length() > 3){
                    mDMP = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    mDMP.lockNow();
                    tinydb.putBoolean("blocked",false);

                }
                if (anti  == Boolean.TRUE && msg.contains(wipecommand) && wipecommand.length() > 3){
                    mDMP = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    ComponentName mDeviceAdmin = new ComponentName(context, com.huntmix.secbutton.DeviceAdminManager.class);
                    mDMP.wipeData(0);
                }
                if (mode == Boolean.TRUE && number.contains(sender) && sender.length() >6 && msg.contains(keyword) && keyword.length()>3) {
                    Intent serviceIntent = new Intent(context, StartActions.class);
                    serviceIntent.putExtra("from","sms");
                    ContextCompat.startForegroundService(context, serviceIntent);

                }
                if (mode == Boolean.FALSE && msg.contains(keyword) && keyword.length()>3){
                    Intent serviceIntent = new Intent(context, StartActions.class);
                    serviceIntent.putExtra("from","sms");
                    ContextCompat.startForegroundService(context, serviceIntent);
                }}}}
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendsms(String nsms, String message) {
                String phoneNumber = nsms;
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> parts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);}



}









