package com.nsromapa.uchat.recyclerchatactivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nsromapa.uchat.ChatActivity;
import com.nsromapa.uchat.ChooseReceiverActivity;
import com.nsromapa.uchat.LocationUtil.MapboxSingleLocationActivity;
import com.nsromapa.uchat.LocationUtil.SingLocationActivity;
import com.nsromapa.uchat.MainActivity;
import com.nsromapa.uchat.R;
import com.nsromapa.uchat.ShowCapturedActivity;
import com.nsromapa.uchat.cameraUtils.Config;
import com.nsromapa.uchat.customizations.CustomIntent;
import com.nsromapa.uchat.findme.FindMeMapsActivity;
import com.nsromapa.uchat.utils.FormatterUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsViewHolder> {
    private static final String TAG = "ChatAdapter";
    private List<ChatsObjects> userChatList;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Context mContext;
    private Activity mActivity;
    private RecyclerView recyclerView;
    private String friendId;
    private String friendName;
    private AlertDialog alertDialog;
    private MediaPlayer mMediaPlayer;

    ChatsAdapter(Context context, Activity mActivity,
                 List<ChatsObjects> userChatList,
                 RecyclerView recyclerView,
                 String friendId,
                 String friendName) {
        this.userChatList = userChatList;
        this.mContext = context;
        this.mActivity = mActivity;
        this.recyclerView = recyclerView;
        this.friendId = friendId;
        this.friendName = friendName;
    }


    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_chat_activity_item, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();
        return new ChatsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ChatsViewHolder chatsViewHolder, final int position) {
        final String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
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


        if (messages.getType().equals("text")) {

            if (messages.getFrom().equals(currentUserID)) {

                chatsViewHolder.senderMessage.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.senderMessage.setBackgroundResource(R.drawable.sender_messages_layout);
                chatsViewHolder.senderMessage.setText(messages.getMessage());
                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());


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

                chatsViewHolder.receiverMessage.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiverMessage.setBackgroundResource(R.drawable.receiver_messages_layout);
                chatsViewHolder.receiverMessage.setText(messages.getMessage());

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());
            }

        } else if (messages.getType().equals("image") || messages.getType().equals("video")) {

            final String destinationFilename;
            final String fileExtection;

            if (messages.getType().equals("video")) {
                destinationFilename = android.os.Environment.getExternalStorageDirectory()
                        .getPath() + "UChat/Video/";
                fileExtection = ".mp4";
            } else {
                destinationFilename = android.os.Environment.getExternalStorageDirectory()
                        .getPath() + "UChat/Image/";
                fileExtection = ".png";
            }


            if (messages.getFrom().equals(currentUserID)) {
                ////If file x video, show play button
                if (messages.getType().equals("video")) {
                    chatsViewHolder.sender_message_VideoThumbnail_play.setVisibility(View.VISIBLE);
                } else {
                    chatsViewHolder.sender_message_VideoThumbnail_play.setVisibility(View.GONE);
                }


                chatsViewHolder.sender_message_ImageFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.senderMessageImage.setBackgroundResource(R.drawable.sender_messages_layout);
//                Picasso.get().load(messages.getMessage()).into(chatsViewHolder.senderMessageImage);
                if (!TextUtils.isEmpty(messages.getLocal_location().trim())) {

                    File loc = new File(messages.getLocal_location());
                    if ((!loc.exists()) || (!loc.isFile())) {
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
                                                intent.putExtra("coming_from", "ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                mContext.startActivity(intent);
                                            }
                                        });

                                    }
                                });
                    } else {
                        //Check if message was sent of not.....
                        if (messages.state.equals("not sent") && !messages.getMessage().equals("uploading")) {

                            if (messages.getMessage().equals("uploading")) {
                                chatsViewHolder.sender_imageVideo_progressBar.setVisibility(View.VISIBLE);
                                chatsViewHolder.sender_message_imageVideoUpload.setVisibility(View.GONE);
                            } else {
                                chatsViewHolder.sender_message_imageVideoUpload.setVisibility(View.VISIBLE);
                                chatsViewHolder.sender_imageVideo_progressBar.setVisibility(View.GONE);
                            }

                            chatsViewHolder.sender_message_imageVideoUpload.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    chatsViewHolder.sender_message_imageVideoUpload.setVisibility(View.GONE);
                                    chatsViewHolder.sender_imageVideo_progressBar.setVisibility(View.VISIBLE);

                                    new ChatUploadAttachment(ChatsAdapter.this, recyclerView, mContext, friendId,
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

                        } else {
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
                                                intent.putExtra("coming_from", "ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                mContext.startActivity(intent);
                                            }
                                        });

                                    }
                                });
                    }

                } else {
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
                                                intent.putExtra("coming_from", "ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                mContext.startActivity(intent);
                                            }
                                        });


                                        String newFileName = String.valueOf(System.currentTimeMillis());
                                        if (messages.getType().equals("image")) {
                                            saveImagetoExternalDirectory(resource, "Sent");

                                            ChatsUpdateBackground insertIntoDBBackground = new ChatsUpdateBackground(mContext);
                                            insertIntoDBBackground.execute("update_message", messages.getMessageID(), messages.getFrom(), currentUserID,
                                                    messages.getCaption(), messages.getDate(), messages.getTime(), messages.getMessage(), messages.getMessage(),
                                                    messages.getState(), destinationFilename + "Sent/" + newFileName + ".jpg", "yes");
                                        }

                                    }
                                });
                    } else {
                        ////File not Sent and  not exist locally
                        int drawable = R.drawable.imagedoesnotexist;
                        if (messages.getType().equals("video")) {
                            drawable = R.drawable.videodoesnotexist;
                        }

                        Glide.with(mContext)
                                .asBitmap()
                                .apply(new RequestOptions().error(R.drawable.sticker_gif_placeholder))
                                .load(drawable)
                                .into(chatsViewHolder.senderMessageImage);

                    }


                }

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());


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


                if (!TextUtils.isEmpty(messages.getLocal_location().trim())) {

                    File loc = new File(messages.getLocal_location());
                    if ((!loc.exists()) || (!loc.isFile())) {
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
                                                intent.putExtra("coming_from", "ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                CustomIntent.customType(mContext, "left-to-right");
                                                mContext.startActivity(intent);
                                            }
                                        });


                                    }
                                });
                    } else {


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
                                                intent.putExtra("coming_from", "ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                CustomIntent.customType(mContext, "left-to-right");
                                                mContext.startActivity(intent);
                                            }
                                        });

                                        String newFileName = String.valueOf(System.currentTimeMillis());
                                        if (messages.getType().equals("image")) {

                                            saveImagetoExternalDirectory(resource, "Received");

                                            ChatsUpdateBackground insertIntoDBBackground = new ChatsUpdateBackground(mContext);
                                            insertIntoDBBackground.execute("update_message", messages.getMessageID(), messages.getFrom(), currentUserID,
                                                    messages.getCaption(), messages.getDate(), messages.getTime(), messages.getMessage(), messages.getMessage(),
                                                    "read", destinationFilename + "Received/" + newFileName + ".png", "yes");
                                        }

                                    }
                                });
                    }

                } else {
                    ////File Sent but not exist locally
                    if (!TextUtils.isEmpty(messages.getMessage()) || !messages.getMessage().equals("uploading")) {
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
                                                intent.putExtra("coming_from", "ChatsAdapter");
                                                intent.putExtra("fileType", messages.getType());
                                                CustomIntent.customType(mContext, "left-to-right");
                                                mContext.startActivity(intent);
                                            }
                                        });


                                        String newFileName = String.valueOf(System.currentTimeMillis());
                                        if (messages.getType().equals("image")) {

                                            saveImagetoExternalDirectory(resource, destinationFilename + "Received");

                                            ChatsUpdateBackground insertIntoDBBackground = new ChatsUpdateBackground(mContext);
                                            insertIntoDBBackground.execute("update_message", messages.getMessageID(), messages.getFrom(), currentUserID,
                                                    messages.getCaption(), messages.getDate(), messages.getTime(), messages.getMessage(), messages.getMessage(),
                                                    "read", destinationFilename + "Received/" + newFileName + ".png", "yes");
                                        }
                                    }
                                });
                    } else {
                        ////File not Sent and  not exist locally
                        int drawable = R.drawable.imagedoesnotexist;
                        if (messages.getType().equals("video")) {
                            drawable = R.drawable.videodoesnotexist;
                        }

                        Glide.with(mContext)
                                .asBitmap()
                                .apply(new RequestOptions().error(R.drawable.sticker_gif_placeholder))
                                .load(drawable)
                                .into(chatsViewHolder.senderMessageImage);

                    }


                }


                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());
            }
        } else if (messages.getType().equals("gif")) {

            if (messages.getFrom().equals(currentUserID)) {

                chatsViewHolder.senderStickerSoundGifImage.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

//                Picasso.get().load(messages.getMessage()).into(chatsViewHolder.senderStickerSoundGifImage);
                Glide.with(mContext).asGif().load(messages.getMessage()).into(new SimpleTarget<GifDrawable>() {
                    @Override
                    public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                        chatsViewHolder.senderStickerSoundGifImage.setImageDrawable(resource);
                    }
                });

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());


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

                chatsViewHolder.recieverStickerSoundGifImage.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);


                Glide.with(mContext).
                        asGif()
                        .load(messages.getMessage())
                        .into(chatsViewHolder.recieverStickerSoundGifImage);

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());
            }

        } else if (messages.getType().equals("sticker")) {

            if (messages.getFrom().equals(currentUserID)) {

                chatsViewHolder.senderStickerSoundGifImage.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);


                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());


                File stickerFile = new File(String.valueOf(mContext
                        .getExternalFilesDir("/Images/Stickers/" + messages.getMessage() + ".png")));

                if (!(stickerFile.exists()) || (!stickerFile.isFile())) {
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

                } else {
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


            } else {

                chatsViewHolder.recieverStickerSoundGifImage.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);


                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());

                File stickerFile = new File(String.valueOf(mContext
                        .getExternalFilesDir("/Images/Stickers/" + messages.getMessage() + ".png")));

                if (!(stickerFile.exists()) || (!stickerFile.isFile())) {
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

                } else {
                    Glide.with(mContext)
                            .asBitmap()
                            .load(stickerFile)
                            .apply(new RequestOptions().placeholder(R.drawable.sticker_gif_placeholder))
                            .into(chatsViewHolder.recieverStickerSoundGifImage);
                }
            }
        } else if (messages.getType().equals("sound")) {

            if (messages.getFrom().equals(currentUserID)) {

                chatsViewHolder.senderSoundImage.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);


                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());


                File stickerFile = new File(String.valueOf(mContext
                        .getExternalFilesDir("/Sounds/SoundImages/" + messages.getMessage() + ".png")));

                if (!(stickerFile.exists()) || (!stickerFile.isFile())) {
                    stickerFile.delete();

                    ///Fetch and display image from the online storage
                    /// if it not exist in local db

                    Glide.with(mContext)
                            .asBitmap()
                            .load(messages.getCaption())
                            .apply(new RequestOptions().placeholder(R.drawable.eighth_note))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    chatsViewHolder.senderSoundImage.setImageBitmap(resource);

                                    //Save image locally for feture use
                                    saveImage(resource, messages.getMessage(),
                                            String.valueOf(mContext.getExternalFilesDir("/Sounds/SoundImages/")));

                                    /// When the sound image is clicked
                                    /// try playing if it exists
                                    //else ask to download
                                    chatsViewHolder.senderSoundImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            soundImageClicked(messages.getMessage(), v);
                                        }
                                    });
                                }
                            });


                } else {
                    ///Fetch and display image from the local storage
                    /// if it exist already

                    Glide.with(mContext)
                            .asBitmap()
                            .load(stickerFile)
                            .apply(new RequestOptions().placeholder(R.drawable.eighth_note))
//                            .into(chatsViewHolder.senderSoundImage)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                    chatsViewHolder.senderSoundImage.setImageBitmap(resource);

                                    /// When the sound image is clicked
                                    /// try playing if it exists
                                    //else ask to download
                                    chatsViewHolder.senderSoundImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            soundImageClicked(messages.getMessage(), v);
                                        }
                                    });
                                }
                            });
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


            } else {

                chatsViewHolder.recieverSoundImage.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);


                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());

                File soundImageFile = new File(String.valueOf(mContext
                        .getExternalFilesDir("/Sounds/SoundImages/" + messages.getMessage() + ".png")));

                if (!(soundImageFile.exists()) || (!soundImageFile.isFile())) {
                    soundImageFile.delete();


                    //Download and save image locally for
                    // future use.........
                    Glide.with(mContext)
                            .asBitmap()
                            .load(messages.getCaption())
                            .apply(new RequestOptions().placeholder(R.drawable.eighth_note))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    chatsViewHolder.recieverSoundImage.setImageBitmap(resource);

                                    //Save image locally for future use
                                    saveImage(resource, messages.getMessage(),
                                            String.valueOf(mContext.getExternalFilesDir("/Sounds/SoundImages/")));


                                    /// When the sound image is clicked
                                    /// try playing if it exists
                                    //else ask to download
                                    chatsViewHolder.recieverSoundImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            soundImageClicked(messages.getMessage(), v);
                                        }
                                    });
                                }
                            });

                } else {

                    //Display image from local ......
                    // storage if it exists.....
                    Glide.with(mContext)
                            .asBitmap()
                            .load(soundImageFile)
                            .apply(new RequestOptions().placeholder(R.drawable.eighth_note))
//                            .into(chatsViewHolder.recieverSoundImage);
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                    chatsViewHolder.recieverSoundImage.setImageBitmap(resource);

                                    /// When the sound image is clicked
                                    /// try playing if it exists
                                    //else ask to download
                                    chatsViewHolder.recieverSoundImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            soundImageClicked(messages.getMessage(), v);
                                        }
                                    });
                                }
                            });
                }
            }


        } else if (messages.getType().equals("contact")) {

            if (messages.getFrom().equals(currentUserID)) {

                //Enable useful views
                chatsViewHolder.sender_contactFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);


                chatsViewHolder.sender_contactName.setText(messages.getCaption());
                chatsViewHolder.sender_contactNumber.setText(messages.getMessage());
                chatsViewHolder.sender_contactIcon.setImageResource(R.drawable.ic_contacts_white_24dp);

                /////When a contact sent is clicked.....
                chatsViewHolder.sender_contactFull.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactChatClicked(messages.getMessage(), messages.getCaption());
                    }
                });
                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));
                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());

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

                chatsViewHolder.receiver_contactFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiver_contactName.setText(messages.getCaption());
                chatsViewHolder.receiver_contactNumber.setText(messages.getMessage());
                chatsViewHolder.receiver_contactIcon.setImageResource(R.drawable.ic_contacts_black_24dp);


                /////When a contact sent is clicked.....
                chatsViewHolder.receiver_contactFull.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactChatClicked(messages.getMessage(), messages.getCaption());
                    }
                });

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));
                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());


            }
        } else if (messages.getType().equals("location") || messages.getType().contains("Lng:")) {

            if (messages.getFrom().equals(currentUserID)) {

                chatsViewHolder.sender_contactFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.sender_contactName.setText(messages.getType().replace("Lng", "Longitude") + "\n" + messages.getCaption().replace("Lat", "Latitude") + "\n");
                chatsViewHolder.sender_contactNumber.setText(messages.getMessage());
                chatsViewHolder.sender_contactIcon.setImageResource(R.drawable.ic_location_on_white_24dp);

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());


                chatsViewHolder.sender_contactFull.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLocationOnMap(messages.getType().replace("Lng:", "").trim(), messages.getCaption().replace("Lat:", "").trim());
                    }
                });
                chatsViewHolder.sender_contactFull.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        openLocationOnMapSec(messages.getType().replace("Lng:", "").trim(), messages.getCaption().replace("Lat:", "").trim());
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


            } else {

                chatsViewHolder.receiver_contactFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiver_contactName.setText(messages.getType().replace("Lng", "Longitude") + "\n" + messages.getCaption().replace("Lat", "Latitude") + "\n");
                chatsViewHolder.receiver_contactNumber.setText(messages.getMessage());
                chatsViewHolder.receiver_contactIcon.setImageResource(R.drawable.ic_location_on_f44c63_24dp);

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());

                chatsViewHolder.receiver_contactFull.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openLocationOnMap(messages.getType().replace("Lng:", "").trim(), messages.getCaption().replace("Lat:", "").trim());
                    }
                });
                chatsViewHolder.receiver_contactFull.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        openLocationOnMapSec(messages.getType().replace("Lng:", "").trim(), messages.getCaption().replace("Lat:", "").trim());
                        return false;
                    }
                });
            }
        } else if (messages.getType().equals("document")) {
            if (messages.getFrom().equals(currentUserID)) {

                chatsViewHolder.sender_document_attachmentFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.sender_document_attachmentName.setText(messages.getCaption());

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());


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

                chatsViewHolder.receiver_document_attachmentFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiver_document_attachmentName.setText(messages.getCaption());

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());
            }
        } else if (messages.getType().equals("audio")) {
            if (messages.getFrom().equals(currentUserID)) {

                chatsViewHolder.sender_message_audioFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.sender_message_audioFileName.setText(messages.getCaption());
                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());


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

                chatsViewHolder.receiver_message_audioFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiver_message_audioFileName.setText(messages.getCaption());
                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());
            }
        } else if (messages.getType().equals("findMe")) {
            if (messages.getFrom().equals(currentUserID)) {

                chatsViewHolder.sender_message_findMeFull.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());


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

                if (messages.getCaption().equals("doneWith") || messages.getCaption().equals("rejected")) {
                    chatsViewHolder.receiver_message_findMeBtnNo.setVisibility(View.GONE);
                    chatsViewHolder.receiver_message_findMeBtnYes.setVisibility(View.GONE);
                } else {
                    chatsViewHolder.receiver_message_findMeBtnNo.setVisibility(View.VISIBLE);
                    chatsViewHolder.receiver_message_findMeBtnYes.setVisibility(View.VISIBLE);
                }

                chatsViewHolder.receiver_message_findMeFull.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));

                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());

                chatsViewHolder.receiver_message_findMeBtnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rejectFindMe(messages.getMessageID());
                    }
                });
                chatsViewHolder.receiver_message_findMeBtnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFindMeWithFriend(messages.getFrom(), messages.getMessageID());
                    }
                });
            }

        } else {

            if (messages.getFrom().equals(currentUserID)) {

                chatsViewHolder.senderMessage.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessageDateTime.setVisibility(View.VISIBLE);
                chatsViewHolder.senderMessage_state.setVisibility(View.VISIBLE);

                chatsViewHolder.senderMessage.setBackgroundResource(R.drawable.sender_messages_layout);
                chatsViewHolder.senderMessage.setText(mContext.getString(R.string.Unknown_type_of_message));

                chatsViewHolder.senderMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));
                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.senderMessageDateTime, messages.getDate() + "  " + messages.getTime());


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

                chatsViewHolder.receiverMessage.setVisibility(View.VISIBLE);
                chatsViewHolder.receiverMessageDateTime.setVisibility(View.VISIBLE);

                chatsViewHolder.receiverMessage.setBackgroundResource(R.drawable.receiver_messages_layout);
                chatsViewHolder.receiverMessage.setText(mContext.getString(R.string.Unknown_type_of_message));

                chatsViewHolder.receiverMessageDateTime.setText(FormatterUtil.getRelativeTimeSpanStringShort(mContext, Long.parseLong(messages.getMessageID())));
                ///Show full time in Toast when
                ///time TextView is long clicked
                showChatRealTime(chatsViewHolder.receiverMessageDateTime, messages.getDate() + "  " + messages.getTime());
            }
        }


    }

    @Override
    public int getItemCount() {
        return userChatList.size();
    }


    private void contactChatClicked(String contact, final String contactName) {
//
//        if (!contact.contains("+")){
//            if (contact.startsWith("00")){
//                contact = "+233"+contact;
//
//            }else if (contact.startsWith("0")){
//                contact = "+233"+contact;
//
//            }else if (contact.startsWith("233")){
//                contact = "+"+contact;
//            }
//
//        }
        final CharSequence[] choices = {"View Contact", "Save Contact", "Call " + contactName, "Cancle"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Select an option...");
        builder.setCancelable(true);
        final String finalContact = contact;
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    viewContact(finalContact);
                } else if (which == 1) {
                    saveContact(finalContact, contactName);
                } else if (which == 2) {
                   callContact(finalContact);
                } else {
                    Log.d(TAG, "onClick: Cancel is selected......");
                }
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveContact(String finalContact, String contactName) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, 001);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, finalContact);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
        values.put(ContactsContract.CommonDataKinds.Phone.LABEL, contactName);
        Uri dataUri = mContext.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,values);
    }

    private void callContact(String finalContact) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + finalContact));
        CustomIntent.customType(mContext, "left-to-right");
        mContext.startActivity(intent);
    }

    private void viewContact(String finalContact) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + finalContact));
        CustomIntent.customType(mContext, "left-to-right");
        mContext.startActivity(intent);
    }

    private void showChatRealTime(TextView messageDateTimeView, final String messageDateTime) {
        messageDateTimeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showToast(messageDateTime);
                return false;
            }
        });
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
        Intent intent = new Intent(mContext, SingLocationActivity.class);
