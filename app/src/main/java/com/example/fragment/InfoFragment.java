package com.example.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.common.Common;
import com.example.model.Account;
import com.example.model.Restaurants;
import com.example.restaurant.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class InfoFragment extends Fragment {
    LinearLayout changeImage;
    CircleImageView imgRestaurant;
    ImageView imgAccFix1, imgAccFix2, imgAccFix3;
    EditText edtRestaurantName,edtRestaurantAddress, edtRestaurantPhone;
    Button btnUpdateInfo, btnChangePassword;
    
    DatabaseReference restaurantRef, accountRef;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    ProgressDialog progressDialog;
    StorageReference storageReference;

    String mUri;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        
        initView(view);
        
        restaurantRef = FirebaseDatabase.getInstance().getReference("Restaurants").child(Common.ID);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        restaurantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Restaurants restaurants = snapshot.getValue(Restaurants.class);
                if (restaurants.getImageURL().equals("default")){
                    imgRestaurant.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getContext()).load(restaurants.imageURL).into(imgRestaurant);
                }

                mUri = restaurants.imageURL;

                edtRestaurantName.setText(restaurants.getName());
                edtRestaurantAddress.setText(restaurants.getAddress());
                edtRestaurantPhone.setText(restaurants.getPhone());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addEvent();
        return view;
    }

    private void addEvent() {
        imgAccFix1.setOnClickListener(v -> edtRestaurantName.setFocusable(true));
        imgAccFix2.setOnClickListener(v -> edtRestaurantAddress.setFocusable(true));
        imgAccFix3.setOnClickListener(v -> edtRestaurantPhone.setFocusable(true));

        changeImage.setOnClickListener(v -> openImage());

        btnUpdateInfo.setOnClickListener(v -> {
            String name = edtRestaurantName.getText().toString();
            String address = edtRestaurantAddress.getText().toString();
            String phone = edtRestaurantPhone.getText().toString();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("address", address);
            hashMap.put("imageURL", mUri);
            hashMap.put("name", name);
            hashMap.put("phone", phone);

            
            restaurantRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Có lỗi xảy ra vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        btnChangePassword.setOnClickListener(v -> displayDialog());
    }

    private void displayDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.change_password_dialog, null);
        final EditText edtCurrentPass = alertLayout.findViewById(R.id.edtCurrentPass);
        final EditText edtNewPass = alertLayout.findViewById(R.id.edtNewPass);
        final EditText edtReNewPass = alertLayout.findViewById(R.id.edtReNewPass);
        final CheckBox cbShowPassword = alertLayout.findViewById(R.id.cbShowPassword);
        cbShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    edtCurrentPass.setTransformationMethod(null);
                    edtNewPass.setTransformationMethod(null);
                    edtReNewPass.setTransformationMethod(null);
                }
                else{
                    edtCurrentPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    edtReNewPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Change Password");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // code for matching password
                String currentPass = edtCurrentPass.getText().toString();
                String newPass = edtNewPass.getText().toString();
                String reNewPass = edtReNewPass.getText().toString();
                accountRef = FirebaseDatabase.getInstance().getReference("Accounts").child(Common.ID);
                accountRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Account account = snapshot.getValue(Account.class);
                        if (currentPass.equals(account.getPassword())){
                            if (newPass.equals(reNewPass)){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("password", newPass);
                                accountRef.updateChildren(hashMap);
                                Toast.makeText(getContext(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }else {
                                Toast.makeText(getContext(), "Mật khẩu không khớp vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getContext(), "Mật khẩu sai vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                dialog.dismiss();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();

    }

    private void initView(View view) {
        changeImage = view.findViewById(R.id.changeImage);
        
        imgRestaurant = view.findViewById(R.id.imgRestaurant);
        imgAccFix1 = view.findViewById(R.id.imgAccFix1);
        imgAccFix2 = view.findViewById(R.id.imgAccFix2);
        imgAccFix3 = view.findViewById(R.id.imgAccFix3);
        
        edtRestaurantName = view.findViewById(R.id.edtRestaurantName);
        edtRestaurantName.setFocusable(false);

        edtRestaurantAddress = view.findViewById(R.id.edtRestaurantAddress);
        edtRestaurantAddress.setFocusable(false);

        edtRestaurantPhone = view.findViewById(R.id.edtRestaurantPhone);
        edtRestaurantPhone.setFocusable(false);
        
        btnUpdateInfo = view.findViewById(R.id.btnUpdateInfo);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri dowloadUri = task.getResult();
                        assert dowloadUri != null;
                        mUri = dowloadUri.toString();
                        Glide.with(getContext()).load(mUri).into(imgRestaurant);
                    } else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}