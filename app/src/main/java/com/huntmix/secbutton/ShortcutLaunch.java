package com.huntmix.secbutton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ShortcutLaunch extends AppCompatActivity {
public TinyDB tinydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut_launch);
        Intent intent = new Intent(this, StartActions.class);
        intent.putExtra("from","shortcut");
        startService(intent);
        openapp();
    }
    public void openapp(){
        tinydb = new TinyDB(this);
        if( tinydb.getString("apppkg").length()>3){
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(tinydb.getString("apppkg"));
            if (launchIntent != null){
                startActivity( launchIntent );
            }
        }

        finish();

    }
}