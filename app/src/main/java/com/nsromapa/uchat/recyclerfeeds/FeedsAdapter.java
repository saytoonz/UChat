package com.nsromapa.uchat.recyclerfeeds;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nsromapa.uchat.R;

import java.util.List;

public class FeedsAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    List<FeedsObjects> postLists;

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recylerview_fragment_feeds_item,viewGroup,false);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder feedViewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }
}
