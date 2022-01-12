package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common.Common;
import com.example.model.Account;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.concurrent.TimeUnit;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText edtPhone, edtLoginPassword;
    CheckBox cbRemember;
    Button btnLogin2;
    TextView txtForgotPassword;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        Paper.init(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Intent intent = new Intent(LoginActivity.this, OTPVerifyActivity.class);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("status", "signin");
                startActivity(intent);

            }
        };

        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PhoneVerifyActivity.class);
            intent.putExtra("status","ForgotPassword");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        btnLogin2.setOnClickListener(v -> {
            String phone =  edtPhone.getText().toString();
            String pdw = edtLoginPassword.getText().toString();
            DatabaseReference accountRef = database.getReference("Accounts").child("+1"+phone);
            if (Common.isConnectedToInternet(getBaseContext())) {
                if (cbRemember.isChecked()) {
                    Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                    Paper.book().write(Common.PDW_KEY, edtLoginPassword.getText().toString());
                }
                final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                mDialog.setMessage("wait....");
                mDialog.show();

                accountRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Account account = dataSnapshot.getValue(Account.class);
                            BCrypt.Result result = BCrypt.verifyer().verify(pdw.toCharArray(), account.getPassword());
                            if (account.getPhone().equals(phone) && result.verified
                                    && account.getRole().equals("restaurant")) {
                                if (account.getIsLockUp().equals("0")){
                                    phoneVerify("+1"+phone);
                                    mDialog.dismiss();
                                    finish();
                                }else {
                                    mDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                                }
                            }
                        }else {
                            mDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else {
                Toast.makeText(LoginActivity.this,"please check your network connection",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initView() {
        edtPhone = findViewById(R.id.edtPhone);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        cbRemember = findViewById(R.id.cbRemember);
        btnLogin2 = findViewById(R.id.btnLogin2);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
    }
    public void phoneVerify(String phone){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}