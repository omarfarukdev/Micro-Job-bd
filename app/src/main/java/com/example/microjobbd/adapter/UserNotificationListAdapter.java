package com.example.microjobbd.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.microjobbd.R;
import com.example.microjobbd.models.UserNotificationInfo;
import com.example.microjobbd.user.ConfirmActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.microjobbd.R.color.colorAccent;

public class UserNotificationListAdapter extends ArrayAdapter<UserNotificationInfo> {

    ArrayList<UserNotificationInfo> arrayList;
    Context context;
    String currentNumber;
    DatabaseReference databaseReference,databaseReference1;

    public UserNotificationListAdapter(@NonNull Context context, int resource, ArrayList<UserNotificationInfo> arrayList) {
        super(context, resource,arrayList);
        this.arrayList = arrayList;
        this.context = context;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= convertView;
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.user_notification_shap,null);
        TextView name=view.findViewById(R.id.workerName);
        TextView phoneNo=view.findViewById(R.id.workerPhn);
        TextView time=view.findViewById(R.id.time);
        TextView status=view.findViewById(R.id.status);
        final Button confirm=view.findViewById(R.id.confirm);
        CircleImageView requestImage=view.findViewById(R.id.requestImage);
        name.setText(arrayList.get(position).getName());
        phoneNo.setText(arrayList.get(position).getPhoneNo());
        time.setText(arrayList.get(position).getTime());
        status.setText("your request has been "+arrayList.get(position).getStatus());
        Picasso.get().load(arrayList.get(position).getImage()).resize(400,400).centerCrop().into(requestImage);
        if (arrayList.get(position).getStatus().equals("Denied")||arrayList.get(position).getIsSeen().equals("True")){
            confirm.setVisibility(View.INVISIBLE);
        }
        if (arrayList.get(position).getIsSeen().equals("True")) {
            name.setTextColor(colorAccent);
            phoneNo.setTextColor(colorAccent);
            time.setTextColor(colorAccent);
            status.setTextColor(colorAccent);
        }

        currentNumber= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Request Status").child(currentNumber);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d:dataSnapshot.getChildren()) {
                            if (d.getKey().equals(arrayList.get(position).getKey())) {
                                databaseReference1=databaseReference.child(d.getKey());
                                databaseReference1.child("Is Seen").setValue("True");
                                Intent intent=new Intent(getContext(),ConfirmActivity.class);
                                intent.putExtra("workerNumber",arrayList.get(position).getPhoneNo());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                confirm.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        return view;
    }
}
