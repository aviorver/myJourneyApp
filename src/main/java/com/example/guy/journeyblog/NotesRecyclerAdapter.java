package com.example.guy.journeyblog;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    public List<NoteItem> noteItemList;
    public Context context;

    public NotesRecyclerAdapter(List<NoteItem> noteItemList){

        this.noteItemList = noteItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_item_list, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String commentMessage = noteItemList.get(position).getMessage();
        holder.setComment_message(commentMessage);

    }


    @Override
    public int getItemCount() {

        if(noteItemList != null) {

            return noteItemList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView comment_message;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message){

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

        }

    }

}