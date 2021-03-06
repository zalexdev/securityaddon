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
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Backgrounddecrypt extends Service {
    public static final String CHANNEL_ID = "DecryptService";
    public String pass;
    public TinyDB tinydb;
    public List<String> list2;
    public Handler mHandler;
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TinyDB tinydb = new TinyDB(Backgrounddecrypt.this);
        list2 = tinydb.getListString("list3");
        pass = intent.getStringExtra("pass");
        mHandler=new Handler();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Decrypting with password: "+pass)
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
                    "Decrypting",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    public void runa() throws Exception{
        TinyDB tinydb = new TinyDB(this);
        tinydb.putBoolean("cryptedf",false);
        mHandler.post(new Runnable(){
            public void run(){
                try {
                    TinyDB tinydb = new TinyDB(Backgrounddecrypt.this);
                    list2 = tinydb.getListString("list3");
                    startdecrypt(list2,pass);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                    e.printStackTrace();
                }
                stopSelf();

            }
        });
    }
    public void decrypt(String path1,String pass) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream(path1);
        String path2 = path1.replace(".crypted","");


        FileOutputStream fos = new FileOutputStream(path2);
        SecretKeySpec sks = new SecretKeySpec(pass.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }
    public void startdecrypt (List<String> list2,String pass) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
        if (pass.length() < 16){
            while (pass.length() != 16){
                pass=pass+"1";
            }
            TinyDB tinydb = new TinyDB(this);
            tinydb.putString("pass",pass);
        }

        if (list2.size() != 0){
            for (int i = 0;i<list2.size();i++){




                String path = list2.get(i);
                File check = new File(path);
                if (check.isDirectory()){
                    for (File child : check.listFiles()){
                        decrypt(child.getAbsolutePath(),pass);

                        File del = new File(child.getAbsolutePath());
                        del.delete();}
                }
                else {

                    decrypt(path+".crypted",pass);
                    File del = new File(path);
                    del.delete();

                }

            }
        }

    }
}