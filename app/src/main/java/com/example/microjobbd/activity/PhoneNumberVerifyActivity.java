package com.example.microjobbd.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.microjobbd.R;
import com.example.microjobbd.user.UserMainActivity;
import com.example.microjobbd.worker.WorkerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.TimeUnit;

public class PhoneNumberVerifyActivity extends AppCompatActivity {

    private String verificationId;
    private FirebaseAuth mAuth;
    ProgressDialog progressBar;
    ProgressDialog progressDialog;
    EditText editText;
    Button worker,user;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verify);

        mAuth=FirebaseAuth.getInstance();
        editText=findViewById(R.id.codeEt);
        constraintLayout=findViewById(R.id.constrainLayout);
        progressDialog=new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Please wait");

        String phonenumber=getIntent().getStringExtra("phonenumber");
        sendVerificationCode(phonenumber);
    }

    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks
        );
        progressBar = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressBar.setMessage("Sending verification code");

        progressBar.show();
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId=s;
            progressBar.dismiss();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code=phoneAuthCredential.getSmsCode();
            if (code != null){
                editText.setText(code);
                verifyCode(code);
                progressBar.dismiss();
            }else {
                signInWithCredential(phoneAuthCredential);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneNumberVerifyActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PhoneNumberVerifyActivity.this,PhoneNumberActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
            progressBar.dismiss();

        }
    };

    public void nextBt(View view) {

        String code=editText.getText().toString().trim();
        if (code.isEmpty()||code.length()<6)
        {
            editText.setError("Enter code...");
            editText.requestFocus();
            return;

        }
        verifyCode(code);
    }
    private void verifyCode(String code)
    {
        //ok
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {

                if(task.isSuccessful()){

                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Worker").child(task.getResult().getUser().getPhoneNumber());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                int count = (int) dataSnapshot.getChildrenCount();
                                if (count>=3 && constraintLayout.getVisibility() == View.VISIBLE){
                                    Intent intent = new Intent(PhoneNumberVerifyActivity.this, WorkerMainActivity.class);
                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            databaseReference.child("Token id").setValue(instanceIdResult.getToken());
                                        }
                                    });
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    finish();
                                    progressDialog.dismiss();
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent = new Intent(PhoneNumberVerifyActivity.this,SetupProfileActivity.class);
                                    databaseReference.child("User id").setValue(task.getResult().getUser().getUid());
                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            databaseReference.child("Token id").setValue(instanceIdResult.getToken());
                                        }
                                    });
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    finish();
                                    progressDialog.dismiss();
                                    startActivity(intent);
                                }
                            }
                            else {
                                checkUser(task);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    task.getException();
                }
            }
        });
        progressDialog.show();
    }
    public void checkUser(final Task<AuthResult> task)
    {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(task.getResult().getUser().getPhoneNumber());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    int count = (int) dataSnapshot.getChildrenCount();
                    if (count>=3 && constraintLayout.getVisibility() == View.VISIBLE){
                        Intent intent = new Intent(PhoneNumberVerifyActivity.this, UserMainActivity.class);
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                databaseReference.child("Token id").setValue(instanceIdResult.getToken());
                            }
                        });
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        progressDialog.dismiss();
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(PhoneNumberVerifyActivity.this,SetupProfileActivity.class);
                        databaseReference.child("User id").setValue(task.getResult().getUser().getUid());
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                databaseReference.child("Token id").setValue(instanceIdResult.getToken());
                            }
                        });
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        progressDialog.dismiss();
                        startActivity(intent);
                    }
                }
               else {
                    Intent intent = new Intent(PhoneNumberVerifyActivity.this, SetupProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void showDialog(final Task<AuthResult> task){

        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.user_type_dialog);
        dialog.show();
        worker=dialog.findViewById(R.id.worker);
        user=dialog.findViewById(R.id.user);
        worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin").child(task.getResult().getUser().getPhoneNumber());
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Intent intent = new Intent(PhoneNumberVerifyActivity.this,SetupProfileActivity.class);
                        databaseReference.child("User id").setValue(task.getResult().getUser().getUid());
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                databaseReference.child("Token id").setValue(instanceIdResult.getToken());
                            }
                        });
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        progressDialog.dismiss();
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

       user.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(task.getResult().getUser().getPhoneNumber());
               databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       Intent intent = new Intent(PhoneNumberVerifyActivity.this, SetupProfileActivity.class);
                       databaseReference.child("User id").setValue(task.getResult().getUser().getUid());
                       FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                           @Override
                           public void onSuccess(InstanceIdResult instanceIdResult) {
                               databaseReference.child("Token id").setValue(instanceIdResult.getToken());
                           }
                       });
                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       finish();
                       progressDialog.dismiss();
                       startActivity(intent);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });

           }
       });
    }
}
