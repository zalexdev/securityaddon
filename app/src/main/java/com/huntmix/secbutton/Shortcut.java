package com.huntmix.secbutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class Shortcut extends AppCompatActivity {
public  TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortcut);
        tinydb = new TinyDB(this);
        if(tinydb.getBoolean("open") && tinydb.getString("openpkg").length()>3){
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(tinydb.getString("openpkg"));
            if (launchIntent != null){
                startActivity( launchIntent );
            }
        }
        Intent intent = new Intent(this, Backgroundstarter.class);
        startService(intent);
        finish();
    }
}