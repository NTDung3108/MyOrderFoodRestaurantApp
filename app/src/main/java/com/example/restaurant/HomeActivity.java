package com.example.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.common.Common;
import com.example.fragment.FoodsFragment;
import com.example.fragment.InfoFragment;
import com.example.fragment.OrderFragment;
import com.example.fragment.ReportFragment;
import com.example.model.Restaurants;
import com.example.notification.NotificationService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer_layout_Home;
    private TextView nav_text_name;
    private CircleImageView imgUser;
    NavigationView nav_view_home;

    private static final int FRAGMENT_ORDER = 0;
    private static final int FRAGMENT_FOODS = 1;
    private static final int FRAGMENT_REPORT = 2;
    private static final int FRAGMENT_INFO = 3;

    private int currentFragment = FRAGMENT_ORDER;

    String id;

    DatabaseReference restaurantRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);

        Toolbar toolbarHome = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbarHome);

        drawer_layout_Home = findViewById(R.id.drawer_layout_Home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer_layout_Home, toolbarHome,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout_Home.addDrawerListener(toggle);
        toggle.syncState();

        nav_view_home = findViewById(R.id.nav_view_home);
        nav_view_home.setNavigationItemSelectedListener(this);
        nav_view_home.getMenu().findItem(R.id.nav_order).setChecked(true);
        View header = nav_view_home.getHeaderView(0);
        nav_text_name = header.findViewById(R.id.nav_text_name);
        imgUser = header.findViewById(R.id.imgUser);

        replaceFragment(new OrderFragment());
        nav_view_home.getMenu().findItem(R.id.nav_order).setChecked(true);

        id = FirebaseAuth.getInstance().getUid();

        restaurantRef = FirebaseDatabase.getInstance().getReference("Restaurants");
        restaurantRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Restaurants restaurants = snapshot.getValue(Restaurants.class);
                nav_text_name.setText(restaurants.getName());

                if (restaurants.getImageURL().equals("default")){
                    imgUser.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(HomeActivity.this).load(restaurants.getImageURL()).into(imgUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout_Home.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_Home.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_order) {
            if (currentFragment != FRAGMENT_ORDER) {
                replaceFragment(new OrderFragment());
                currentFragment = FRAGMENT_ORDER;
                nav_view_home.getMenu().findItem(R.id.nav_order).setChecked(true);
                nav_view_home.getMenu().findItem(R.id.nav_foods).setChecked(false);
                nav_view_home.getMenu().findItem(R.id.nav_report).setChecked(false);
                nav_view_home.getMenu().findItem(R.id.nav_account).setChecked(false);
            }
        } else if (id == R.id.nav_foods) {
            if (currentFragment != FRAGMENT_FOODS) {
                replaceFragment(new FoodsFragment());
                currentFragment = FRAGMENT_FOODS;
                nav_view_home.getMenu().findItem(R.id.nav_foods).setChecked(true);
                nav_view_home.getMenu().findItem(R.id.nav_order).setChecked(false);
                nav_view_home.getMenu().findItem(R.id.nav_report).setChecked(false);
                nav_view_home.getMenu().findItem(R.id.nav_account).setChecked(false);
            }
        } else if (id == R.id.nav_report) {
            if (currentFragment != FRAGMENT_REPORT) {
                replaceFragment(new ReportFragment());
                currentFragment = FRAGMENT_REPORT;
                nav_view_home.getMenu().findItem(R.id.nav_report).setChecked(true);
                nav_view_home.getMenu().findItem(R.id.nav_order).setChecked(false);
                nav_view_home.getMenu().findItem(R.id.nav_foods).setChecked(false);
                nav_view_home.getMenu().findItem(R.id.nav_account).setChecked(false);
            }
        } else if (id == R.id.nav_account) {
            if (currentFragment != FRAGMENT_INFO) {
                replaceFragment(new InfoFragment());
                currentFragment = FRAGMENT_INFO;
                nav_view_home.getMenu().findItem(R.id.nav_account).setChecked(true);
                nav_view_home.getMenu().findItem(R.id.nav_report).setChecked(false);
                nav_view_home.getMenu().findItem(R.id.nav_order).setChecked(false);
                nav_view_home.getMenu().findItem(R.id.nav_foods).setChecked(false);
            }
        } else if (id == R.id.nav_logout) {
            Paper.book().delete(Common.USER_KEY);
            Paper.book().delete(Common.PDW_KEY);

            Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show();

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        }

        drawer_layout_Home.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentFrame, fragment);
        transaction.commit();
    }
}