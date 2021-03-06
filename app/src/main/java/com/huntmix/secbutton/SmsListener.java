package com.huntmix.secbutton;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.content.ContextCompat;


public class SmsListener extends BroadcastReceiver{
    public TinyDB tinydb;
    public Boolean sms;
    public String keyword;
    public String number;
    public Boolean mode;

    @Override
    public void onReceive(Context context, Intent intent) {


        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Log.e("sms","Sms recived");
            tinydb = new TinyDB(context);
            keyword = tinydb.getString("keyword");
            number = tinydb.getString("numbers");
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
                        Log.e("sms",sender+msg);
                        if (mode == Boolean.TRUE && number.contains(sender) && sender.length() >6 && msg.contains(keyword)) {
                            Log.e("sms","Detected keyword");
                            Intent serviceIntent = new Intent(context, Backgroundstarter.class);
                            ContextCompat.startForegroundService(context, serviceIntent);
                        }

                        else{
                        if (mode == Boolean.FALSE && msg.contains(keyword)){
                            Intent serviceIntent = new Intent(context, Backgroundstarter.class);
                            ContextCompat.startForegroundService(context, serviceIntent);
                    }



                    }

            }
        }}
           }
    }


    }







