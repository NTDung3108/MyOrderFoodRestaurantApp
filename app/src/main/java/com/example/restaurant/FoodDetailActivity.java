package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adapter.CommentAdapter;
import com.example.model.Category;
import com.example.model.Food;
import com.example.model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodDetailActivity extends AppCompatActivity {
    ImageView imgFoodDetail, imgBack;
    EditText edtFoodName, edtPrice, edtDiscount, edtDescription;
    RatingBar ratingBar;
    RecyclerView rvComment;
    LinearLayout btnUpdate;
    Spinner spCategory;

    Food food;

    DatabaseReference ratingRef, foodRef, categoryRef;

    CommentAdapter commentAdapter;

    ArrayList<Rating> ratings = new ArrayList<>();
    ArrayList<Category> categories = new ArrayList<>();
    ArrayList<String> listName = new ArrayList<>();

    ArrayAdapter<String> adapter;

    private static final  int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    String mUri;

    String menuId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        food = (Food) getIntent().getSerializableExtra("Food");
        mUri = food.getImage();
        menuId = food.getMenuId();

        storageReference = FirebaseStorage.getInstance().getReference("images")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        categoryRef = FirebaseDatabase.getInstance().getReference("Category");

        initView();
        addEvents();
        
        getRatingList(food.getId());
        getCategory();
        
    }

    private void getCategory() {
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
                spCategory.setAdapter(adapter);
                for (Category category : categories){
                    if (category.getId().equals(food.getMenuId())){
                        String myString = category.getName();
                        int position = adapter.getPosition(myString);
                        spCategory.setSelection(position);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRatingList(String foodId) {
        ratingRef = FirebaseDatabase.getInstance().getReference("Rating");
        Query ratingList = ratingRef.orderByChild("foodId").equalTo(foodId);
        ratingList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ratings.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Rating rating = snapshot.getValue(Rating.class);
                    ratings.add(rating);
                }
                commentAdapter = new CommentAdapter(FoodDetailActivity.this, ratings);
                rvComment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addEvents() {
        imgBack.setOnClickListener(v -> {
            finish();
        });

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                menuId = getCategoryID(listName.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnUpdate.setOnClickListener(v -> {
            foodRef = FirebaseDatabase.getInstance().getReference("Foods").child(food.getId());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("description", edtDescription.getText().toString());
            hashMap.put("discount", Double.valueOf(edtDiscount.getText().toString()));
            hashMap.put("image", mUri);
            hashMap.put("menuId", menuId);
            hashMap.put("name", edtFoodName.getText().toString());
            hashMap.put("price", Double.valueOf(edtPrice.getText().toString()));

            foodRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(FoodDetailActivity.this, "Thông tin món ăn đã được cập nhật", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(FoodDetailActivity.this, "Có lỗi! Thông tin món ăn chưa được cập nhật. Vui Lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        imgFoodDetail.setOnClickListener(v -> openImage());
    }

    private void initView() {

        imgFoodDetail = findViewById(R.id.imgFoodDetail);
        Glide.with(this).load(food.getImage()).into(imgFoodDetail);

        imgBack = findViewById(R.id.imgBack);

        edtFoodName = findViewById(R.id.edtFoodName);
        edtFoodName.setText(food.getName());


        edtPrice = findViewById(R.id.edtPrice);
        edtPrice.setText(food.getPrice()+"");


        edtDiscount = findViewById(R.id.edtDiscount);
        edtDiscount.setText(food.getDiscount()+"");


        edtDescription = findViewById(R.id.edtDescription);
        edtDescription.setText(food.getDescription());


        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(Float.parseFloat(food.getRatting()+""));

        rvComment = findViewById(R.id.rvComment);

        rvComment.setLayoutManager(new LinearLayoutManager(FoodDetailActivity.this));

        btnUpdate = findViewById(R.id.btnUpdate);

        spCategory = findViewById(R.id.spCategory);
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
        progressDialog = new ProgressDialog(FoodDetailActivity.this);
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
                    Glide.with(FoodDetailActivity.this).load(mUri).into(imgFoodDetail);
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