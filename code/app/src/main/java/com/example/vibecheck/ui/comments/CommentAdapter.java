/**
 *  This class is the comment adapter for the recyclerview that holds comments in the MyMoodDisplayFragment and UserMoodDisplayFragment
 *  java classes. It populates the fields within the comment_item.xml layout file for an individual comment so that comment can be added
 *  to the recyclerview as intended.
 *
 *  No outstanding issues with this class.
 */

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

    /**
     *  This class is the interface comment view holder for the recyclerview that holds comments in the MyMoodDisplayFragment and UserMoodDisplayFragment.
     *  Contains the fields within the comment_item.xml layout file for an individual comment so that comment can be added to the recyclerview as intended.
     */
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

    /**
     * This is the implementation of the comment view holder for the recyclerview that holds comments in the MyMoodDisplayFragment and UserMoodDisplayFragment
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    /**
     * The onBindViewHolder() function takes each comment object from its position in the comment list and populates the fields within the comment_item.xml layout file
     * with the correct information taken from the comment object
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
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

        //Attempt to get the display name of the user associated with the comment
        String username = comment.getUsername();
        MoodUtils.getDisplayName(username, displayName -> {
            if (displayName != null) {
                holder.commentDisplayName.setText(displayName);
            }
        });
    }

    /**
     * This function returns the number of comments in the comment list, was intended to display the number of comments
     * a mood event has but was a low priority additional feature not implemented.
     * @return
     *      Returns the number of comments in the comment list
     */
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    /**
     * Sets the comments list for the adapter and notifies the adapter that the data set has changed.
     * @param comments
     */
    public void setComments(List<Comment> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }
}