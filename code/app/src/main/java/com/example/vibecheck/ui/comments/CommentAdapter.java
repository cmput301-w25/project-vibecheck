package com.example.vibecheck.ui.comments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText, commentDisplayName, commentTime;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.comment_text);
            commentDisplayName = itemView.findViewById(R.id.comment_display_name);
            commentTime = itemView.findViewById(R.id.comment_time);
        }
    }

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.commentText.setText(comment.getCommentText());
        holder.commentDisplayName.setText(comment.getUsername());
        if (comment.getTimestamp() != null) {
            holder.commentTime.setText(MoodUtils.timeSincePosting(comment.getTimestamp()));
        } else {
            holder.commentTime.setText("Just now");
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setComments(List<Comment> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }
}

