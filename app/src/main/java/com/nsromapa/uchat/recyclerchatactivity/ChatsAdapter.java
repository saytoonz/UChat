package com.nsromapa.uchat.recyclerchatactivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nsromapa.uchat.ChatActivity;
import com.nsromapa.uchat.LocationUtil.MapboxSingleLocationActivity;
import com.nsromapa.uchat.LocationUtil.SingLocationActivity;
import com.nsromapa.uchat.MainActivity;
import com.nsromapa.uchat.R;
import com.nsromapa.uchat.ShowCapturedActivity;
import com.nsromapa.uchat.ViewPostActivity;
import com.nsromapa.uchat.cameraUtils.Config;
import com.nsromapa.uchat.findme.FindMeMapsActivity;
import com.nsromapa.uchat.utils.FormatterUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsViewHolder> {
    List<ChatsObjects> userChatList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Context mContext;
    private  RecyclerView recyclerView;
    private String friendId;

    public  ChatsAdapter(Context context,List<ChatsObjects> userChatList, RecyclerView recyclerView, String friendId) {
        this.userChatList = userChatList;
        this.mContext = context;
        this.recyclerView = recyclerView;
        this.friendId = friendId;
    }


    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_chat_activity_item,  viewGroup, false);
        mAuth = FirebaseAuth.getInstance();
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder chatsViewHolder, final int position) {
        final String currentUserID = mAuth.getCurrentUser().getUid();
        final ChatsObjects messages = userChatList.get(position);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(messages.getFrom());
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild("profileImageUrl")){
//                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
//                    Picasso.get().load(profileImageUrl).placeholder(R.drawable.profile_image).into(chatsViewHolder.receiverImage);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        chatsViewHolder.receiverMessage.setVisibility(View.GONE);
        chatsViewHolder.senderMessage.setVisibility(View.GONE);
        chatsViewHolder.receiver_message_ImageFull.setVisibility(View.GONE);
        chatsViewHolder.sender_message_ImageFull.setVisibility(View.GONE);
        chatsViewHolder.receiver_message_VideoThumbnail_play.setVisibility(View.GONE);
        chatsViewHolder.sender_message_VideoThumbnail_play.setVisibility(View.GONE);
        chatsViewHolder.sender_message_imageVideoUpload.setVisibility(View.GONE);
        chatsViewHolder.sender_imageVideo_progressBar.setVisibility(View.GONE);
        chatsViewHolder.receiverMessageDateTime.setVisibility(View.GONE);
        chatsViewHolder.senderMessageDateTime.setVisibility(View.GONE);
        chatsViewHolder.senderStickerSoundGifImage.setVisibility(View.GONE);
        chatsViewHolder.recieverStickerSoundGifImage.setVisibility(View.GONE);
        chatsViewHolder.senderSoundImage.setVisibility(View.GONE);
        chatsViewHolder.recieverSoundImage.setVisibility(View.GONE);
        chatsViewHolder.receiver_contactFull.setVisibility(View.GONE);
        chatsViewHolder.sender_contactFull.setVisibility(View.GONE);
        chatsViewHolder.receiver_document_attachmentFull.setVisibility(View.GONE);
        chatsViewHolder.sender_document_attachmentFull.setVisibility(View.GONE);
        chatsViewHolder.receiver_message_audioFull.setVisibility(View.GONE);
        chatsViewHolder.sender_message_audioFull.setVisibility(View.GONE);
        chatsViewHolder.receiver_message_findMeFull.setVisibility(View.GONE);
        chatsViewHolder.sender_message_findMeFull.setVisibility(View.GONE);
        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);



        if (messages.getType().equals("text")){

            if ( messages.getFrom().equals(currentUserID)){

                chatsViewHolder.senderMessage.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.senderMessage.setBackgroundResource(R.drawable.sender_messages_layout);
                chatsViewHolder.senderMessage.setText(messages.getMessage());
                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.senderMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });

                switch (messages.getState()) {
                    case "not sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.black_overlay);
                        break;
                    case "sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorInitial);
                        break;
                    case "delivered":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorAccent);
                        break;
                    case "read":
                        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);
                        break;
                }

            }else{

                chatsViewHolder.receiverMessage.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiverMessage.setBackgroundResource(R.drawable.receiver_messages_layout);
                chatsViewHolder.receiverMessage.setText(messages.getMessage());

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.receiverMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }






        else if (messages.getType().equals("image") || messages.getType().equals("video")) {

            final String destinationFilename;
            final String fileExtection;

            if (messages.getType().equals("video")){
                destinationFilename = android.os.Environment.getExternalStorageDirectory()
                        .getPath()+"UChat/Video/";
                fileExtection=".mp4";
            }else{
                destinationFilename = android.os.Environment.getExternalStorageDirectory()
                        .getPath()+"UChat/Image/";
                fileExtection = ".png";
            }


            if ( messages.getFrom().equals(currentUserID)){
               ////If file x video, show play button
                if (messages.getType().equals("video")){
                    chatsViewHolder.sender_message_VideoThumbnail_play.setVisibility(View.VISIBLE);
                }else{
                    chatsViewHolder.sender_message_VideoThumbnail_play.setVisibility(View.GONE);
                }



                chatsViewHolder.sender_message_ImageFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.senderMessageImage.setBackgroundResource(R.drawable.sender_messages_layout);
//                Picasso.get().load(messages.getMessage()).into(chatsViewHolder.senderMessageImage);
                if (!TextUtils.isEmpty(messages.getLocal_location().trim())){

                    File loc = new File(messages.getLocal_location());
                    if ((!loc.exists()) || (!loc.isFile()) ) {
                        loc.delete();

                        chatsViewHolder.sender_message_imageVideoUpload.setVisibility(View.GONE);
                        chatsViewHolder.sender_imageVideo_progressBar.setVisibility(View.GONE);

                        /// load file from online if it does not exist locally
                        ///and has local location....
                        Glide.with(mContext)
                                .asBitmap()
                                .load(messages.getMessage())
                                .apply(new RequestOptions().error(R.drawable.sticker_gif_placeholder))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        chatsViewHolder.senderMessageImage.setImageBitmap(resource);

                                        //View image if it is clicked
                                        chatsViewHolder.senderMessageImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, ShowCapturedActivity.class);
                                                intent.putExtra(Config.KeyName.FILEPATH, messages.getMessage());
                                                intent.putExtra("coming_from","ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                mContext.startActivity(intent);
                                            }
                                        });

                                    }
                                });
                    }else {
                        //Check if message was sent of not.....
                        if (messages.state.equals("not sent") && !messages.getMessage().equals("uploading")){

                            if (messages.getMessage().equals("uploading")){
                                chatsViewHolder.sender_imageVideo_progressBar.setVisibility(View.VISIBLE);
                                chatsViewHolder.sender_message_imageVideoUpload.setVisibility(View.GONE);
                            }else{
                                chatsViewHolder.sender_message_imageVideoUpload.setVisibility(View.VISIBLE);
                                chatsViewHolder.sender_imageVideo_progressBar.setVisibility(View.GONE);
                            }

                            chatsViewHolder.sender_message_imageVideoUpload.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    chatsViewHolder.sender_message_imageVideoUpload.setVisibility(View.GONE);
                                    chatsViewHolder.sender_imageVideo_progressBar.setVisibility(View.VISIBLE);

                                    new ChatUploadAttachment(ChatsAdapter.this,recyclerView, mContext, friendId,
                                            chatsViewHolder.sender_imageVideo_progressBar,
                                            chatsViewHolder.sender_message_imageVideoUpload)
                                            .execute("upload_attachment",
                                                    messages.getMessageID(),
                                                    messages.getLocal_location(),
                                                    messages.getType(),
                                                    messages.getCaption(),
                                                    String.valueOf(position));
                                }
                            });

                        }
                        else{
                            chatsViewHolder.sender_message_imageVideoUpload.setVisibility(View.GONE);
                            chatsViewHolder.sender_imageVideo_progressBar.setVisibility(View.GONE);
                        }


                        //Load file from local source
                        Glide.with(mContext)
                                .asBitmap()
                                .load(messages.getLocal_location())
                                .apply(new RequestOptions().error(R.drawable.sticker_gif_placeholder))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                        chatsViewHolder.senderMessageImage.setImageBitmap(resource);


                                        //View image if it is clicked
                                        chatsViewHolder.senderMessageImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, ShowCapturedActivity.class);
                                                intent.putExtra(Config.KeyName.FILEPATH, messages.getLocal_location());
                                                intent.putExtra("coming_from","ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                mContext.startActivity(intent);
                                            }
                                        });

                                    }
                                });
                    }

                }else{
                    ////File Sent but not exist locally
                    if (!TextUtils.isEmpty(messages.getMessage()) || !messages.getMessage().equals("uploading")) {
                        Glide.with(mContext)
                                .asBitmap()
                                .apply(new RequestOptions().error(R.drawable.sticker_gif_placeholder))
                                .load(messages.getMessage())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                        chatsViewHolder.senderMessageImage.setImageBitmap(resource);

                                         //View image if it is clicked
                                        chatsViewHolder.senderMessageImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, ShowCapturedActivity.class);
                                                intent.putExtra(Config.KeyName.FILEPATH, messages.getMessage());
                                                intent.putExtra("coming_from","ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                mContext.startActivity(intent);
                                            }
                                        });


                                        String newFileName = String.valueOf(System.currentTimeMillis());
                                        if (messages.getType().equals("image")){
                                            saveImage(resource,newFileName,destinationFilename+"Sent/");

                                            ChatsRetrieveBackground insertIntoDBBackground = new ChatsRetrieveBackground(mContext);
                                            insertIntoDBBackground.execute("update_message", messages.getMessageID(), messages.getFrom(), currentUserID,
                                                    messages.getCaption(), messages.getDate(), messages.getTime(), messages.getMessage(), messages.getMessage(),
                                                    messages.getState(), destinationFilename+"Sent/"+newFileName+".png", "yes");
                                        }

                                    }
                                });
                    }else{
                        ////File not Sent and  not exist locally
                        int drawable = R.drawable.imagedoesnotexist;
                        if (messages.getType().equals("video")){
                            drawable = R.drawable.videodoesnotexist;
                        }

                            Glide.with(mContext)
                                    .asBitmap()
                                    .apply(new RequestOptions().error(R.drawable.sticker_gif_placeholder))
                                    .load(drawable)
                                    .into(chatsViewHolder.senderMessageImage);

                    }


                }

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.senderMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });



                switch (messages.getState()) {
                    case "not sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.black_overlay);
                        break;
                    case "sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorInitial);
                        break;
                    case "delivered":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorAccent);
                        break;
                    case "read":
                        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);
                        break;
                }



            } else {
                if (messages.getType().equals("video")) {
                    chatsViewHolder.receiver_message_VideoThumbnail_play.setVisibility(View.VISIBLE);
                } else {
                    chatsViewHolder.receiver_message_VideoThumbnail_play.setVisibility(View.GONE);
                }
                chatsViewHolder.receiver_message_ImageFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiver_message_ImageFull.setBackgroundResource(R.drawable.receiver_messages_layout);


                if (!TextUtils.isEmpty(messages.getLocal_location().trim())){

                    File loc = new File(messages.getLocal_location());
                    if ((!loc.exists()) || (!loc.isFile()) ) {
                        loc.delete();

                        /// load file from online if it does not exist locally
                        ///and has local location....
                        Glide.with(mContext)
                                .asBitmap()
                                .load(messages.getMessage())
                                .apply(new RequestOptions().error(R.drawable.sticker_gif_placeholder))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        chatsViewHolder.receiverMessageImage.setImageBitmap(resource);
                                        chatsViewHolder.receiverMessageImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, ShowCapturedActivity.class);
                                                intent.putExtra(Config.KeyName.FILEPATH, messages.getMessage());
                                                intent.putExtra("coming_from","ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                mContext.startActivity(intent);
                                            }
                                        });


                                    }
                                });
                    }else {


                        //Load file from local source
                        Glide.with(mContext)
                                .asBitmap()
                                .load(messages.getLocal_location())
                                .apply(new RequestOptions().error(R.drawable.sticker_gif_placeholder))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                        chatsViewHolder.receiverMessageImage.setImageBitmap(resource);

                                        //View image if it is clicked
                                        chatsViewHolder.receiverMessageImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, ShowCapturedActivity.class);
                                                intent.putExtra(Config.KeyName.FILEPATH, messages.getLocal_location());
                                                intent.putExtra("coming_from","ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                mContext.startActivity(intent);
                                            }
                                        });

                                        String newFileName = String.valueOf(System.currentTimeMillis());
                                        if (messages.getType().equals("image")){

                                            saveImage(resource,newFileName,destinationFilename+"Received/");

                                            ChatsRetrieveBackground insertIntoDBBackground = new ChatsRetrieveBackground(mContext);
                                            insertIntoDBBackground.execute("update_message", messages.getMessageID(), messages.getFrom(), currentUserID,
                                                    messages.getCaption(), messages.getDate(), messages.getTime(), messages.getMessage(), messages.getMessage(),
                                                    "read", destinationFilename+"Received/"+newFileName+".png", "yes");
                                        }

                                    }
                                });
                    }

                }else{
                    ////File Sent but not exist locally
                    if (!TextUtils.isEmpty(messages.getMessage()) ||  !messages.getMessage().equals("uploading")) {
                        Glide.with(mContext)
                                .asBitmap()
                                .apply(new RequestOptions().error(R.drawable.sticker_gif_placeholder))
                                .load(messages.getMessage())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                        chatsViewHolder.receiverMessageImage.setImageBitmap(resource);

                                        //View image if it is clicked
                                        chatsViewHolder.receiverMessageImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(mContext, ShowCapturedActivity.class);
                                                intent.putExtra(Config.KeyName.FILEPATH, messages.getMessage());
                                                intent.putExtra("coming_from","ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                mContext.startActivity(intent);
                                            }
                                        });


                                        String newFileName = String.valueOf(System.currentTimeMillis());
                                        if (messages.getType().equals("image")){

                                            saveImage(resource,newFileName,destinationFilename+"Received/");

                                            ChatsRetrieveBackground insertIntoDBBackground = new ChatsRetrieveBackground(mContext);
                                            insertIntoDBBackground.execute("update_message", messages.getMessageID(), messages.getFrom(), currentUserID,
                                                    messages.getCaption(), messages.getDate(), messages.getTime(), messages.getMessage(), messages.getMessage(),
                                                    "read", destinationFilename+"Received/"+newFileName+".png", "yes");
                                        }
                                    }
                                });
                    }else{
                        ////File not Sent and  not exist locally
                        int drawable = R.drawable.imagedoesnotexist;
                        if (messages.getType().equals("video")){
                            drawable = R.drawable.videodoesnotexist;
                        }

                        Glide.with(mContext)
                                .asBitmap()
                                .apply(new RequestOptions().error(R.drawable.sticker_gif_placeholder))
                                .load(drawable)
                                .into(chatsViewHolder.senderMessageImage);

                    }


                }


                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.receiverMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }





        else if (messages.getType().equals("gif")){

            if ( messages.getFrom().equals(currentUserID)){

                chatsViewHolder.senderStickerSoundGifImage.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

//                Picasso.get().load(messages.getMessage()).into(chatsViewHolder.senderStickerSoundGifImage);
                Glide.with(mContext).asGif().load(messages.getMessage()).into(chatsViewHolder.senderStickerSoundGifImage);

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.senderMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });

                switch (messages.getState()) {
                    case "not sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.black_overlay);
                        break;
                    case "sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorInitial);
                        break;
                    case "delivered":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorAccent);
                        break;
                    case "read":
                        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);
                        break;
                }

            }else{

                chatsViewHolder.recieverStickerSoundGifImage.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);


                Glide.with(mContext).
                        asGif()
                        .load(messages.getMessage())
                        .into(chatsViewHolder.recieverStickerSoundGifImage);

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.receiverMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }





        else if (messages.getType().equals("sticker")){

            if ( messages.getFrom().equals(currentUserID)){

                chatsViewHolder.senderStickerSoundGifImage.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);


                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.senderMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });


                    File stickerFile =new File(String.valueOf(mContext
                            .getExternalFilesDir("/Images/Stickers/"+messages.getMessage()+".png")));

                    if (!(stickerFile.exists()) || (!stickerFile.isFile()) ) {
                        stickerFile.delete();
                        Glide.with(mContext)
                                .asBitmap()
                                .load(messages.getCaption())
                                .apply(new RequestOptions().placeholder(R.drawable.sticker_gif_placeholder))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        chatsViewHolder.senderStickerSoundGifImage.setImageBitmap(resource);
                                        saveImage(resource, messages.getMessage(),
                                                String.valueOf(mContext.getExternalFilesDir("/Images/Stickers/")));
                                    }
                                });

                    }else {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(stickerFile)
                                .apply(new RequestOptions().placeholder(R.drawable.sticker_gif_placeholder))
                                .into(chatsViewHolder.senderStickerSoundGifImage);
                    }


                switch (messages.getState()) {
                    case "not sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.black_overlay);
                        break;
                    case "sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorInitial);
                        break;
                    case "delivered":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorAccent);
                        break;
                    case "read":
                        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);
                        break;
                }



            }else{

                chatsViewHolder.recieverStickerSoundGifImage.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);


                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.receiverMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });

                    File stickerFile =new File(String.valueOf(mContext
                            .getExternalFilesDir("/Images/Stickers/"+messages.getMessage()+".png")));

                    if (!(stickerFile.exists()) || (!stickerFile.isFile()) ) {
                        stickerFile.delete();
                        Glide.with(mContext)
                                .asBitmap()
                                .load(messages.getCaption())
                                .apply(new RequestOptions().placeholder(R.drawable.sticker_gif_placeholder))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        chatsViewHolder.recieverStickerSoundGifImage.setImageBitmap(resource);
                                        saveImage(resource, messages.getMessage(),
                                                String.valueOf(mContext.getExternalFilesDir("/Images/Stickers/")));
                                    }
                                });

                    }else {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(stickerFile)
                                .apply(new RequestOptions().placeholder(R.drawable.sticker_gif_placeholder))
                                .into(chatsViewHolder.recieverStickerSoundGifImage);
                    }
            }
        }






        else if (messages.getType().equals("sound")){

            if ( messages.getFrom().equals(currentUserID)){

                chatsViewHolder.senderSoundImage.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);


                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.senderMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });


                File stickerFile =new File(String.valueOf(mContext
                        .getExternalFilesDir("/Sounds/SoundImages/"+messages.getMessage()+".png")));

                if (!(stickerFile.exists()) || (!stickerFile.isFile()) ) {
                    stickerFile.delete();
                    Glide.with(mContext)
                            .asBitmap()
                            .load(messages.getCaption())
                            .apply(new RequestOptions().placeholder(R.drawable.eighth_note))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    chatsViewHolder.senderSoundImage.setImageBitmap(resource);
                                    saveImage(resource, messages.getMessage(),
                                            String.valueOf(mContext.getExternalFilesDir("/Sounds/SoundImages/")));

                                }
                            });


                }else {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(stickerFile)
                            .apply(new RequestOptions().placeholder(R.drawable.eighth_note))
                            .into(chatsViewHolder.senderSoundImage);
                }


                switch (messages.getState()) {
                    case "not sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.black_overlay);
                        break;
                    case "sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorInitial);
                        break;
                    case "delivered":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorAccent);
                        break;
                    case "read":
                        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);
                        break;
                }


            }else{

                chatsViewHolder.recieverSoundImage.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);


                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.receiverMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });

                File soundImageFile =new File(String.valueOf(mContext
                        .getExternalFilesDir("/Sounds/SoundImages/"+messages.getMessage()+".png")));

                if (!(soundImageFile.exists()) || (!soundImageFile.isFile()) ) {
                    soundImageFile.delete();
                    Glide.with(mContext)
                            .asBitmap()
                            .load(messages.getCaption())
                            .apply(new RequestOptions().placeholder(R.drawable.eighth_note))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    chatsViewHolder.recieverSoundImage.setImageBitmap(resource);
                                    saveImage(resource, messages.getMessage(),
                                            String.valueOf(mContext.getExternalFilesDir("/Sounds/SoundImages/")));
                                }
                            });

                }else {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(soundImageFile)
                            .apply(new RequestOptions().placeholder(R.drawable.eighth_note))
                            .into(chatsViewHolder.recieverSoundImage);
                }
            }
        }





        else if (messages.getType().equals("contact")){

            if ( messages.getFrom().equals(currentUserID)){

                chatsViewHolder.sender_contactFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.sender_contactName.setText(messages.getCaption());
                chatsViewHolder.sender_contactNumber.setText(messages.getMessage());
                chatsViewHolder.sender_contactIcon.setImageResource(R.drawable.ic_contacts_white_24dp);

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.senderMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });
                switch (messages.getState()) {
                    case "not sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.black_overlay);
                        break;
                    case "sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorInitial);
                        break;
                    case "delivered":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorAccent);
                        break;
                    case "read":
                        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);
                        break;
                }


            }else{

                chatsViewHolder.receiver_contactFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiver_contactName.setText(messages.getCaption());
                chatsViewHolder.receiver_contactNumber.setText(messages.getMessage());
                chatsViewHolder.receiver_contactIcon.setImageResource(R.drawable.ic_contacts_black_24dp);

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.receiverMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }




        else if (messages.getType().equals("location") || messages.getType().contains("Lng:")){

            if ( messages.getFrom().equals(currentUserID)){

                chatsViewHolder.sender_contactFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.sender_contactName.setText(messages.getType().replace("Lng","Longitude")+"\n"+messages.getCaption().replace("Lat","Latitude")+"\n");
                chatsViewHolder.sender_contactNumber.setText(messages.getMessage());
                chatsViewHolder.sender_contactIcon.setImageResource(R.drawable.ic_location_on_white_24dp);

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.senderMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });

                chatsViewHolder.sender_contactFull.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLocationOnMap(messages.getType().replace("Lng:","").trim(),messages.getCaption().replace("Lat:","").trim());
                    }
                });
                chatsViewHolder.sender_contactFull.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        openLocationOnMapSec(messages.getType().replace("Lng:","").trim(),messages.getCaption().replace("Lat:","").trim());
                        return false;
                    }
                });


                switch (messages.getState()) {
                    case "not sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.black_overlay);
                        break;
                    case "sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorInitial);
                        break;
                    case "delivered":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorAccent);
                        break;
                    case "read":
                        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);
                        break;
                }


            }else{

                chatsViewHolder.receiver_contactFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiver_contactName.setText(messages.getType().replace("Lng","Longitude")+"\n"+messages.getCaption().replace("Lat","Latitude")+"\n");
                chatsViewHolder.receiver_contactNumber.setText(messages.getMessage());
                chatsViewHolder.receiver_contactIcon.setImageResource(R.drawable.ic_location_on_f44c63_24dp);

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.receiverMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });

                chatsViewHolder.receiver_contactFull.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLocationOnMap(messages.getType().replace("Lng:","").trim(),messages.getCaption().replace("Lat:","").trim());
                    }
                });
                chatsViewHolder.receiver_contactFull.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        openLocationOnMapSec(messages.getType().replace("Lng:","").trim(),messages.getCaption().replace("Lat:","").trim());
                        return false;
                    }
                });
            }
        }




        else if (messages.getType().equals("document")){
            if ( messages.getFrom().equals(currentUserID)){

                chatsViewHolder.sender_document_attachmentFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.sender_document_attachmentName.setText(messages.getCaption());

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.senderMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });


                switch (messages.getState()) {
                    case "not sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.black_overlay);
                        break;
                    case "sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorInitial);
                        break;
                    case "delivered":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorAccent);
                        break;
                    case "read":
                        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);
                        break;
                }



            }else{

                chatsViewHolder.receiver_document_attachmentFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiver_document_attachmentName.setText(messages.getCaption());

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.receiverMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }





        else if (messages.getType().equals("audio")){
            if ( messages.getFrom().equals(currentUserID)){

                chatsViewHolder.sender_message_audioFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.sender_message_audioFileName.setText(messages.getCaption());
                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.senderMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });


                switch (messages.getState()) {
                    case "not sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.black_overlay);
                        break;
                    case "sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorInitial);
                        break;
                    case "delivered":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorAccent);
                        break;
                    case "read":
                        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);
                        break;
                }


            }else{

                chatsViewHolder.receiver_message_audioFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiver_message_audioFileName.setText(messages.getCaption());
                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.receiverMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }




        else if (messages.getType().equals("findMe")){
            if ( messages.getFrom().equals(currentUserID)){

                chatsViewHolder.sender_message_findMeFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.senderMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });


                switch (messages.getState()) {
                    case "not sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.black_overlay);
                        break;
                    case "sent":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorInitial);
                        break;
                    case "delivered":
                        chatsViewHolder.senderMessage_state.setBackgroundResource(R.color.colorAccent);
                        break;
                    case "read":
                        chatsViewHolder.senderMessage_state.setVisibility(View.GONE);
                        break;
                }

            }else{

                if (messages.getCaption().equals("doneWith")|| messages.getCaption().equals("rejected")){
                    chatsViewHolder.receiver_message_findMeBtnNo.setVisibility(View.GONE);
                    chatsViewHolder.receiver_message_findMeBtnYes.setVisibility(View.GONE);
                }else{
                    chatsViewHolder.receiver_message_findMeBtnNo.setVisibility(View.VISIBLE);
                    chatsViewHolder.receiver_message_findMeBtnYes.setVisibility(View.VISIBLE);
                }

                chatsViewHolder.receiver_message_findMeFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext,Long.parseLong(messages.getMessageID())));
                chatsViewHolder.receiverMessageDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext,messages.getDate()+"  "+messages.getTime(), Toast.LENGTH_SHORT).show();
                    }
                });

                chatsViewHolder.receiver_message_findMeBtnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectFindMe(messages.getMessageID());
                    }
                });
                chatsViewHolder.receiver_message_findMeBtnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFindMeWithFriend(messages.getFrom());
                    }
                });
            }

        }




    }

    private void openLocationOnMap(String longitude, String latitude) {
//        Intent intent = new Intent(mContext,SingLocationActivity.class);
        Intent intent = new Intent(mContext, MapboxSingleLocationActivity.class);
        intent.putExtra("long", Double.parseDouble(longitude));
        intent.putExtra("lat", Double.parseDouble(latitude));
        intent.putExtra("friendName", "FriendName");
        mContext.startActivity(intent);
    }


    private void openLocationOnMapSec(String longitude, String latitude) {
        Intent intent = new Intent(mContext,SingLocationActivity.class);
//        Intent intent = new Intent(mContext, MapboxSingleLocationActivity.class);
        intent.putExtra("long", Double.parseDouble(longitude));
        intent.putExtra("lat", Double.parseDouble(latitude));
        intent.putExtra("friendName", "FriendName");
        mContext.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return userChatList.size();
    }







    private void openFindMeWithFriend(String fromUid) {
        Intent intent = new Intent(mContext, FindMeMapsActivity.class);
        intent.putExtra("friend_uid",fromUid);
        intent.putExtra("friend_name",fromUid);
        intent.putExtra("connect_auto","yes");
        mContext.startActivity(intent);
    };



    private void rejectFindMe(String messageID) {
        Toast.makeText(mContext, messageID, Toast.LENGTH_SHORT).show();
    }
    private void downloadfiles(String url, String soundName, String localLocation) {
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setDestinationInExternalFilesDir(mContext,localLocation,soundName);

        downloadManager.enqueue(request);

    }
    private void saveImage(Bitmap image,String imageFileName, String directory) {
        File storageDir = new File(directory);

        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName+".png");
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Toast.makeText(this, "saved "+imageFileName,Toast.LENGTH_SHORT).show();
        }
    }


}

