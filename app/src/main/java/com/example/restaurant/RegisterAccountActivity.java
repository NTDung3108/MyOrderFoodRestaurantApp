package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.common.Common;
import com.example.model.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class RegisterAccountActivity extends AppCompatActivity {

    MaterialEditText edtPassword, edtRePassword;
    Button btnNext;

    static String phone, id;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        initView();

        phone = getIntent().getStringExtra("phoneNumber");
        id = Common.ID;

        Toast.makeText(this, phone, Toast.LENGTH_SHORT).show();

        DatabaseReference accountRef = FirebaseDatabase.getInstance()
                .getReference("Accounts").child(mAuth.getCurrentUser().getPhoneNumber());

        btnNext.setOnClickListener(v -> {
            final ProgressDialog mDialog = new ProgressDialog(RegisterAccountActivity.this);
            mDialog.setMessage("wait....");
            mDialog.show();

            if (Common.isConnectedToInternet(getApplicationContext())){
                accountRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            mDialog.dismiss();
                            Toast.makeText(RegisterAccountActivity.this,
                                    "the number phone has exist", Toast.LENGTH_LONG).show();
                        }else {
                            mDialog.dismiss();
                            String password = edtPassword.getText().toString();
                            String rePassword = edtRePassword.getText().toString();
                            String role = "restaurant";

                            if (password.equals(rePassword)) {
                                String hashPass = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                                Account account = new Account(id, phone, hashPass, role, "0");
                                accountRef.setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(RegisterAccountActivity.this, "you have success registered", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(RegisterAccountActivity.this, RegisterRestaurantInfoActivity.class);
                                            intent.putExtra("SDT", phone);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterAccountActivity.this, "Mật Khẩu Không Khớp Nhau Vui Lòng Nhập Lại", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else {
                Toast.makeText(RegisterAccountActivity.this, "check internet of you", Toast.LENGTH_LONG).show();
                return;
            }
        });

    }

    private void initView() {
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        btnNext = findViewById(R.id.btnNext);
    }
}