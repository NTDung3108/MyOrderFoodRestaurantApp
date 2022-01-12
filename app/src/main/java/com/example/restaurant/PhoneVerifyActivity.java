package com.example.restaurant;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;


public class PhoneVerifyActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    EditText edtRegisterPhone;
    Button btnGetOTP;
    ProgressBar progressBar;
    TextView processText;

    FirebaseAuth mAuth;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    static String ccPhoneNumber, phoneNumber, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        initView();

        status = getIntent().getStringExtra("status");

        ccp.registerCarrierNumberEditText(edtRegisterPhone);

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                ccPhoneNumber = ccp.getFullNumberWithPlus();
                phoneNumber = edtRegisterPhone.getText().toString();
                Toast.makeText(PhoneVerifyActivity.this, ccPhoneNumber, Toast.LENGTH_SHORT).show();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("vn");

        btnGetOTP.setOnClickListener(v -> {
            if (edtRegisterPhone.getText().toString().trim().isEmpty()){
                Toast.makeText(PhoneVerifyActivity.this, "Enter phone number", Toast.LENGTH_SHORT).show();
            }else {
                ccPhoneNumber = ccp.getFullNumberWithPlus().replace("","");
                phoneNumber = edtRegisterPhone.getText().toString().replace("-","");
                Toast.makeText(this, ccPhoneNumber, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(ccPhoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                btnGetOTP.setVisibility(View.VISIBLE);
                processText.setText(e.toString());
                processText.setTextColor(Color.RED);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                btnGetOTP.setVisibility(View.VISIBLE);
                processText.setText("OTP has been send");
                Intent intent = new Intent(getApplicationContext(), OTPVerifyActivity.class);
                intent.putExtra("phone", ccPhoneNumber);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("verificationId", verificationId);
                intent.putExtra("status", status);
                startActivity(intent);

                Log.d("STATUS::::::", status);
            }
        };

    }

    private void initView() {
        ccp = findViewById(R.id.ccp);
        edtRegisterPhone = findViewById(R.id.edtRegisterPhone);
        btnGetOTP = findViewById(R.id.btnGetOTP);
        progressBar = findViewById(R.id.progressBar);
        processText = findViewById(R.id.processText);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        FirebaseAuth.getInstance().signOut();

    }

}