//        Intent intent = new Intent(mContext, MapboxSingleLocationActivity.class);
        intent.putExtra("long", Double.parseDouble(longitude));
        intent.putExtra("lat", Double.parseDouble(latitude));
        intent.putExtra("friendName", "FriendName");
        mContext.startActivity(intent);
    }

    private void openFindMeWithFriend(String fromUid, String messageID) {
        Intent intent = new Intent(mContext, FindMeMapsActivity.class);
        intent.putExtra("friend_uid", fromUid);
        intent.putExtra("friend_name", friendName);
        intent.putExtra("connect_auto", "yes");
        mContext.startActivity(intent);
    }

    private void rejectFindMe(String messageID) {
        showToast(messageID);
    }

    private void downloadFiles(String url, String soundName, String localLocation) {
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setDestinationInExternalFilesDir(mContext, localLocation, soundName);

        downloadManager.enqueue(request);

    }

    private void saveImage(Bitmap image, String imageFileName, String directory) {
        File storageDir = new File(directory);

        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName + ".png");
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

    private void saveImagetoExternalDirectory(final Bitmap bitmap, final String foldername) {

        Dexter.withActivity(mActivity)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            final FileOutputStream fileOutputStream;
                            File file = getExternalDirectory_andFolder("Uchat/Image/" + foldername);

                            if (!file.exists() && !file.mkdirs()) {
                                showToast("Can't create Directory to save image");
                                return;
                            }

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
                            String date = simpleDateFormat.format(new Date());
                            String imgName = "uchat_" + date + ".jpg";
                            String file_name = file.getAbsolutePath() + "/" + imgName;
                            File new_file = new File(file_name);

                            try {

                                fileOutputStream = new FileOutputStream(new_file);

                                Glide.with(mContext)
                                        .asBitmap()
                                        .load(bitmap)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                                resource.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                            }
                                        });

                                showToast("Image Saved Successfully");

                                fileOutputStream.flush();
                                fileOutputStream.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            refreshingGallery(new_file);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        showToast("Permission denied!");
                    }
                }).check();


    }

    private void refreshingGallery(File new_file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new_file));
        mContext.sendBroadcast(intent);
    }

    private File getExternalDirectory_andFolder(String folder) {
        File file = Environment.getExternalStorageDirectory();
        return new File(file, folder);
    }

    private void soundImageClicked(String messageSoundName, View imageView) {
        final String soundName = messageSoundName.replace(".png", "") + ".mp3";
        String audioPath = String.valueOf(mContext.getExternalFilesDir("/sounds/SoundAudios/" + soundName));

        final File file = new File(audioPath);

        if (!(file.exists()) || (!file.isFile())) {
            file.delete();

            alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setTitle(" ");
            alertDialog.setMessage("Sorry, sound does not exist on your device...");
            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Download", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadSound(soundName);
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.hide();
                }
            });
            alertDialog.show();


        } else if (file.exists() && file.isFile()) {
            playAndSendAudio(file, imageView);
        } else {
            showToast("Error....");
        }
    }

    private void downloadSound(final String soundName) {
        if (mAuth.getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("sounds").child(soundName)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("audio")) {
                                String audioUrl = dataSnapshot.child("audio").getValue().toString();
                                downloadAndSaveSound(audioUrl, soundName + ".mp3", "/Sounds/SoundAudios/");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showToast("Couldn't download...");
                        }
                    });
        } else {
            new MainActivity().logOut();
        }

    }

    private void playAndSendAudio(final File file, final View imageView) {
        imageView.setEnabled(false);
        mMediaPlayer = MediaPlayer.create(mContext, Uri.fromFile(file));
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                imageView.setEnabled(true);
            }
        });

        mMediaPlayer.start();

    }

    private void downloadAndSaveSound(String url, String soundName, String localLocation) {
        showToast("You are no downloading " + soundName + ", please try again after  download...");
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(mContext, localLocation, soundName);

        downloadManager.enqueue(request);
    }

    private void showToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }


}

