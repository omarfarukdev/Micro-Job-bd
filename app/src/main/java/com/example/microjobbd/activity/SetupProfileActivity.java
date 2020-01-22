package com.example.microjobbd.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.microjobbd.MainActivity;
import com.example.microjobbd.R;
import com.example.microjobbd.user.UserMainActivity;
import com.example.microjobbd.worker.WorkerMainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SetupProfileActivity extends AppCompatActivity {

    EditText userFullName,userAddress,userNIDno;
    Spinner gender,usertype,workerSp;
    Toolbar toolbar;
    String workertyp;
    ImageView imageView;
    Uri uri;
    DatabaseReference imageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        userAddress=findViewById(R.id.useraddressEt);
        userFullName=findViewById(R.id.userfullnameEt);
        userNIDno=findViewById(R.id.userNIDNoEt);
        gender=findViewById(R.id.genderSp);
        usertype=findViewById(R.id.userSp);
        imageView=findViewById(R.id.imageView);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Setup Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(SetupProfileActivity.this, android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.gender));
        gender.setAdapter(genderAdapter);
        ArrayAdapter<String> usertypeAdapter=new ArrayAdapter<>(SetupProfileActivity.this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.usertype));
        usertype.setAdapter(usertypeAdapter);


    }

    public void nextBt(View view) {

        int fName = userFullName.getText().toString().length();
        int faddress=userAddress.getText().toString().length();
        int fnid=userNIDno.getText().toString().length();
        String usertyp=usertype.getSelectedItem().toString();

        if(fName <= 2){
            userFullName.setError("Enter full name.");
        }
        else if(faddress<=3)
        {
            userAddress.setError("Please Your Addres");
        }
        else if(fnid<=9)
        {
            userNIDno.setError("Please Your NID");
        }
       /* else if (!usertyp.equals("User")||!usertyp.equals("Worker")){
            AlertDialog.Builder builder=new AlertDialog.Builder(SetupProfileActivity.this);
            builder.setMessage("Please select your uset type").setPositiveButton("Ok",null);
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }*/

        else if(fName>2&&faddress>3&&fnid>9&&usertyp.equals("User"))
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());//current user database reference
            databaseReference.child("Full Name").setValue(userFullName.getText().toString());
            databaseReference.child("Address").setValue(userAddress.getText().toString());
            databaseReference.child("NID No").setValue(userNIDno.getText().toString());
            databaseReference.child("Gender").setValue(gender.getSelectedItem().toString());
            imageReference=databaseReference.child("Image");
            uploadImages(imageReference);
            Intent intent=new Intent(SetupProfileActivity.this, UserMainActivity.class);
            SetupProfileActivity.this.finish();
            startActivity(intent);

        }
        else if(fName>2&&faddress>3&&fnid>9&&usertyp.equals("Worker"))
        {
           /* final Dialog dialog=new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.user_type_dialog);
            dialog.show();
            Button button=dialog.findViewById(R.id.ok);
             workerSp=dialog.findViewById(R.id.workertype);*/
            final Dialog dialog=new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.user_type_dialog);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.show();
            Button button=dialog.findViewById(R.id.ok);
            workerSp=dialog.findViewById(R.id.workertype);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    workertyp=workerSp.getSelectedItem().toString();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Worker").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());//current user database reference
                    databaseReference.child("Full Name").setValue(userFullName.getText().toString());
                    databaseReference.child("Address").setValue(userAddress.getText().toString());
                    databaseReference.child("NID No").setValue(userNIDno.getText().toString());
                    databaseReference.child("Gender").setValue(gender.getSelectedItem().toString());
                    databaseReference.child("Worker Type").setValue(workertyp);
                    databaseReference.child("Verification").setValue("No");
                    imageReference=databaseReference.child("Image");
                    uploadImages(imageReference);
                    Intent intent=new Intent(SetupProfileActivity.this, MainActivity.class);
                    SetupProfileActivity.this.finish();
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("called","true");
        int count1 = 0;
        if (requestCode == 1 && resultCode == RESULT_OK) {
            count1++;
            if (count1 == 1) {
                uri = data.getData();


                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex1 = cursor.getColumnIndex(filePathColumn[0]);

                String filePath1 = cursor.getString(columnIndex1);
                Log.d("FAaa",""+filePath1);
                imageView.setImageBitmap(BitmapFactory.decodeFile(filePath1));
            }

        }
    }
    public void uploadImage1(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.image_option_dialog);
        //dialog.setTitle("Choose your position.");

        ImageButton cameraDialogImageBt = dialog.findViewById(R.id.cameraDialogImageBt);
        ImageButton gallaryDialogImageBt = dialog.findViewById(R.id.gallaryDialogImageBt);


        cameraDialogImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
                dialog.dismiss();
            }
        });

        gallaryDialogImageBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void uploadImages(final DatabaseReference databaseReference) {
        try {
            final StorageReference storageReference =
                    FirebaseStorage.getInstance().getReference().child("Photo").child(uri.getLastPathSegment());

            Bitmap bitmap = BitmapFactory.decodeFile(uri.toString());

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            databaseReference.setValue(uri.toString());
                            Log.d("Image",""+uri);

                        }
                    });
                }
            });
        }catch (Exception e){
            Log.d("Omm",""+e);
        }
    }
}
