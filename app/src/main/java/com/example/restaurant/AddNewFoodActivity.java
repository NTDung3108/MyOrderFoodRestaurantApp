package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.common.Common;
import com.example.model.Category;
import com.example.model.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class AddNewFoodActivity extends AppCompatActivity {

    RelativeLayout addNewImageFood;
    ImageView imgNewFood, imgBack;
    EditText edtNewFoodName, edtNewFoodPrice, edtNewFoodDiscount, edtNewFoodDescription;
    Spinner spNFCategory;
    Button btnAddNewFood;

    int stt;
    String id;
    String menuId;

    DatabaseReference categoryRef, foodRef;

    ArrayList<Category> categories = new ArrayList<>();
    ArrayList<String> listName = new ArrayList<>();

    ArrayAdapter<String> adapter;

    private static final  int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    String mUri = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_food);

        stt = getIntent().getIntExtra("STT", -1);
        id = Common.ID+stt;

        Log.d("ID::::::::::::::", id);

        initView();

        categoryRef = FirebaseDatabase.getInstance().getReference("Category");
        storageReference = FirebaseStorage.getInstance().getReference("images")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categories.clear();
                listName.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Category category = snapshot.getValue(Category.class);
                    categories.add(category);
                    listName.add(category.getName());
                }
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, listName);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spNFCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addEvent();
    }

    private void addEvent() {
        spNFCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                menuId = getCategoryID(listName.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        imgBack.setOnClickListener(v -> finish());

        addNewImageFood.setOnClickListener(v -> openImage());

        btnAddNewFood.setOnClickListener(v -> {
            foodRef = FirebaseDatabase.getInstance().getReference("Foods").child(id);

            String name = edtNewFoodName.getText().toString();
            double price = Double.valueOf(edtNewFoodPrice.getText().toString());
            double discount = Double.valueOf(edtNewFoodDiscount.getText().toString());
            String restaurant = Common.ID;
            String description = edtNewFoodDescription.getText().toString();
            double rating = 0.0;

            if (name.isEmpty() || description.isEmpty() || mUri.equals("default")){
                Toast.makeText(this, "Bạn đã nhập thiếu thông tin vui lòng thử lại", Toast.LENGTH_LONG).show();
            }else {
                Food food = new Food(id,name,mUri,price,discount,menuId,restaurant,description,rating);
                foodRef.setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AddNewFoodActivity.this, "Thêm món ăn mới thành công", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddNewFoodActivity.this, "Có lỗi xảy ra vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void initView() {
        addNewImageFood = findViewById(R.id.addNewImageFood);
        imgNewFood = findViewById(R.id.imgNewFood);
        imgBack = findViewById(R.id.imgBack);
        edtNewFoodName = findViewById(R.id.edtNewFoodName);
        edtNewFoodPrice = findViewById(R.id.edtNewFoodPrice);
        edtNewFoodDiscount = findViewById(R.id.edtNewFoodDiscount);
        edtNewFoodDescription = findViewById(R.id.edtNewFoodDescription);
        btnAddNewFood = findViewById(R.id.btnAddNewFood);
        spNFCategory = findViewById(R.id.spNFCategory);
    }

    private String getCategoryID(String categoryName) {
        for (Category category : categories) {
            if (category.getName().equals(categoryName))
                return category.getId();
        }
        return "null";
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
        progressDialog = new ProgressDialog(AddNewFoodActivity.this);
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
                    Glide.with(AddNewFoodActivity.this).load(mUri).into(imgNewFood);
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