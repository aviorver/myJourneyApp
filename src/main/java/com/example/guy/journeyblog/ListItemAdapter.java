package com.example.guy.journeyblog;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener
{
    ItemClickListener itemClickListener;
    TextView item_title,item_description;
    public ListItemViewHolder(View itemView)
    {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

        item_title =(TextView) itemView.findViewById(R.id.item_title);
        item_description = itemView.findViewById(R.id.item_description);
    }
     public void setItemClickListener (ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
     }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.selecttheaction);
        menu.add(0,0,getAdapterPosition(),R.string.delete);
    }
}



public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {
    ToDoFragment mainActivity;
    List<ToDoItem> toDoItemList;

    public ListItemAdapter(ToDoFragment mainActivity, List<ToDoItem> toDoItemList) {
        this.mainActivity = mainActivity;
        this.toDoItemList = toDoItemList;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity.getContext());
        View view = inflater.inflate(R.layout.list_item,viewGroup,false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder listItemViewHolder, int position) {

        listItemViewHolder.item_title.setText(toDoItemList.get(position).getTitle());
        listItemViewHolder.item_description.setText(toDoItemList.get(position).getDescription());
        listItemViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                mainActivity.title.setText(toDoItemList.get(position).getTitle());
                mainActivity.description.setText(toDoItemList.get(position).getDescription());
                mainActivity.isUpdate=true;
                mainActivity.idUpdate = toDoItemList.get(position).getId();
            }
        });


    }

    @Override
    public int getItemCount() {
        return toDoItemList.size();
    }
}
