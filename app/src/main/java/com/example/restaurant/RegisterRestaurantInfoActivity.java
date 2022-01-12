package com.example.restaurant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.common.Common;
import com.example.model.Restaurants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterRestaurantInfoActivity extends AppCompatActivity {
    RelativeLayout layoutAvatar;
    CircleImageView imageAvatar;
    EditText edtName, edtAddress;
    Button btnComplete;


    private static final  int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    DatabaseReference restaurantRef;

    String mUri = "default";
    static String phone, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_restaurant_info);

        initView();

        phone = getIntent().getStringExtra("SDT");
        id = Common.ID;

        restaurantRef = FirebaseDatabase.getInstance().getReference("Restaurants").child(id);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        layoutAvatar.setOnClickListener(v -> openImage());

        btnComplete.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String address = edtAddress.getText().toString();

            Restaurants restaurants = new Restaurants(name, address, mUri, phone);

            restaurantRef.setValue(restaurants).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Intent intent = new Intent(RegisterRestaurantInfoActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        });
    }

    private void initView() {
        layoutAvatar = findViewById(R.id.layoutAvatar);
        imageAvatar = findViewById(R.id.imageAvatar);
        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtAddress);
        btnComplete = findViewById(R.id.btnComplete);
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data!= null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        progressDialog = new ProgressDialog(RegisterRestaurantInfoActivity.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()){
                    throw task.getException();
                }

                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Uri dowloadUri = task.getResult();
                    assert dowloadUri != null;
                    mUri = dowloadUri.toString();
                    Glide.with(RegisterRestaurantInfoActivity.this).load(mUri).into(imageAvatar);
                }else {
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        }else {
            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}