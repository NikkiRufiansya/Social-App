package com.codetalenta.socialapp.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codetalenta.socialapp.Constant;
import com.codetalenta.socialapp.HomeActivity;
import com.codetalenta.socialapp.R;
import com.codetalenta.socialapp.UserInfoActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SignUpFragment extends Fragment {
    private View view;
    private EditText editTextEmail, editTextPassword, editTextPassword2;
    private Button btnRegister;
    TextView signIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        init();
        return view;
    }

    private void init(){
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        btnRegister = view.findViewById(R.id.register);
        signIn = view.findViewById(R.id.textViewSignIn);
        editTextPassword2 = view.findViewById(R.id.editTextPassword2);
        signIn.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignInFragment()).commit();
        });

        btnRegister.setOnClickListener(v->{
            if (validate()){
                register();
            }
        });

    }

    private boolean validate(){
        if (editTextEmail.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Email Kosong", Toast.LENGTH_SHORT).show();
        }else if (editTextPassword.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Passowrd Kosong", Toast.LENGTH_SHORT).show();
        }else if(editTextPassword2.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Confirmasi Password Empty", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    private void register(){
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REGISTER, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONObject user = object.getJSONObject("user");
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user",getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("token", object.getString("token"));
                    editor.putString("name", user.getString("name"));
                    editor.putString("lastname", user.getString("lastname"));
                    editor.putString("photo", user.getString("photo"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    startActivity(new Intent(getContext(), UserInfoActivity.class));
                    getActivity().finish();
                    Toast.makeText(getContext(), "Register Success", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getContext(), "Register Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Register Failed", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(getContext(), "Register Failed", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("Content-Type", "application/json");
                map.put("Accept", "application/json");
                map.put("email", editTextEmail.getText().toString().trim());
                map.put("password", editTextPassword.getText().toString());
                return map;
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> map = new HashMap<>();
//                map.put("Content-Type", "application/json");
//                map.put("Accept", "application/json");
//                return map;
//            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}