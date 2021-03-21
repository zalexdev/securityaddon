package com.huntmix.secbutton;


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


public class SmsListener extends BroadcastReceiver{
    public TinyDB tinydb;
    public Boolean sms;
    public String keyword;
    public ArrayList<String> number = new ArrayList<>();
    public ArrayList<String> number2 = new ArrayList<>();
    public Boolean mode;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            tinydb = new TinyDB(context);
            keyword = tinydb.getString("keyword");
            number = tinydb.getListString("numbers");
            mode = tinydb.getBoolean("private");
            sms = tinydb.getBoolean("sms");
            if (sms == Boolean.TRUE){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String sender;
            if (bundle != null){
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        sender = msgs[i].getOriginatingAddress();
                        String msg = msgs[i].getMessageBody();
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
                        number = number2;
                        Log.e("sms", String.valueOf(number)+" "+number.contains(sender)+" "+sender);
                        if (mode == Boolean.TRUE && number.contains(sender) && sender.length() >6 && msg.contains(keyword)) {
                            Intent serviceIntent = new Intent(context, Backgroundstarter.class);
                            ContextCompat.startForegroundService(context, serviceIntent);
                            Uri uri = Uri.parse("content://sms/inbox");

                            ContentResolver contentResolver = context.getContentResolver();

                            String where = "address="+sender;
                            Cursor cursor = contentResolver.query(uri, new String[] { "_id", "thread_id"}, where, null,
                                    null);
                            while (cursor.moveToNext()) {
                                long thread_id = cursor.getLong(1);
                                where = "thread_id="+thread_id;
                                Uri thread = Uri.parse("content://sms/inbox");
                                context.getContentResolver().delete(thread, where, null);

                            }



                        }
                        else{
                        if (mode == Boolean.FALSE && msg.contains(keyword)){
                            Intent serviceIntent = new Intent(context, Backgroundstarter.class);
                            ContextCompat.startForegroundService(context, serviceIntent);
                    }}}}}}


    }}







