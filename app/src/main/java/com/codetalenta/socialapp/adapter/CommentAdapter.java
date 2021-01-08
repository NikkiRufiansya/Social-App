package com.codetalenta.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codetalenta.socialapp.R;
import com.codetalenta.socialapp.models.Comments;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyView> {

    private Context context;
    private ArrayList<Comments> list;

    public CommentAdapter(Context context, ArrayList<Comments> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        Comments comments = new Comments();
        Picasso.get().load(list.get(position).getUsers().getPhoto()).into(holder.imageCommentProfile);
        holder.textComment.setText(list.get(position).getComment());
        holder.textCommentName.setText(list.get(position).getUsers().getUserName());
        holder.textDateComment.setText(list.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        CircleImageView imageCommentProfile;
        TextView textCommentName, textDateComment, textComment;

        public MyView(@NonNull View itemView) {
            super(itemView);
            imageCommentProfile = itemView.findViewById(R.id.imageCommentProfile);
            textComment = itemView.findViewById(R.id.textComment);
            textCommentName = itemView.findViewById(R.id.textCommentName);
            textDateComment = itemView.findViewById(R.id.textDateComment);
        }
    }
}
