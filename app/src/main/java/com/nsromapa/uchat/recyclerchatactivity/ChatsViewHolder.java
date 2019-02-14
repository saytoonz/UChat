package com.nsromapa.uchat.recyclerchatactivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nsromapa.emoticompack.samsung.SamsungEmoticonProvider;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonTextView;
import com.nsromapa.uchat.R;

public class ChatsViewHolder extends RecyclerView.ViewHolder {

    EmoticonTextView senderMessage, receiverMessage;
    TextView senderMessageDateTime, receiverMessageDateTime;

    LinearLayout receiver_message_ImageFull,sender_message_ImageFull;
    ImageView senderMessageImage, receiverMessageImage;
    ImageView receiver_message_VideoThumbnail_play,sender_message_VideoThumbnail_play;
    ImageView sender_message_imageVideoUpload;
    ProgressBar sender_imageVideo_progressBar;

    ImageView senderStickerSoundGifImage, recieverStickerSoundGifImage;
    ImageView senderSoundImage, recieverSoundImage;

    LinearLayout receiver_contactFull, sender_contactFull;
    TextView receiver_contactName, sender_contactName;
    TextView receiver_contactNumber, sender_contactNumber;
    ImageView receiver_contactIcon, sender_contactIcon;

    LinearLayout receiver_document_attachmentFull, sender_document_attachmentFull;
    EmoticonTextView receiver_document_attachmentName, sender_document_attachmentName;

    LinearLayout receiver_message_audioFull,sender_message_audioFull;
    TextView receiver_message_audioFileName,sender_message_audioFileName;

    LinearLayout receiver_message_findMeFull, sender_message_findMeFull;
    TextView receiver_message_findMeTextNotification, sender_message_findMeTextNotification;
    Button receiver_message_findMeBtnNo,receiver_message_findMeBtnYes;

    View senderMessage_state;



    public ChatsViewHolder(@NonNull View itemView) {
        super(itemView);

        senderMessage = itemView.findViewById(R.id.sender_message_text);
        receiverMessage = itemView.findViewById(R.id.receiver_message_text);

        sender_message_ImageFull= itemView.findViewById(R.id.sender_message_ImageFull);
        receiver_message_ImageFull= itemView.findViewById(R.id.receiver_message_ImageFull);
        sender_message_VideoThumbnail_play = itemView.findViewById(R.id.sender_message_VideoThumbnail_play);
        receiver_message_VideoThumbnail_play = itemView.findViewById(R.id.receiver_message_VideoThumbnail_play);
        senderMessageImage = itemView.findViewById(R.id.sender_message_Image);
        receiverMessageImage = itemView.findViewById(R.id.receiver_message_Image);
        sender_message_imageVideoUpload = itemView.findViewById(R.id.sender_message_imageVideoUpload);
        sender_imageVideo_progressBar = itemView.findViewById(R.id.sender_imageVideo_progressBar);

        senderMessageDateTime = itemView.findViewById(R.id.senderMessage_dateTime);
        receiverMessageDateTime = itemView.findViewById(R.id.receiverMessage_dateTime);
        senderStickerSoundGifImage = itemView.findViewById(R.id.sender_sticker_gif_Image);
        recieverStickerSoundGifImage = itemView.findViewById(R.id.receiver_sticker_gif_Image);
        senderSoundImage = itemView.findViewById(R.id.sender_sound_Image);
        recieverSoundImage = itemView.findViewById(R.id.receiver_sound_Image);

        receiver_contactFull = itemView.findViewById(R.id.receiver_contactFull);
        sender_contactFull = itemView.findViewById(R.id.sender_contactFull);
        receiver_contactName = itemView.findViewById(R.id.receiver_contactName);
        sender_contactName = itemView.findViewById(R.id.sender_contactName);
        receiver_contactNumber = itemView.findViewById(R.id.receiver_contactNumber);
        sender_contactNumber = itemView.findViewById(R.id.sender_contactNumber);
        sender_contactIcon = itemView.findViewById(R.id.sender_contactIcon);
        receiver_contactIcon = itemView.findViewById(R.id.receiver_contactIcon);

        receiver_document_attachmentName = itemView.findViewById(R.id.receiver_document_attachmentName);
        sender_document_attachmentName = itemView.findViewById(R.id.sender_document_attachmentName);
        receiver_document_attachmentFull = itemView.findViewById(R.id.receiver_document_attachmentFull);
        sender_document_attachmentFull = itemView.findViewById(R.id.sender_document_attachmentFull);


        receiver_message_audioFull = itemView.findViewById(R.id.receiver_message_audioFull);
        sender_message_audioFull = itemView.findViewById(R.id.sender_message_audioFull);
        receiver_message_audioFileName = itemView.findViewById(R.id.receiver_message_audioFileName);
        sender_message_audioFileName = itemView.findViewById(R.id.sender_message_audioFileName);

        receiver_message_findMeFull = itemView.findViewById(R.id.receiver_message_findMeFull);
        sender_message_findMeFull = itemView.findViewById(R.id.sender_message_findMeFull);
        receiver_message_findMeTextNotification = itemView.findViewById(R.id.receiver_message_findMeTextNotification);
        sender_message_findMeTextNotification = itemView.findViewById(R.id.sender_message_findMeTextNotification);
        receiver_message_findMeBtnNo = itemView.findViewById(R.id.receiver_message_findMeBtnNo);
        receiver_message_findMeBtnYes = itemView.findViewById(R.id.receiver_message_findMeBtnYes);



        senderMessage_state = itemView.findViewById(R.id.senderMessage_state);


        senderMessage.setEmoticonProvider(SamsungEmoticonProvider.create());
        receiverMessage.setEmoticonProvider(SamsungEmoticonProvider.create());

    }

}
