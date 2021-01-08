package com.codetalenta.socialapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.codetalenta.socialapp.fragments.HomeFragment;
import com.codetalenta.socialapp.fragments.SignInFragment;
import com.codetalenta.socialapp.fragments.SignUpFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {
    FloatingActionButton fab;
    static final int GALLERY_ADD_POST = 2;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager.beginTransaction().replace(R.id.frameHomeContainer, new HomeFragment()).commit();

        init();
    }

    private void init() {
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_ADD_POST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ADD_POST && resultCode == RESULT_OK) {
            Uri img = data.getData();
            Intent intent = new Intent(HomeActivity.this, AddPostActivity.class);
            intent.setData(img);
            startActivity(intent);
        }

    }
}