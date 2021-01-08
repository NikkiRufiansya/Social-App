package com.codetalenta.socialapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {

    EditText name, lastName;
    Button btnContinue;
    TextView selectImage;
    CircleImageView imageProfile;
    static final int GALLERY_ADD_PROFILE = 1;
    Bitmap bitmap = null;
    SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
    }

    private void init() {
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        name = findViewById(R.id.editTextName);
        lastName = findViewById(R.id.editTextLastName);
        btnContinue = findViewById(R.id.btnContinue);
        selectImage = findViewById(R.id.selectPhoto);
        imageProfile = findViewById(R.id.profile_image);
        selectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_ADD_PROFILE);
        });
        btnContinue.setOnClickListener(v -> {
            if (validate()) {
                saveUserInfo();

            }
        });
    }

    private void saveUserInfo() {
        String namaPendek = name.getText().toString().trim();
        String namaPanjang = lastName.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.SAVE_USER_INFO, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("photo", object.getString("photo"));
                    editor.apply();
                    startActivity(new Intent(UserInfoActivity.this, HomeActivity.class));
                    Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
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
                System.out.println(token);
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer "+token);
                map.put("Content-Type","application/x-www-form-urlencoded");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", namaPendek);
                map.put("lastname", namaPanjang);
                map.put("photo", bitmapToString(bitmap));
                return map;
            }

        };



        RequestQueue queue = Volley.newRequestQueue(UserInfoActivity.this);
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

    private boolean validate() {
        if (name.getText().toString().isEmpty()) {
            Toast.makeText(UserInfoActivity.this, "Email Kosong", Toast.LENGTH_SHORT).show();
        } else if (lastName.getText().toString().isEmpty()) {
            Toast.makeText(UserInfoActivity.this, "Passowrd Kosong", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ADD_PROFILE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            imageProfile.setImageURI(imageUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}