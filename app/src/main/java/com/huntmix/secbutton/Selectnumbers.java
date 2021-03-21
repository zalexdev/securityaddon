package com.huntmix.secbutton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;

import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;
import com.wafflecopter.multicontactpicker.RxContacts.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FILE_AND_FOLDER_MULTI_CHOICE;
import static com.github.isabsent.filepicker.SimpleFilePickerDialog.CompositeMode.FOLDER_ONLY_MULTI_CHOICE;

public class Selectnumbers extends Activity {
    private static final int CONTACT_PICKER_REQUEST = 991;
    public TinyDB tinydb;
    private ArrayList<ContactResult> results = new ArrayList<>();
    public ArrayList<String> nums = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.null1);
        tinydb = new TinyDB(this);
    contacts();
    }
    public void contacts(){
        new MultiContactPicker.Builder(Selectnumbers.this)
                .theme(R.style.MultiContactPicker_Azure)
                .hideScrollbar(false)
                .showTrack(true)
                .searchIconColor(Color.WHITE)
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                .handleColor(ContextCompat.getColor(Selectnumbers.this, R.color.colorPrimary))
                .bubbleColor(ContextCompat.getColor(Selectnumbers.this, R.color.colorPrimary))
                .bubbleTextColor(Color.WHITE)
                .setTitleText("Select Contacts")
                .setLoadingType(MultiContactPicker.LOAD_SYNC)
                .limitToColumn(LimitColumn.NONE)
                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_PICKER_REQUEST){
            if(resultCode == RESULT_OK) {
                results.addAll(MultiContactPicker.obtainResult(data));;
                if(results.size() > 0) {
                    for (int i = 0; i<results.size();i++){
                        List<PhoneNumber> num = results.get(i).getPhoneNumbers();
                        for (int i2 = 0;i2<num.size();i2++){
                            nums.add(num.get(i2).getNumber());
                            tinydb.putListString("numbers",nums);
                        }}
                }
            } else if(resultCode == RESULT_CANCELED){
                toaster("No contacts selected!");
            }
        }
        finish();
    }
    public void toaster(String status){
        Toast stat = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT);
        stat.show();
    }
}
