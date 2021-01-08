package com.codetalenta.socialapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codetalenta.socialapp.CommentActivity;
import com.codetalenta.socialapp.Constant;
import com.codetalenta.socialapp.HomeActivity;
import com.codetalenta.socialapp.R;
import com.codetalenta.socialapp.models.Posts;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyView> {

    private Context context;
    private ArrayList<Posts> list;
    private ArrayList<Posts> listAll;
    private SharedPreferences preferences;

    public PostsAdapter(Context context, ArrayList<Posts> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public PostsAdapter.MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.MyView holder, int position) {
        Posts posts = list.get(position);
        Picasso.get().load(Constant.URL + "storage/profiles/" + posts.getUsers().getPhoto()).into(holder.imageProfile);
        Picasso.get().load(Constant.URL + "storage/posts/" + posts.getPhoto()).into(holder.imagePosts);
        holder.postsName.setText(posts.getUsers().getUserName());

        holder.txtComments.setText("View all " + posts.getComments() + " comments");
        holder.txtLikes.setText(posts.getLikes() + " Likes");
        holder.postsDate.setText(posts.getDate());
        holder.postsDesc.setText(posts.getDesc());


        holder.btnLikes.setImageResource(
                posts.isSelfLike() ? R.drawable.ic_baseline_favorite_red : R.drawable.ic_baseline_favorite_border_24
        );

        holder.btnLikes.setOnClickListener(v -> {
            holder.btnLikes.setImageResource(
                    posts.isSelfLike() ? R.drawable.ic_baseline_favorite_border_24 : R.drawable.ic_baseline_favorite_red
            );

            StringRequest request = new StringRequest(Request.Method.POST, Constant.LIKES_POSTS, response -> {
                Posts mPosts = list.get(position);

                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("success")) {
                        mPosts.setSelfLike(!posts.isSelfLike());
                        mPosts.setLikes(mPosts.isSelfLike() ? posts.getLikes() + 1 : posts.getLikes() - 1);
                        list.set(position, mPosts);
                        notifyItemChanged(position);
                        notifyDataSetChanged();
                    } else {
                        holder.btnLikes.setImageResource(
                                posts.isSelfLike() ? R.drawable.ic_baseline_favorite_red : R.drawable.ic_baseline_favorite_border_24
                        );
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
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", posts.getId() + "");
                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        });

        holder.txtComments.setOnClickListener(v -> {
            Intent intent = new Intent(((HomeActivity) context), CommentActivity.class);
            intent.putExtra("postId", posts.getId());
            intent.putExtra("postPosition", position);
            context.startActivity(intent);
        });

        holder.btnComments.setOnClickListener(v -> {
            Intent intent = new Intent(((HomeActivity) context), CommentActivity.class);
            intent.putExtra("postId", posts.getId());
            intent.putExtra("postPosition", position);
            context.startActivity(intent);
        });

        if (posts.getUsers().getId() == preferences.getInt("id", 0)) {
            holder.btnOptions.setVisibility(View.VISIBLE);
        } else {
            holder.btnOptions.setVisibility(View.GONE);
        }

        holder.btnOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btnOptions);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_edit: {

                        }
                        case R.id.item_delete: {

                        }
                    }
                    return false;
                }

            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Posts> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(listAll);
            } else {
                for (Posts posts : listAll) {
                    if (posts.getDesc().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            posts.getUsers().getUserName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(posts);
                    }
                }

            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends Posts>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    public class MyView extends RecyclerView.ViewHolder {
        CircleImageView imageProfile;
        TextView postsName, postsDate, postsDesc, txtLikes, txtComments;
        ImageButton btnOptions;
        ImageView imagePosts, btnLikes, btnComments;

        public MyView(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfilePost);
            btnOptions = itemView.findViewById(R.id.btnPostOptions);
            imagePosts = itemView.findViewById(R.id.imagePostPhoto);
            btnLikes = itemView.findViewById(R.id.btnPostLike);
            btnComments = itemView.findViewById(R.id.btnPostComment);
            postsName = itemView.findViewById(R.id.textPostName);
            postsDate = itemView.findViewById(R.id.textPostDate);
            postsDesc = itemView.findViewById(R.id.textPostDesc);
            txtLikes = itemView.findViewById(R.id.textPostLikes);
            txtComments = itemView.findViewById(R.id.textPostComments);
        }
    }
}
