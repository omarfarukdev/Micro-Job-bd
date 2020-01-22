package com.example.microjobbd.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.microjobbd.R;
import com.example.microjobbd.models.WorkerInformation;

import java.util.ArrayList;

public class WorkerListAdapter extends ArrayAdapter <WorkerInformation> {

    ArrayList<WorkerInformation> arrayList;
    Context context;

    public WorkerListAdapter(@NonNull Context context, int resource,ArrayList<WorkerInformation>  arrayList) {
        super(context, resource,arrayList);
        this.arrayList=arrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      View view=convertView;

        LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.worker_shape,null);
        TextView name=view.findViewById(R.id.workername);
        TextView location=view.findViewById(R.id.location);
        name.setText(arrayList.get(position).getName());
        location.setText(arrayList.get(position).getAddress());

      return view;
    }
}
