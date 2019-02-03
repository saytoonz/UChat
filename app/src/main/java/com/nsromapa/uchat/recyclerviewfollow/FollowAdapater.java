package com.nsromapa.uchat.recyclerviewfollow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nsromapa.uchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nsromapa.uchat.usersInfos.UserInformation;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class FollowAdapater extends RecyclerView.Adapter<FollowViewHolders> {

    private List<FollowObject> usersList;
    private Context context;

    public FollowAdapater(List<FollowObject> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    public FollowViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recylerview_followers_item, null);
        FollowViewHolders rcv = new FollowViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final FollowViewHolders holder, int position) {
        holder.mName.setText(usersList.get(position).getName());
        holder.mIndex.setText(usersList.get(position).getIndex());


        if (usersList.get(position).getProfileImageUrl() == null
                || usersList.get(position).getProfileImageUrl().equals("default")) {
            holder.profileImageView.setImageResource(R.drawable.profile_image);
        } else {
            Picasso.get()
                    .load(usersList.get(position).getProfileImageUrl())
                    .placeholder(R.drawable.profile_image)
                    .into(holder.profileImageView);
        }


        if (UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())) {
            //If following set the image for following
            holder.mFollow.setImageResource(R.drawable.ic_remove_red_24dp);
        } else {
            //If Not Following set image for follow
            holder.mFollow.setImageResource(R.drawable.ic_group_add_81d4fa_24dp);
        }

        holder.mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (!UserInformation.listFollowing.contains(usersList.get(holder.getLayoutPosition()).getUid())) {
                    //Add to followers and set image for remove from followers
                    holder.mFollow.setImageResource(R.drawable.ic_remove_red_24dp);
                    FollowUser(userId,usersList.get(holder.getLayoutPosition()).getUid());

                } else {
                    //Remove from followers and set image for follow
                    holder.mFollow.setImageResource(R.drawable.ic_group_add_81d4fa_24dp);
                    UnFollowUser(userId, usersList.get(holder.getLayoutPosition()).getUid());

                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return this.usersList.size();
    }



    private void FollowUser(final String myUserId, final String otherUserId){

        ///Add user to the other person's followers
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(otherUserId)
                .child("followers").child(myUserId)
                .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //// Add other person to user's followers
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(myUserId).child("following")
                            .child(otherUserId).setValue(true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful()){
                                     HashMap<String,String> followNotificationMap = new HashMap<>();
                                     followNotificationMap.put("from",myUserId);
                                     followNotificationMap.put("type","request");


                                     //Add this follow action to Notification
                                     FirebaseDatabase.getInstance().getReference()
                                             .child("Notifications").child(otherUserId).push()
                                             .setValue(followNotificationMap)
                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     if (task.isSuccessful()){

                                                         ///Try adding users as friends
                                                         AddUsersAsFriends(myUserId,otherUserId);

                                                     }
                                                 }
                                             });

                                 }
                                }
                            });
                }
            }
        });

    }
    private void UnFollowUser(final String myUserId, final String otherUserId) {
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(otherUserId)
                .child("followers").child(myUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("users").child(myUserId)
                            .child("following").child(otherUserId).removeValue();
                }
            }
        });
    }
    private static void AddUsersAsFriends(final String myUserId, final String otherUserId){

        ////Check to see if other user has already followed the user, then add them as friends
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(otherUserId)
                .child("following").child(myUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            ////Check to see if other user has already followed the user, then add them as friends
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users").child(otherUserId)
                                    .child("friends").child(myUserId)
                                    .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("users").child(myUserId)
                                                .child("friends").child(otherUserId)
                                                .setValue(true);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
