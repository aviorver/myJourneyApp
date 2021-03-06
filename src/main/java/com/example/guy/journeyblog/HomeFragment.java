package com.example.guy.journeyblog;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private List<JourneyPost> post_list;
    private List<User> user_list;

    private RecyclerView journey_list_view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private DocumentSnapshot lastVisible;
    private JourneyRecyclerAdapter journeyRecyclerAdapter;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        journey_list_view = view.findViewById(R.id.journey_list_view);
        post_list = new ArrayList<>();
        user_list = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            journeyRecyclerAdapter = new JourneyRecyclerAdapter(post_list,user_list);
            journey_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
            journey_list_view.setAdapter(journeyRecyclerAdapter);
            Query firstQuery =  firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (e != null) {
                    } else {
                        for (final DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String docId = doc.getDocument().getId();
                                final JourneyPost journeyPost = doc.getDocument().toObject(JourneyPost.class).withId(docId);
                                String journeyUserID = doc.getDocument().getString("user_id");
                                firebaseFirestore.collection("User").document(journeyUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                   if(task.isSuccessful())
                                   {
                                       if(doc.getDocument().getString("user_id").equals(mAuth.getCurrentUser().getUid())) {
                                           User user = task.getResult().toObject(User.class);
                                           user_list.add(user);
                                           post_list.add(journeyPost);
                                           journeyRecyclerAdapter.notifyDataSetChanged();
                                       }
                                   }
                                    }
                                });
                            }
                        }
                    }
                }

            });

        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
