package com.example.guy.journeyblog;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class ToDoFragment extends Fragment {


    public ToDoFragment() {
    }
    private FirebaseAuth firebaseAuth;

    List<ToDoItem> toDoItemList = new ArrayList<>();
    FirebaseFirestore firebaseFirestore;
    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;
    public MaterialEditText title,description;
    public boolean isUpdate=false;
    ListItemAdapter adapter;
    String current_user;
    AlertDialog alertDialog;
    public String idUpdate ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
         current_user = firebaseAuth.getCurrentUser().getUid();
        alertDialog = new SpotsDialog(getContext());
        title =  view.findViewById(R.id.title);
        description =  view.findViewById(R.id.description);
        fab =  view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isUpdate)
                {
                    setData(title.getText().toString(),description.getText().toString());
                    title.setText("");
                    description.setText("");
                }
                else
                {

                    updateData(title.getText().toString(),description.getText().toString());
                    isUpdate=!isUpdate;
                    title.setText("");
                    description.setText("");
                }
            }
        });
        listItem = (RecyclerView) view.findViewById(R.id.listTodo);
        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        listItem.setLayoutManager(layoutManager);

        loadData();
        return view;
    }
    private void updateData(final String title, String description)
    {
        firebaseFirestore.collection("ToDoList").document(idUpdate).update("title",title,"description",description).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
           Toast.makeText(getActivity(),getString(R.string.updated),Toast.LENGTH_SHORT).show();
            }
        });

        firebaseFirestore.collection("ToDoList").document(idUpdate).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                loadData();
            }
        });
    }
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        if(item.getTitle().equals(getString(R.string.delete)))
            deleteItem(item.getOrder());
        return super.onContextItemSelected(item);
    }
    private void deleteItem(int index)
    {
        firebaseFirestore.collection("ToDoList").document(toDoItemList.get(index).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            loadData();
            }
        });
    }
    private void setData(String title,String description)
    {
        String id = UUID.randomUUID().toString();
        Map<String,Object> todo = new HashMap<>();
        todo.put("id",id);
        todo.put("title",title);
        todo.put("description",description);
        todo.put("currentid",current_user);

        firebaseFirestore.collection("ToDoList").document(id).set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            loadData();
            }
        });
    }
    private void loadData() {
        alertDialog.show();
        if(toDoItemList.size()>0)
            toDoItemList.clear();
        firebaseFirestore.collection("ToDoList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult())
                {
                    ToDoItem todo = new ToDoItem(doc.getString("id"),doc.getString("title"),doc.getString("description"),doc.getString("currentid"));
                    if(todo.getCurrentid().equals(current_user))
                    toDoItemList.add(todo);
                }
                adapter = new ListItemAdapter(ToDoFragment.this, toDoItemList);
                listItem.setAdapter(adapter);
                alertDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),getString(R.string.error)+e.getMessage(),Toast.LENGTH_SHORT);
            }
        });
    }
}
