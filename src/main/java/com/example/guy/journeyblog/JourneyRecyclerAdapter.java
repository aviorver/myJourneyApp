package com.example.guy.journeyblog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class JourneyRecyclerAdapter extends RecyclerView.Adapter<JourneyRecyclerAdapter.ViewHolder> {
    public List<JourneyPost> post_list;
    public Context context;
    public FirebaseAuth mAuth;
    private List<User> user_list;
    public FirebaseFirestore firebaseFirestore;
    private AlertDialog alertDialog;

    public JourneyRecyclerAdapter(List<JourneyPost> postList, List<User> user_list)
    {
        post_list=postList;
        this.user_list=user_list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.journey_list_item,viewGroup,false);
            context=viewGroup.getContext();
            mAuth = FirebaseAuth.getInstance();
            firebaseFirestore= FirebaseFirestore.getInstance();
        alertDialog = new SpotsDialog(view.getContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        //viewHolder.setIsRecyclable(false);
        final String PostId = post_list.get(i).Postid;
        final String currentUserId = mAuth.getCurrentUser().getUid();
        String desc_data = post_list.get(i).getDesc();
        viewHolder.setDescView(desc_data);
        String img_data = post_list.get(i).getImage_url();
        String tumbUri = post_list.get(i).getImage_thumb();
        viewHolder.setJourneyImg(img_data, tumbUri);
        String journey_user_id = post_list.get(i).getUser_id();
        if (journey_user_id.equals(currentUserId)) {
            viewHolder.delete_btn.setEnabled(true);
            viewHolder.delete_btn.setVisibility(View.VISIBLE);
        }

        if (currentUserId != null) {

            viewHolder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.show();
                    firebaseFirestore.collection("Posts").document(PostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            viewHolder.delete_btn.setEnabled(false);
                            viewHolder.delete_btn.setVisibility(View.INVISIBLE);
                            post_list.remove(i);
                            user_list.remove(i);
                            notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
                    });
                }
            });
            viewHolder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent commentIntent = new Intent(context, NotesActivity.class);
                    commentIntent.putExtra("postID", PostId);
                    context.startActivity(commentIntent);
                }
            });

            String user_name = user_list.get(i).getName();
            String user_img = user_list.get(i).getImage();
            viewHolder.setUserData(user_name, user_img);

            try {
                long millisec = post_list.get(i).getTimestamp().getTime();
                String dateString = android.text.format.DateFormat.format("MM/dd/yyyy", new Date(millisec)).toString();
                viewHolder.setTime(dateString);
            } catch (Exception e) {

            }

        }
    }
    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
    private TextView descView,journeyTime,UserName,likeCounter,commentsCounter;
    private CircleImageView userImage;
    private View mView;
        private Button delete_btn;

        private ImageView journeyImageView,like,comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            commentsCounter = mView.findViewById(R.id.comments_text);
            delete_btn=mView.findViewById(R.id.delete_post_btn);
            comment = mView.findViewById(R.id.comment_btn);

        }

        public void setDescView(String desc) {
            this.descView =mView.findViewById(R.id.journey_desc);
            descView.setText(desc);
        }
        public void setJourneyImg(String downloadUri, String tumbUri)
        {
        journeyImageView=mView.findViewById(R.id.journey_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(Glide.with(context).load(tumbUri)).into(journeyImageView);
        }
        public void setTime(String time)
        {
            journeyTime = mView.findViewById(R.id.journey_date);
            journeyTime.setText(time);
        }
        public void setUserData(String name,String img)
        {
            UserName = mView.findViewById(R.id.journey_username);
            userImage = mView.findViewById(R.id.journey_user_image);
            UserName.setText(name);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(img).into(userImage);
        }
    //    public void updateLikeCounts(int count)
//        {
//            likeCounter.setText(count+" Likes");
//        }

        public void updateCommentsCounts(int counter) {
            commentsCounter.setText(counter+context.getString(R.string.note));
        }
    }
}
