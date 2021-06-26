package com.huntmix.secbutton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.abdeveloper.library.MultiSelectModel;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;

public class MainActivity extends AppCompatActivity {
    public TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tinydb = new TinyDB(this);
        if(!tinydb.getBoolean("firstrun")){
            autorunalert();
            ArrayList<Integer> actionslist= new ArrayList<>();
            ArrayList<String> actionslist2= new ArrayList<>();
            actionslist.add(1);
            actionslist.add(2);
            actionslist.add(3);
            actionslist.add(4);
            actionslist2.add("Delete Apps");
            actionslist2.add("Delete files");
            actionslist2.add("Crypt folders");
            actionslist2.add("Send Sms");
            tinydb.putListInt("smspre",actionslist);
            tinydb.putListInt("lockpre",actionslist);
            tinydb.putListInt("notifpre",actionslist);
            tinydb.putListInt("shortpre",actionslist);
            tinydb.putListString("smsactions",actionslist2);
            tinydb.putListString("lockactions",actionslist2);
            tinydb.putListString("shortactions",actionslist2);
            tinydb.putListString("notifactions",actionslist2);
            tinydb.putBoolean("firstrun",true);
        }
        if(tinydb.getBoolean("crypted")){
            Intent intent = new Intent(MainActivity.this, Decrypter.class);
            startActivity(intent);
        }

        Intent pushIntent = new Intent(this, BackgroundKeeper.class);
        this.startService(pushIntent);

            }

    @Override
    protected void onResume() {
        super.onResume();
        counter();
    }

    public void appssettings(View view){
        Intent intent = new Intent(MainActivity.this, AppSettings.class);
        startActivity(intent);
    }
    public void deletesettings(View view){
        Intent intent = new Intent(MainActivity.this, StorageSettings.class);
        startActivity(intent);
    }
    public void cryptsettings(View view){
        Intent intent = new Intent(MainActivity.this, CryptSettings.class);
        startActivity(intent);
    }
    public void sossettings(View view){
        Intent intent = new Intent(MainActivity.this, SosSettings.class);
        startActivity(intent);
    }
    public void smssettings(View view){
        Intent intent = new Intent(MainActivity.this, SmsSettings.class);
        startActivity(intent);
    }
    public void notifsettings(View view){
        Intent intent = new Intent(MainActivity.this, NotifSettings.class);
        startActivity(intent);
    }
    public void locksettings(View view){
        Intent intent = new Intent(MainActivity.this, LockSettings.class);
        startActivity(intent);
    }
    public void shortcutsettings(View view){
        Intent intent = new Intent(MainActivity.this, ShortcutSettings.class);
        startActivity(intent);
    }
    public void cryptonic(View view){
        Intent intent = new Intent(MainActivity.this, Cryptonic.class);
        startActivity(intent);
    }
    public void antitheft(View view){
        Intent intent = new Intent(MainActivity.this, AntitheftSettings.class);
        startActivity(intent);
    }
    public void about(View view){
        Intent intent = new Intent(MainActivity.this, About.class);
        startActivity(intent);
    }
    @SuppressLint("SetTextI18n")
    public void counter(){
        MaterialTextView apps = findViewById(R.id.appcount);
        MaterialTextView storage = findViewById(R.id.storagecount);
        MaterialTextView crypt = findViewById(R.id.cryptcount);
        MaterialTextView sos = findViewById(R.id.soscount);
        ImageView smsnext = findViewById(R.id.smsnext);
        ImageView locknext = findViewById(R.id.locknext);
        ImageView shortnext = findViewById(R.id.shortnext);
        ImageView notifnext = findViewById(R.id.notifnext);
        if (tinydb.getBoolean("perm3") && tinydb.getString("keyword").length() >0){
            ImageViewCompat.setImageTintList(smsnext, ColorStateList.valueOf(getResources().getColor(R.color.green))); }
        if (tinydb.getBoolean("dam") && tinydb.getInt("pinwrong") >1){ImageViewCompat.setImageTintList(locknext, ColorStateList.valueOf(getResources().getColor(R.color.green))); }
        if (tinydb.getBoolean("short")){ImageViewCompat.setImageTintList(shortnext, ColorStateList.valueOf(getResources().getColor(R.color.green)));}
        if (tinydb.getString("keynotif").length() >2 && tinydb.getListString("notiflist").size() >0){ImageViewCompat.setImageTintList(notifnext, ColorStateList.valueOf(getResources().getColor(R.color.green)));}
        apps.setText(Integer.toString(tinydb.getListString("list").size()));
        storage.setText(Integer.toString(tinydb.getListString("list2").size()));
        crypt.setText(Integer.toString(tinydb.getListString("list3").size()));
        sos.setText(Integer.toString(tinydb.getListString("list4").size()));

    }
    public void autorunalert(){
        new AndExAlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.perm))
                .setImage(R.drawable.battery,20)
                .setMessage(getResources().getString(R.string.autorun))
                .setPositiveBtnText("OK")
                .setCancelableOnTouchOutside(false)
                .OnPositiveClicked(new AndExAlertDialogListener() {
                    @Override
                    public void OnClick(String input) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .build();
    }
}