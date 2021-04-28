package com.huntmix.secbutton;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

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
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Crypter extends Service {
    public static final String CHANNEL_ID = "CryptService";
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
        TinyDB tinydb = new TinyDB(Crypter.this);
        list2 = tinydb.getListString("listcrypt");
        pass = intent.getStringExtra("pass");
        mHandler = new Handler();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Decrypting with password: " + pass)
                .setSmallIcon(R.drawable.icon)
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

    public void runa() throws Exception {
        TinyDB tinydb = new TinyDB(this);
        mHandler.post(new Runnable() {
            public void run() {
                try {
                    TinyDB tinydb = new TinyDB(Crypter.this);
                    list2 = tinydb.getListString("listcrypt");
                    startcrypt(list2, pass);
                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
                    e.printStackTrace();
                }
                Toast toast2 = Toast.makeText(getApplicationContext(),
                        "Crypted!", Toast.LENGTH_SHORT);
                toast2.show();
                stopSelf();

            }
        });
    }

    public void startcrypt (List<String> list2,String pass) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {

        if (list2.size() != 0){

            for (int i = 0;i<list2.size();i++){
                String path = list2.get(i);
                File check = new File(path);
                if (check.isDirectory()){
                    for (File child : check.listFiles()){
                        encrypt(child.getAbsolutePath(),child.getAbsolutePath()+".crypted",pass);

                        File del = new File(child.getAbsolutePath());
                        del.delete();
                    }

                }

                else {

                    encrypt(path,path+".crypted",pass);
                    File del = new File(path);
                    del.delete();

                }}


        }



    }
    public void encrypt(String path,String path2,String pass) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        TinyDB tinydb = new TinyDB(this);


        FileInputStream notcrypted = new FileInputStream(path);


        FileOutputStream crypted = new FileOutputStream(path2);
        SecretKeySpec sks = new SecretKeySpec(pass.getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(crypted, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while((b = notcrypted.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        notcrypted.close();

    }
}
//        pass2 = intent.getStringExtra("pass");