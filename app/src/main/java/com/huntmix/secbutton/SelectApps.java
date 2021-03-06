package com.huntmix.secbutton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SelectApps extends AppCompatActivity{



    private RelativeLayout mRelativeLayout;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<GetInfo> installedApps;
    private FloatingActionButton shareButton;
    private GetApps appManager;
    public Context context;

    public TinyDB tinydb;






    @Override
    public void onCreate(Bundle savedInstanceState){

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        tinydb = new TinyDB(this);
        installedApps = new ArrayList<GetInfo>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        appManager = new GetApps(this);
        installedApps = appManager.getApps();

        // Initialize a new adapter for RecyclerView
        mAdapter = new Adapter(
                getApplicationContext(),
                installedApps
        );


        mRecyclerView.setAdapter(mAdapter);

    }


    public void openmain() {

        Intent intent = new Intent(SelectApps.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void deletelist(View view){
        tinydb.putString("list","");
        Toast.makeText(SelectApps.this, "List was cleared!",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SelectApps.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}