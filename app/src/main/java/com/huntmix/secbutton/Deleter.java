package com.huntmix.secbutton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Deleter extends Activity {
    public TinyDB tinydb;
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Intent intent = new Intent(this, Backgroundstarter.class);
            startService(intent);
            finish();

    }
}
