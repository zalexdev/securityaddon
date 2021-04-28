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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.topjohnwu.superuser.Shell;

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

public class StartActions extends Service {
    public static final String CHANNEL_ID = "API";
    public TinyDB tinydb;
    public String pass;
    public List<String> list;
    public List<String> list2;
    public List<String> list3;
    public List<String> list4;
    public String from;
    public String msg;
    public Boolean clean;
    public Boolean rootfolder;
    public Boolean root;
    public Handler mHandler;
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //GETTING OPTIONS
        tinydb = new TinyDB(this);
        list= tinydb.getListString("list");
        list2= tinydb.getListString("list2");
        list3= tinydb.getListString("list3");
        list4= tinydb.getListString("list4");
        pass = tinydb.getString("pass");
        from = intent.getStringExtra("from");
        msg = tinydb.getString("msg");
        clean = tinydb.getBoolean("clear");
        rootfolder = tinydb.getBoolean("rootfolders");
        root = tinydb.getBoolean("rootapps");
        mHandler=new Handler();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Actions started")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
       startactions();
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
    public void toaster(String status){
        Toast stat = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT);
        stat.show();
    }
    //DELETING FILES
    public void startdelete (List<String> list2){
        for (int i = 0;i<list2.size();i++){
            if(rootfolder){
                Shell.su("su rm -rf"+list2.get(i)).exec();
            }{
            String path = list2.get(i);
            toaster(path);
            File dir = new File(path);
            deleteRecursive(dir);}
        }
        toaster("Deleted!");
    }
    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }
    //CRYPTING FILES
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
        notcrypted.close();

    }
   //Sending sms
   @RequiresApi(api = Build.VERSION_CODES.N)
   public void sendsms(List<String> nsms, String message) {
       List<String> nsms2 = nsms.stream()
               .distinct()
               .collect(Collectors.toList());
       if (nsms.size()>0){
           for(int i = 0;i <nsms2.size();i++){
               String phoneNumber = nsms2.get(i);
               SmsManager smsManager = SmsManager.getDefault();
               ArrayList<String> parts = smsManager.divideMessage(message);
               smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);}}
   }
   //Deleting apps
   public void deleteapps(Boolean clean, Boolean rm, List<String> list) throws IOException {
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
    public void startcrypter() throws Exception{
        mHandler.post(new Runnable(){
            public void run(){
                try {
                    startcrypt(list3,pass);
                } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tinydb.putBoolean("crypted",true);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startactions(){
        if (from.equals("sms")){ List<String> actions = tinydb.getListString("smsactions");
                for (int i =0;i<actions.size();i++){
                    if (actions.get(i).equals("Delete Apps")){try {deleteapps(clean,root,list); }catch (IOException e){e.printStackTrace();}}
                    if (actions.get(i).equals("Delete files")){startdelete(list2);}
                    if (actions.get(i).equals("Crypt folders")){try{ startcrypter();} catch (Exception e){e.printStackTrace();}}
                    if (actions.get(i).equals("Send Sms")){sendsms(list4,msg);}
                    if (actions.get(i).equals("Power off")){Intent i2 = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                        i2.putExtra("android.intent.extra.KEY_CONFIRM", true);
                        i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(i2);}
                }
        }
        if (from.equals("lock")){ List<String> actions = tinydb.getListString("lockactions");
            for (int i =0;i<actions.size();i++){
                if (actions.get(i).equals("Delete Apps")){try {deleteapps(clean,root,list); }catch (IOException e){e.printStackTrace();}}
                if (actions.get(i).equals("Delete files")){startdelete(list2);}
                if (actions.get(i).equals("Crypt folders")){try{ startcrypter();} catch (Exception e){e.printStackTrace();}}
                if (actions.get(i).equals("Send Sms")){sendsms(list4,msg);}
                if (actions.get(i).equals("Power off")){Intent i2 = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                    i2.putExtra("android.intent.extra.KEY_CONFIRM", true);
                    i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(i2);}
            }
        }
        if (from.equals("shortcut")){ List<String> actions = tinydb.getListString("shortactions");
            for (int i =0;i<actions.size();i++){
                if (actions.get(i).equals("Delete Apps")){try {deleteapps(clean,root,list); }catch (IOException e){e.printStackTrace();}}
                if (actions.get(i).equals("Delete files")){startdelete(list2);}
                if (actions.get(i).equals("Crypt folders")){try{ startcrypter();} catch (Exception e){e.printStackTrace();}}
                if (actions.get(i).equals("Send Sms")){sendsms(list4,msg);}
                if (actions.get(i).equals("Power off")){Intent i2 = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                    i2.putExtra("android.intent.extra.KEY_CONFIRM", true);
                    i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(i2);}
            }
        }
        if (from.equals("notif")){ List<String> actions = tinydb.getListString("notifactions");
            for (int i =0;i<actions.size();i++){
                if (actions.get(i).equals("Delete Apps")){try {deleteapps(clean,root,list); }catch (IOException e){e.printStackTrace();}}
                if (actions.get(i).equals("Delete files")){startdelete(list2);}
                if (actions.get(i).equals("Crypt folders")){try{ startcrypter();} catch (Exception e){e.printStackTrace();}}
                if (actions.get(i).equals("Send Sms")){sendsms(list4,msg);}
                if (actions.get(i).equals("Power off")){Intent i2 = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                    i2.putExtra("android.intent.extra.KEY_CONFIRM", true);
                    i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(i2);}
            }
        }
        eraseall();

    }
    public void eraseall(){
        tinydb.putString("pass","");
        tinydb.putString("list","");
        tinydb.putString("list1","");
        tinydb.putString("list2","");
        tinydb.putString("list4","");
        tinydb.putString("from","");
        tinydb.putString("msg","");
        tinydb.putString("clean","");
        tinydb.putString("rootfolder","");
        tinydb.putString("root","");
        tinydb.putBoolean("firstrun",false);
    }
}