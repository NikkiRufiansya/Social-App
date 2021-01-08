package com.codetalenta.socialapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codetalenta.socialapp.Constant;
import com.codetalenta.socialapp.HomeActivity;
import com.codetalenta.socialapp.R;
import com.codetalenta.socialapp.adapter.PostsAdapter;
import com.codetalenta.socialapp.models.Posts;
import com.codetalenta.socialapp.models.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {
    private View view;
    public  static RecyclerView recyclerView;
    public  static ArrayList<Posts> arrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostsAdapter postsAdapter;
    private Toolbar toolbar;
    SharedPreferences userPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        return view;
    }

    private void init() {
        userPref = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerHome);
        swipeRefreshLayout = view.findViewById(R.id.swipeHome);
        toolbar = view.findViewById(R.id.tolbarHome);
        ((HomeActivity) getContext()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        getPosts();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();

            }
        });
    }


    private void getPosts() {
        arrayList = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.POSTS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = object.getJSONArray("posts");
                    System.out.println("post : "+array);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject postObject = array.getJSONObject(i);
                        JSONObject userObject = postObject.getJSONObject("user");

                        Users users = new Users();
                        users.setId(userObject.getInt("id"));
                        users.setUserName(userObject.getString("name") + " " + userObject.getString("lastname"));
                        users.setPhoto(userObject.getString("photo"));

                        Posts posts = new Posts();
                        posts.setId(postObject.getInt("id"));
                        posts.setUsers(users);
                        posts.setLikes(postObject.getInt("likesCount"));
                        posts.setComments(postObject.getInt("commentsCount"));
                        posts.setDate(postObject.getString("created_at"));
                        posts.setDesc(postObject.getString("desc"));
                        posts.setPhoto(postObject.getString("photo"));
                        posts.setSelfLike(postObject.getBoolean("selfLike"));

                        arrayList.add(posts);
                    }


                    postsAdapter = new PostsAdapter(getContext(), arrayList);
                    LinearLayoutManager manager = new GridLayoutManager(getContext(),1);
                    recyclerView.setLayoutManager(manager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(postsAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            swipeRefreshLayout.setRefreshing(false);
        }, error -> {
            error.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                map.put("Content-Type", "application/x-www-form-urlencoded");
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                postsAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}