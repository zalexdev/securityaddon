package com.huntmix.secbutton;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class DeviceAdmin extends DeviceAdminReceiver {

    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, context.getString(R.string.enable));
        Intent pushIntent = new Intent(context, BackgroundKeeper.class);
        context.startService(pushIntent);
        
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.trydisable);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, context.getString(R.string.disable));
    }

    @Override
    public void onPasswordFailed(Context ctxt, Intent intent) {
        DeviceAdminManager dam = new DeviceAdminManager(ctxt);
        dam.failedUnlockAttemptOccurred();
    }

}
