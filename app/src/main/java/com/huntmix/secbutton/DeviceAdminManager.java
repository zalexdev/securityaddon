package com.huntmix.secbutton;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class DeviceAdminManager {
    private Context context;
    private ComponentName mDeviceAdmin;
    private DevicePolicyManager mDPM;
    public TinyDB tinydb;

    public DeviceAdminManager(Context context) {
        this.context = context;
        mDeviceAdmin = new ComponentName(context, com.huntmix.secbutton.DeviceAdmin.class);
        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    public boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdmin);
    }


    private int getNumberOfFailedUnlockAttempts(){
        return mDPM.getCurrentFailedPasswordAttempts();
    }
    public Intent getStartAdminEnableIntent(){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                context.getString(R.string.admindesc));
        return intent;
    }
    public void failedUnlockAttemptOccurred(){
        tinydb = new TinyDB(context);
        int fails = getNumberOfFailedUnlockAttempts();
        if (fails == tinydb.getInt("pinwrong") || fails > tinydb.getInt("pinwrong")){
            if (tinydb.getBoolean("camera")){
                CameraManager mgr = new CameraManager(context);
                mgr.takePhoto();}
            Intent intent = new Intent(context, StartActions.class);
            intent.putExtra("from","lock");
            context.startService(intent);
        }
        Log.e("Wrong pin entered! ", String.valueOf(fails));
            Log.e("GG", String.valueOf(mDPM.isAdminActive(mDeviceAdmin)));
            Toast toast2 = Toast.makeText(context,
                    "Wrong pin entered!"+fails, Toast.LENGTH_SHORT);
            toast2.show(); }


}
