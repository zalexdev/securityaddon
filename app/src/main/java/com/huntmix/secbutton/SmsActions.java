package com.huntmix.secbutton;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


public class SmsActions extends BroadcastReceiver{
    public TinyDB tinydb;
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
            Log.e("gg","i wroks");
            number = tinydb.getListString("list5");
            mode = tinydb.getBoolean("privatemode");
            if (keyword.length() >3){
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
                            Intent serviceIntent = new Intent(context, StartActions.class);
                            serviceIntent.putExtra("from","sms");
                            ContextCompat.startForegroundService(context, serviceIntent);

                            }

                        else{
                        if (mode == Boolean.FALSE && msg.contains(keyword)){
                            Intent serviceIntent = new Intent(context, StartActions.class);
                            serviceIntent.putExtra("from","sms");
                            ContextCompat.startForegroundService(context, serviceIntent);
                    }}}}}

        }


    }}







