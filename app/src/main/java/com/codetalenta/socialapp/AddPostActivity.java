package com.codetalenta.socialapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codetalenta.socialapp.fragments.HomeFragment;
import com.codetalenta.socialapp.models.Posts;
import com.codetalenta.socialapp.models.Users;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
    Button btnPost;
    ImageView imgPost, imgCancel;
    EditText textDesc;
    TextView changePhoto;
    Bitmap bitmap = null;
    final static int GALLERY_CHANGE_POST = 3;
    SharedPreferences userPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        init();
    }

    private void init() {
        userPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        btnPost = findViewById(R.id.btnAddPost);
        imgPost = findViewById(R.id.imgAddPost);
        imgCancel = findViewById(R.id.cancelPost);
        textDesc = findViewById(R.id.textDesc);
        changePhoto = findViewById(R.id.chagePhoto);

        imgCancel.setOnClickListener(v -> {
            super.onBackPressed();
        });

        imgPost.setImageURI(getIntent().getData());
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        changePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_CHANGE_POST);
        });

        btnPost.setOnClickListener(v -> {
            if (!textDesc.getText().toString().isEmpty()) {
                post();
            } else {
                Toast.makeText(this, "Post description is required", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post() {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_POSTS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONObject postObject = object.getJSONObject("post");
                    JSONObject userObject = postObject.getJSONObject("user");

                    Users users = new Users();
                    users.setId(userObject.getInt("id"));
                    users.setUserName(userObject.getString("name")+" "+userObject.getString("lastname"));
                    users.setPhoto(userObject.getString("photo"));

                    Posts posts= new Posts();
                    posts.setUsers(users);
                    posts.setId(postObject.getInt("id"));
                    posts.setSelfLike(false);
                    posts.setPhoto(postObject.getString("photo"));
                    posts.setDesc(postObject.getString("desc"));
                    posts.setComments(0);
                    posts.setLikes(0);
                    posts.setDate(postObject.getString("created_at"));

                    HomeFragment.arrayList.add(0,posts);
                    HomeFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                map.put("Content-Type", "application/x-www-form-urlencoded");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("desc", textDesc.getText().toString().trim());
                map.put("photo", bitmapToString(bitmap));
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }
        return "";
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CHANGE_POST && resultCode == RESULT_OK) {
            Uri img = data.getData();
            imgPost.setImageURI(img);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}