package com.huntmix.secbutton;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Backgroundstarter extends Service {
    public static final String CHANNEL_ID = "DeleterService";
    public String pass;

    public String path;
    public List<String> list;
    public List<String> list2;
    public List<String> list3;
    public int size;
    public Boolean rm;
    public Boolean alarm;
    public Boolean clean;
    public Boolean open;
    public Boolean reboot;
    public Boolean autodel;
    public TinyDB tinydb;
    public String openapp;
public Handler mHandler;
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler=new Handler();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Working...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        try {
            runa();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    public void starteraseall() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        //get all values
        TinyDB tinydb = new TinyDB(this);
        list = tinydb.getListString("list");
        openapp = tinydb.getString("openpkg");
        rm = tinydb.getBoolean("rootmode");
        clean = tinydb.getBoolean("clean");
        reboot = tinydb.getBoolean("reboot");
        autodel = tinydb.getBoolean("autodel");
        open = tinydb.getBoolean("open");
        size = tinydb.getInt("sizetargets");
        list2 = tinydb.getListString("list2");
        list3 = tinydb.getListString("list3");
        alarm = tinydb.getBoolean("warning");

        //starting actions

        String result16 = tinydb.getString("pass");
        if (result16.length() < 16){
            while (result16.length() != 16){
                result16=result16+"1";
            }
            tinydb.putString("pass",result16);
        }
        pass = tinydb.getString("pass");
        Log.e("gg",result16+pass+tinydb.getString("pass")+pass.length());
        sendSMS();
        if (list.size() != 0 && !list.isEmpty() && list != null) {

            startdelete();
        }
        if (list2.size() !=0 && !list2.isEmpty() && list2 != null){
            starttargetsdelete();
        }
        if (list3.size() !=0 && !list3.isEmpty() && list3 != null){
            startcrypt(list3,pass);
            tinydb.putBoolean("cryptedf",true);

        }
        tinydb.putString("list","");
        tinydb.putString("list2","");
        tinydb.putString("pass","");
        stopSelf();
        stopSelf();
    }
    public void sendSMS() {
        TinyDB tinydb = new TinyDB(this);
        List<String> nsms = tinydb.getListString("nums");
        List<String> nsms2 = nsms.stream()
                .distinct()
                .collect(Collectors.toList());
        if (nsms.size()>0){
        for(int i = 0;i <nsms2.size();i++){
            String phoneNumber = nsms2.get(i);
            String message = tinydb.getString("msg");
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);}}
    }
    public void starttargetsdelete (){
        for (int i = 0;i<list2.size();i++){
            path = list2.get(i);
            File dir = new File(path);
            deleteRecursive(dir);
            Log.e("deleting:",path); }}
    void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }
    public void deleteme() throws IOException {
        if (autodel == Boolean.TRUE && rm == Boolean.TRUE){
            Runtime.getRuntime().exec("su -c pm uninstall --user 0 com.huntmix.secbutton");
        }

    }
    public void startdelete() throws IOException {
        //clean or delete
        if (clean == Boolean.TRUE){
            for (int v = 0; v < list.size(); v++) {
                //clean via command

                Runtime.getRuntime().exec("su -c pm clear --user 0 " + list.get(v));
            }
        }else{
//check root or default method
            if (rm == Boolean.TRUE) {
                //if root method
                for (int v = 0; v < list.size(); v++) {
                    if (list.get(v) !="com.huntmix.secbutton"){
                        Runtime.getRuntime().exec("su -c pm uninstall --user 0 " + list.get(v));
                    }
//if not root method
                }} else {
                for (int v = 0; v < list.size(); v++) {
                    if (list.get(v) !="com.huntmix.secbutton"){
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        intent.setData(Uri.parse("package:" + list.get(v)));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }}
            }
        }}
    public void encrypt(String path,String path2,String pass) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        TinyDB tinydb = new TinyDB(this);
        tinydb.putBoolean("cryptedf",true);
        FileInputStream notcrypted = new FileInputStream(path);
        FileOutputStream crypted = new FileOutputStream(path2);
        SecretKeySpec sks = new SecretKeySpec(pass.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        CipherOutputStream cos = new CipherOutputStream(crypted, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = notcrypted.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        cos.flush();
        cos.close();
        notcrypted.close(); }
    public void runa() throws Exception{
        mHandler.post(new Runnable(){
            public void run(){
                try {
                    starteraseall(); } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                    e.printStackTrace();}}});}
    public void startcrypt (List<String> list2,String pass) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        if (list2.size() != 0){
            for (int i = 0;i<list2.size();i++){
                String path = list2.get(i);
                File check = new File(path);
                if (check.isDirectory()){
                    for (File child : check.listFiles()){
                        encrypt(child.getAbsolutePath(),child.getAbsolutePath()+".crypted",pass);
                        File del = new File(child.getAbsolutePath());
                        del.delete(); }} else {
                    encrypt(path,path+".crypted",pass);
                    File del = new File(path);
                    del.delete();}}}}}