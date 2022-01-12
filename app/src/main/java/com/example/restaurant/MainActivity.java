package com.example.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.common.Common;
import com.example.model.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        Paper.init(this);

        String user = Paper.book().read(Common.USER_KEY);
        String pdw = Paper.book().read(Common.PDW_KEY);
        if (user != null && pdw != null) {
            if (!user.isEmpty() && !pdw.isEmpty())
                login(user, pdw);
        }

        btnLogin.setOnClickListener(view ->{
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent registerIntent = new Intent(this, PhoneVerifyActivity.class);
            registerIntent.putExtra("status","Register");
            registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(registerIntent);
        });
    }

    private void login(final String phone, final String pdw) {
        final DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference("Accounts");
        final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Vui lòng chờ ....");
        mDialog.dismiss();
        accountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(phone).exists()){
                    Account account = dataSnapshot.child(phone).getValue(Account.class);
                    if (account.getPhone().equals(phone) && account.getPassword().equals(pdw)
                            && account.getRole().equals("restaurant")) {
                        if (account.getIsLockUp().equals("0")){
                            Common.ID = account.getId();
                            mDialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}