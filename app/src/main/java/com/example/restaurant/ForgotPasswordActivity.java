package com.example.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.common.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edtFGPassword, edtFGRePassword;
    Button btnChangPassword;
    String sdt;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtFGPassword = findViewById(R.id.edtFGPassword);
        edtFGRePassword = findViewById(R.id.edtFGRePassword);
        btnChangPassword = findViewById(R.id.btnChangPassword);

        sdt=getIntent().getExtras().getString("phoneNumber");


        final DatabaseReference talbe_user = FirebaseDatabase.getInstance().getReference("Accounts")
                .child(mAuth.getCurrentUser().getPhoneNumber());

        btnChangPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(ForgotPasswordActivity.this);
                    mDialog.setMessage("Vui lòng chờ");
                    mDialog.show();
// code thay đổi mật khẩu
                    String newPass = edtFGPassword.getText().toString();
                    String reNewPass = edtFGRePassword.getText().toString();
                    if (newPass.equals(reNewPass)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("password", newPass);
                        talbe_user.updateChildren(hashMap);
                        Toast.makeText(ForgotPasswordActivity.this,"Mật khẩu thay đổi thành công",Toast.LENGTH_LONG).show();
                        mDialog.dismiss();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        mDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this,"Mật khẩu không khớp vui lòng kiểm tra lại",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this,"Hãy kiểm tra đường truyền Internet của bạn",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}