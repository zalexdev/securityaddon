package com.huntmix.secbutton;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetApps {
    private Context justcontext;
    private GetInfo getAppInfo;
    private ArrayList<GetInfo> myApps;
    public TinyDB tinydb;
    public Boolean sys;
    public Boolean rm;

    public GetApps(Context c) {
        justcontext = c;
        myApps = new ArrayList<GetInfo>();
    }

    public ArrayList<GetInfo> getApps() {
        tinydb = new TinyDB(justcontext);

        rm = tinydb.getBoolean("rootapps");
        if (rm == Boolean.FALSE){
            tinydb.putBoolean("sysapp",false);
        }
        sys = tinydb.getBoolean("sysapp");
        if (sys == Boolean.FALSE){
        loadApps();}
        if (sys == Boolean.TRUE){
            loadApps2();
        }
        return myApps;
    }


    private void loadApps() {


        List<ApplicationInfo> packages = justcontext.getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {

                //checks for flags; if flagged, check if updated system app
                if((packageInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    GetInfo newApp = new GetInfo();
                    newApp.setAppName(getApplicationLabelByPackageName(packageInfo.packageName));
                    newApp.setAppPackage(packageInfo.packageName);
                    newApp.setAppIcon(getAppIconByPackageName(packageInfo.packageName));
                    myApps.add(newApp);
                    //it's a system app, not add
                } else if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //in this case, it should be a user-installed app
                } else {
                    GetInfo newApp = new GetInfo();
                    newApp.setAppName(getApplicationLabelByPackageName(packageInfo.packageName));
                    newApp.setAppPackage(packageInfo.packageName);
                    newApp.setAppIcon(getAppIconByPackageName(packageInfo.packageName));
                    myApps.add(newApp);
                }


        }

        Collections.sort(myApps, new Comparator<GetInfo>() {
            @Override
            public int compare(GetInfo s1, GetInfo s2) {
                return s1.getAppName().compareToIgnoreCase(s2.getAppName());

            }
        });

    }
    private void loadApps2() {
        tinydb = new TinyDB(justcontext);
        sys = tinydb.getBoolean("sys");

        List<ApplicationInfo> packages = justcontext.getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {



                GetInfo newApp = new GetInfo();
                newApp.setAppName(getApplicationLabelByPackageName(packageInfo.packageName));
                newApp.setAppPackage(packageInfo.packageName);
                newApp.setAppIcon(getAppIconByPackageName(packageInfo.packageName));
                myApps.add(newApp);



        }

        Collections.sort(myApps, new Comparator<GetInfo>() {
            @Override
            public int compare(GetInfo s1, GetInfo s2) {
                return s1.getAppName().compareToIgnoreCase(s2.getAppName());

            }
        });

    }

    // Custom method to get application icon by package name
    private Drawable getAppIconByPackageName(String packageName) {
        Drawable icon;
        try {
            icon = justcontext.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // Get a default icon
            icon = ContextCompat.getDrawable(justcontext, R.drawable.icon);
        }
        return icon;
    }

    // Custom method to get application label by package name
    private String getApplicationLabelByPackageName(String packageName) {
        PackageManager packageManager = justcontext.getPackageManager();
        ApplicationInfo applicationInfo;
        String label = "Unknown";
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                label = (String) packageManager.getApplicationLabel(applicationInfo);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return label;
    }
}
