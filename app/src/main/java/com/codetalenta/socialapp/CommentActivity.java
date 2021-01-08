package com.codetalenta.socialapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codetalenta.socialapp.adapter.CommentAdapter;
import com.codetalenta.socialapp.fragments.HomeFragment;
import com.codetalenta.socialapp.models.Comments;
import com.codetalenta.socialapp.models.Posts;
import com.codetalenta.socialapp.models.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Comments> arrayList;
    private CommentAdapter adapter;
    public int postId;
    private ImageView cancelComments;
    private SharedPreferences preferences;
    private EditText textAddComment;
    private ImageButton btnAddComment;
    public static int postPosition = 0;
    public SharedPreferences userPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();
    }

    private void init() {
        userPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        postPosition = getIntent().getIntExtra("postPosition", -1);
        postId = getIntent().getIntExtra("postId", 0);
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyclerComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cancelComments = findViewById(R.id.cancelComments);
        textAddComment = findViewById(R.id.textAddComment);
        btnAddComment = findViewById(R.id.btnAddComment);
        btnAddComment.setOnClickListener(v -> {
            addComments();
        });
        cancelComments.setOnClickListener(v -> {
            super.onBackPressed();
        });
        getComments();
    }

    private void addComments() {
        String commentTxt = textAddComment.getText().toString();
        if (commentTxt.length() > 0) {
            StringRequest request = new StringRequest(Request.Method.POST, Constant.ADD_COMMENTS, response -> {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("success")) {
                        JSONObject comment = object.getJSONObject("comment");
                        JSONObject user = comment.getJSONObject("user");

                        Users users = new Users();
                        users.setId(user.getInt("id"));
                        users.setUserName(user.getString("name")+" "+user.getString("lastname"));
                        users.setPhoto(Constant.URL+"storage/profiles/"+user.getString("photo"));

                        Comments comments = new Comments();
                        comments.setUsers(users);
                        comments.setId(comment.getInt("id"));
                        comments.setDate(comment.getString("created_at"));
                        comments.setComment(comment.getString("comment"));

                        Posts posts = HomeFragment.arrayList.get(postPosition);
                        posts.setComments(posts.getComments()+1);
                        HomeFragment.arrayList.set(postPosition,posts);
                        HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();

                        arrayList.add(comments);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        textAddComment.setText("");
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
                    postId = getIntent().getIntExtra("postId", 0);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", postId + "");
                    map.put("comment", commentTxt);
                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }

    }


    private void getComments() {
        arrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.COMMENTS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray comments = object.getJSONArray("comments");
                    for (int i = 0; i < comments.length(); i++) {
                        JSONObject comment = comments.getJSONObject(i);
                        JSONObject users = comment.getJSONObject("user");

                        Users users1 = new Users();
                        users1.setId(users.getInt("id"));
                        users1.setPhoto(Constant.URL+"storage/profiles/"+users.getString("photo"));
                        users1.setUserName(users.getString("name")+" "+users.getString("lastname"));

                        Comments comments1 = new Comments();
                        comments1.setId(comment.getInt("id"));
                        comments1.setUsers(users1);
                        comments1.setDate(comment.getString("created_at"));
                        comments1.setComment(comment.getString("comment"));
                        arrayList.add(comments1);
                    }

                    adapter = new CommentAdapter(this, arrayList);
                    recyclerView.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                map.put("Content-Type", "application/x-www-form-urlencoded");
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                postId = getIntent().getIntExtra("postId", 0);
                HashMap<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/x-www-form-urlencoded");
                map.put("id", postId + "");

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}