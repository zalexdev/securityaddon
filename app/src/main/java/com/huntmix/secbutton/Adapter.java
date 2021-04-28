package com.huntmix.secbutton;



import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    ;

    public Context justcontext;
    private ArrayList<GetInfo> data;
    List selected = new ArrayList();

    public Adapter(Context context, ArrayList<GetInfo> list) {
        justcontext = context;
        data = list;

    }




    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameofapk;
        public TextView nameofpkg;
        public ImageView iconofapp;
        public CheckBox selector;
        public RelativeLayout fullitem;

        public ViewHolder(View v) {
            super(v);
            // Get the widgets reference from custom layout
            nameofapk = (TextView) v.findViewById(R.id.nameofapp);
            nameofpkg = (TextView) v.findViewById(R.id.apkpkg);
            iconofapp = (ImageView) v.findViewById(R.id.iconofapp);
            selector = (CheckBox) v.findViewById(R.id.selector);
            fullitem = (RelativeLayout) v.findViewById(R.id.item);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(justcontext).inflate(R.layout.apkitem, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // Get pkg
        final String packageName = data.get(position).getAppPackage();

        // Get thicon
        Drawable icon = data.get(position).getAppIcon();

        // Get label
        String label = data.get(position).getAppName();

        // Set label
        holder.nameofapk.setText(label);

        // Set pkg
        holder.nameofpkg.setText(packageName);

        // Set icon
        holder.iconofapp.setImageDrawable(icon);

        holder.selector.setChecked(data.get(position).isSelected());
//onclick set checkbox state and append pkg to list with writing to db
        holder.fullitem.setOnClickListener(new View.OnClickListener() {



            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                TinyDB tinydb = new TinyDB(justcontext);
                data.get(position).setSelected(!data.get(position).isSelected());
               com.huntmix.secbutton.Adapter.this.notifyDataSetChanged();
                if (selected.contains(packageName)){
                    selected.remove(packageName);
                }else{
                    selected.add(packageName);
                }
                tinydb.putListString("list", (ArrayList<String>) selected);
                Log.e("pkg", String.valueOf(selected));


            }

        });




    }

    @Override
    public int getItemCount() {
        // Count the installed apps
        return data.size();
    }
}
