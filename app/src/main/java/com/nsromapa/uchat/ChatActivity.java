package com.nsromapa.uchat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.hardware.input.InputManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nsromapa.emoticompack.samsung.SamsungEmoticonProvider;
import com.nsromapa.gifpack.giphy.GiphyGifProvider;
import com.nsromapa.say.emogifstickerkeyboard.EmoticonGIFKeyboardFragment;
import com.nsromapa.say.emogifstickerkeyboard.emoticons.Emoticon;
import com.nsromapa.say.emogifstickerkeyboard.emoticons.EmoticonSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.gifs.Gif;
import com.nsromapa.say.emogifstickerkeyboard.gifs.GifSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.internal.sound.SoundSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.internal.sticker.StickerSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonEditText;
import com.nsromapa.uchat.LocationUtil.PermissionUtils;
import com.nsromapa.uchat.LocationUtil.SingLocationActivity;
import com.nsromapa.uchat.customizations.CustomIntent;
import com.nsromapa.uchat.findme.FindMeMapsActivity;
import com.nsromapa.uchat.recyclerchatactivity.ChatsAdapter;
import com.nsromapa.uchat.recyclerchatactivity.ChatsObjects;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback {

    private String receiver_user_image_url, receiver_user_name, receiver_user_id, sender_user_id;


    private TextView userName, lastSeen;
    private CircleImageView userProfileImage;
    private ImageView backArrow,sendMessageBtn,toggleKeyboardEmoji;
    private EmoticonEditText messageEditText;
    private ImageView captureImage,recAudio;
    private LinearLayout img_capt_aud_rec,attachment_layouout;
    private ImageView attach_photo, attach_video, attach_gallery, attach_record;
    private ImageView attach_audio, attach_document, attach_findUser, attach_location, attach_contact;
    private RecyclerView userMessagesRecycler;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef, theseUsersMessageTableRef;
    private String messageSenderRef;
    private String messageReceivererRef;

    private AlertDialog alertDialog;
    private MediaPlayer mMediaPlayer;

    private List<ChatsObjects> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private ChatsAdapter chatsAdapter;

    private String currentDate,currentTime;

    private ProgressDialog progressDialog;
    private InputMethodManager inputMethodManager;
    private EmoticonGIFKeyboardFragment emoticonGIFKeyboardFragment;

    private static final int REQUEST_RECORD_AUDIO = 0;
    private static String AUDIO_FILE_PATH;

    private final static int SELECTED_FROM_GALLERY_CODE = 1000;
    private final static int SELECTED_AUDIO_CODE = 1100;
    private final static int SELECTED_DOCUMENT_CODE = 1110;
    private final static int SELECTED_CONTACT_CODE = 1111;
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;

    // LogCat tag
    private static final String TAG = ChatActivity.class.getSimpleName();
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private double latitude;
    private double longitude;
    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;

    boolean isPermissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeFBaseAndGetIntent();
        initializeControllers();
        initializeEmojiGifStickerKeyBoard();
        DisplayLastSeen();


        //Set up Toolbar views
        userName.setText(receiver_user_name);
        lastSeen.setText("");
        Glide.with(this)
                .asBitmap()
                .load(receiver_user_image_url)
                .apply(new RequestOptions().placeholder(R.drawable.profile_image))
                .into(userProfileImage);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Send message button is clicked
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(messageEditText.getText().toString()))
                {
                    SendMessage(messageEditText.getText().toString(),"text","");
                    messageEditText.setText("");

                }else displayAttchments();
            }
        });

        //Record audio button is clicked
        recAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requstPermissionToStartRecording();
            }
        });
        //Record audio button in attachment is clicked
        attach_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requstPermissionToStartRecording();
            }
        });
        //Select audio button in attachment is clicked
        attach_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requstPermissionToSelectAudio();
            }
        });

        //Select Image or Video from SDCard button in attachment is clicked
        attach_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requstPermissionToSelectFromGallery();
            }
        });

        //Select Document from SDCard button in attachment is clicked
        attach_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requstPermissionToSelectDocument();
            }
        });

        //Select Contact from SDCard button in attachment is clicked
        attach_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requstPermissionToSelectContact();
            }
        });

        //Get my location and send
        attach_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMyLocation();
            }
        });

        //Open find me
        attach_findUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFindMe();
            }
        });



        permissionUtils=new PermissionUtils(ChatActivity.this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);



        // check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateStatus("online");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateStatus("online");
        }
        //Load all messages when intent is opened
        mRootRef.child("messages").child(sender_user_id).child(receiver_user_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        ChatsObjects messages = dataSnapshot.getValue(ChatsObjects.class);
                        messageList.add(messages);
                        chatsAdapter.notifyDataSetChanged();

                        userMessagesRecycler.smoothScrollToPosition(userMessagesRecycler.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        ChatsObjects messages = dataSnapshot.getValue(ChatsObjects.class);
                        messageList.remove(messages);
                        chatsAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ChatsObjects messages = dataSnapshot.getValue(ChatsObjects.class);
                        messageList.remove(messages);
                        chatsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void initializeFBaseAndGetIntent(){
        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        sender_user_id = mAuth.getCurrentUser().getUid();

        assert  getIntent().getExtras() != null;
        receiver_user_id = getIntent().getExtras().get("receiver_user_id").toString();
        receiver_user_name = getIntent().getExtras().get("receiver_user_name").toString();
        receiver_user_image_url = getIntent().getExtras().get("receiver_user_image_url").toString();

    }
    private void initializeControllers() {

        Toolbar mChatToolbar = findViewById(R.id.activity_chat_toolbar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        //Set customized toolbar as the main toolbar
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.toolbar_chat_activity_toolbar,null);
        actionBar.setCustomView(actionBarView);


        //Initialize views on the chat toolbar (toolbar_chat_activity_toolbar)
        userName = findViewById(R.id.toolbar_chat_activity_user_profile_name);
        lastSeen = findViewById(R.id.toolbar_chat_activity_user_state);
        userProfileImage = findViewById(R.id.toolbar_chat_activity_user_profile_image);
        backArrow = findViewById(R.id.toolbar_chat_activity_back_arrow);

        //Initialize views in the activity chat layout
        toggleKeyboardEmoji = findViewById(R.id.activity_chat_insert_Emoji);
        sendMessageBtn = findViewById(R.id.activity_chat_send);
        messageEditText = findViewById(R.id.activity_chat_message_text);
        captureImage = findViewById(R.id.activity_chat_capture_image);
        recAudio = findViewById(R.id.activity_chat_record_audio);
        img_capt_aud_rec = findViewById(R.id.image_capture_audio_rec_linearLayout);

        attachment_layouout = findViewById(R.id.chat_activity_layout_attachment);
        attach_photo = findViewById(R.id.chat_activity_open_Photo);
        attach_video = findViewById(R.id.chat_activity_open_Video);
        attach_gallery = findViewById(R.id.chat_activity_open_Gallery);
        attach_record = findViewById(R.id.chat_activity_open_Recorder);
        attach_audio = findViewById(R.id.chat_activity_open_Audio);
        attach_document = findViewById(R.id.chat_activity_open_Document);
        attach_findUser = findViewById(R.id.chat_activity_open_FindUser);
        attach_location = findViewById(R.id.chat_activity_open_Location);
        attach_contact = findViewById(R.id.chat_activity_open_Contact);




        //Setup chats into the chat recyclerView
        userMessagesRecycler = findViewById(R.id.activity_chat_recyclerview);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesRecycler.setLayoutManager(linearLayoutManager);
        chatsAdapter = new ChatsAdapter(this,messageList);
        userMessagesRecycler.setAdapter(chatsAdapter);


//        Initialize keyboard
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        //Toggle send/attachment button with respect to text in message editText
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0){
                    img_capt_aud_rec.setVisibility(View.VISIBLE);
                    sendMessageBtn.setImageResource(R.drawable.ic_add_circle_outline_737373_24dp);
                }else {
                    img_capt_aud_rec.setVisibility(View.GONE);
                    sendMessageBtn.setImageResource(R.drawable.ic_play_arrow_737373_24dp);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        Hide attachment layout when keyboard is opened
        messageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams params = attachment_layouout.getLayoutParams();
                if (attachment_layouout.getHeight() > 0) {
                    params.height = 0;
                    attachment_layouout.setLayoutParams(params);
                    attachment_layouout.setVisibility(View.INVISIBLE);

                    CustomIntent.customType(ChatActivity.this, "bottom-to-up");
                }
            }
        });



        //Firebase Database References for messages
        messageSenderRef = "messages/"+ sender_user_id +"/"+ receiver_user_id;
        messageReceivererRef = "messages/"+ receiver_user_id +"/"+ sender_user_id;
        theseUsersMessageTableRef = mRootRef.child("Messages")
                .child(sender_user_id).child(receiver_user_id);


    }
    private void initializeEmojiGifStickerKeyBoard() {

        EmoticonGIFKeyboardFragment.EmoticonConfig emoticonConfig = new EmoticonGIFKeyboardFragment.EmoticonConfig()
                .setEmoticonProvider(SamsungEmoticonProvider.create())
                .setEmoticonSelectListener(new EmoticonSelectListener() {
                    @Override
                    public void emoticonSelected(Emoticon emoticon) {
                        messageEditText.append(emoticon.getUnicode(),
                                messageEditText.getSelectionStart(),
                                messageEditText.getSelectionEnd());
                    }

                    @Override
                    public void onBackSpace() {

                    }
                });
        EmoticonGIFKeyboardFragment.GIFConfig gifConfig = new EmoticonGIFKeyboardFragment
                .GIFConfig(GiphyGifProvider.create(this,"564ce7370bf347f2b7c0e4746593c179"))
                .setGifSelectListener(new GifSelectListener() {
                    @Override
                    public void onGifSelected(@NonNull Gif gif) {
                        sendKeyboardGIF(gif.getGifUrl());

                    }
                });

        EmoticonGIFKeyboardFragment.STICKERConfig stickerConfig = new EmoticonGIFKeyboardFragment.STICKERConfig()
                .setStickerSelectedListener(new StickerSelectListener() {
                    @Override
                    public void onStickerSelectListner(@NonNull File sticker) {
                        sendKeyboardSticker(sticker.getName().replace(".png",""));
                    }
                });

        EmoticonGIFKeyboardFragment.SoundConfig  soundConfig = new EmoticonGIFKeyboardFragment.SoundConfig()
                .setSoundImageSelectedListener(new SoundSelectListener() {
                    @Override
                    public void onSoundSelectListner(@NonNull File soundImage) {

                        String soundName = soundImage.getName().replace(".png",".mp3");
                        String audioPath = String.valueOf(getExternalFilesDir("/sounds/SoundAudios/"+soundName));

                        File file =new File(audioPath);

                        if (!(file.exists()) || (!file.isFile()) ) {
                            file.delete();
                            downloadSound(soundName);
                        }else if (file.exists() && file.isFile()){
                            playAndSendAudio(file);
                        }else {
                            Toast.makeText(ChatActivity.this, "Error....", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        emoticonGIFKeyboardFragment = EmoticonGIFKeyboardFragment
                .getNewInstance(findViewById(R.id.keyboard_container), emoticonConfig, gifConfig,stickerConfig,soundConfig);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.keyboard_container, emoticonGIFKeyboardFragment)
                .commit();



        //Set smiley button to open/close the emoticon gif keyboard
        toggleKeyboardEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emoticonGIFKeyboardFragment.isOpen()){

                    emoticonGIFKeyboardFragment.toggle();
                    toggleKeyboardEmoji.setImageResource(R.drawable.ic_smiley);

                    if (inputMethodManager != null){
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        messageEditText.requestFocus();
                    }
                }else {
                    //Check if keyboard is open and close it if it is
                    if (inputMethodManager.isAcceptingText()){
                        inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(),0);
                    }

                    toggleKeyboardEmoji.setImageResource(R.drawable.ic_keyboard_black_24dp);
                    emoticonGIFKeyboardFragment.toggle();
                }
                ViewGroup.LayoutParams params = attachment_layouout.getLayoutParams();
                if (attachment_layouout.getHeight()>0){
                    params.height =0;
                    attachment_layouout.setLayoutParams(params);
                    attachment_layouout.setVisibility(View.INVISIBLE);
                }

            }
        });



        messageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emoticonGIFKeyboardFragment.isOpen()) {
                    //Check if keyboard is open and close it if it is
                    if (inputMethodManager.isAcceptingText()){
                        inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(),0);
                    }
                }
            }
        });

        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    if (emoticonGIFKeyboardFragment.isOpen())
                        //Check if keyboard is open and close it if it is
                        if (inputMethodManager.isAcceptingText()){
                            inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(),0);
                        }
            }
        });



    }



    public void SendMessage(String messageText,String type, String caption ) {
        if (!TextUtils.isEmpty(messageText.trim())){

            Calendar calendarFordate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calendarFordate.getTime());

            Calendar calendarForTime= Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calendarForTime.getTime());

            ////Get unique key for message
            String messagePushKey = theseUsersMessageTableRef.push().getKey();

            Map<String,Object> messageTextBody = new HashMap<>();
            messageTextBody.put("messageID",messagePushKey);
            messageTextBody.put("message",messageText);
            messageTextBody.put("caption",caption);
            messageTextBody.put("type",type);
            messageTextBody.put("from",sender_user_id);
            messageTextBody.put("date",currentDate);
            messageTextBody.put("time",currentTime);



            Map<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushKey, messageTextBody);
            messageBodyDetails.put(messageReceivererRef + "/" + messagePushKey, messageTextBody);

            mRootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message could not send....", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void uploadFilesInChat(Uri fileUri, final String fileType, final String caption) {
        final String messagePushKey = theseUsersMessageTableRef.push().getKey();
        String folder=fileType;

        if (fileType.equals("image")){
            folder = "captures";
        }

        final StorageReference serverFilePath = FirebaseStorage.getInstance().getReference().child(folder).child(messagePushKey);

        if (fileUri != null && !TextUtils.isEmpty(fileType)){

            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading file....");
            progressDialog.setProgress(0);
            progressDialog.show();

            UploadTask uploadTask = serverFilePath.putFile(fileUri);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    serverFilePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String fileUrl = Objects.requireNonNull(task.getResult()).toString();

                            progressDialog.dismiss();

                            Calendar calendarFordate = Calendar.getInstance();
                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                            currentDate = currentDateFormat.format(calendarFordate.getTime());

                            Calendar calendarForTime= Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
                            currentTime = currentTimeFormat.format(calendarForTime.getTime());


                            Map<String,Object> messageTextBody = new HashMap<>();
                            messageTextBody.put("message",fileUrl);
                            messageTextBody.put("caption",caption);
                            messageTextBody.put("type",fileType);
                            messageTextBody.put("from",sender_user_id);
                            messageTextBody.put("date",currentDate);
                            messageTextBody.put("time",currentTime);


                            Map<String, Object> messageBodyDetails = new HashMap<>();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushKey, messageTextBody);
                            messageBodyDetails.put(messageReceivererRef + "/" + messagePushKey, messageTextBody);

                            mRootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(ChatActivity.this, "There was an error....", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChatActivity.this, "There was an error....", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            int currentProgress = (int)(100*taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setProgress(currentProgress);
                        }
                    });


        }else{
            Toast.makeText(this, "Unknown error...", Toast.LENGTH_SHORT).show();
        }

    }

    private void DisplayLastSeen(){
        mRootRef.child("users").child(receiver_user_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String userState = "";
                        if (dataSnapshot.child("userState").hasChild("state")){
                            String state=dataSnapshot.child("userState").child("state").getValue().toString();
                            String date=dataSnapshot.child("userState").child("date").getValue().toString();
                            String time=dataSnapshot.child("userState").child("time").getValue().toString();
                          
                            if (!state.equals("offline"))
                                userState = state;
                            else
                                userState = "Last seen"+date+"-"+time;
                            
                        }else{

                        }
                        lastSeen.setText(userState);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        lastSeen.setText("");
                    }
                });

    }


    private void playAndSendAudio(final File file) {
        final ImageView playImg = new ImageView(ChatActivity.this);
        playImg.setImageResource(R.drawable.eighth_note);

        alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
        alertDialog.setTitle(" ");
        alertDialog.setView(playImg);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.stop();
                mMediaPlayer.release();
                sendKeyboardSound(file.getName().replace(".mp3",""));
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        });

        playImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
            }
        });

        alertDialog.show();

        mMediaPlayer = MediaPlayer.create(ChatActivity.this, Uri.fromFile(file));
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.start();

    }


    private void sendKeyboardGIF(String url) {
        SendMessage(url,"gif","");
    }
    private void sendKeyboardSticker(final String name) {
        FirebaseDatabase.getInstance().getReference()
                .child("stickers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(name)){
                            String stickerLink = dataSnapshot.child(name).child("loc").getValue().toString();
                            SendMessage(name,"sticker",stickerLink);
                        }else
                            Toast.makeText(ChatActivity.this, "Sticker has been depreciated...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void sendKeyboardSound(String name) {
        SendMessage(name,"sound","");
    }
    private void downloadSound(final String soundName) {
        mRootRef.child("sounds").child(soundName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("audio")){
                            String audioUrl = dataSnapshot.child("audio").getValue().toString();
                            downloadAndSaveSound(audioUrl, soundName + ".mp3", "/Sounds/SoundAudios/");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showToast("Couldn't download...");
                    }
                });
    }

    private void downloadAndSaveSound(String url, String soundName, String localLocation) {
        showToast("You are no downloading "+soundName+", please try again after  download...");
        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(this,localLocation,soundName);

        downloadManager.enqueue(request);
    }

    private void requstPermissionToSelectContact() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            selectContactFile();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(ChatActivity.this, "Permission needed to continue this operation...!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }
    private void requstPermissionToSelectDocument() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            selectDocumentFile();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(ChatActivity.this, "Permission needed to continue this operation...!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }
    private void requstPermissionToSelectAudio() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            selectAudioFile();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(ChatActivity.this, "Permission needed to continue this operation...!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }
    private void requstPermissionToSelectFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            selectImageOrVideoFile();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(ChatActivity.this, "Permission needed to continue this operation...!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }
    private void requstPermissionToStartRecording() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            startAudioRecording();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(ChatActivity.this, "Permission needed to continue this operation...!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void startAudioRecording(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMMddhhmmss");
        String date = simpleDateFormat.format(new Date());

        String fileName = "AUD" +date+".wav";
        AUDIO_FILE_PATH = getExternalDirectory_andFolder("UChat/Audio/Sent",fileName);

        AndroidAudioRecorder.with(this)
                //Require
                .setFilePath(AUDIO_FILE_PATH)
                .setColor(ContextCompat.getColor(this,R.color.colorAccent))
                .setRequestCode(REQUEST_RECORD_AUDIO)

                //Opntional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_48000)
                .setAutoStart(true)
                .setKeepDisplayOn(true)


                //Start Recording Activity
                .record();

    }
    private void selectImageOrVideoFile(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*,video/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*,video/*"});
        startActivityForResult(intent, SELECTED_FROM_GALLERY_CODE);
    }
    private void selectAudioFile(){
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"audio/*"});
        startActivityForResult(intent,SELECTED_AUDIO_CODE);
    }
    private void selectDocumentFile(){
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/*"});
        startActivityForResult(intent,SELECTED_DOCUMENT_CODE);
    }
    private void selectContactFile(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent,SELECTED_CONTACT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data.getData() !=null;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_RECORD_AUDIO) {

//                Toast.makeText(this, AUDIO_FILE_PATH, Toast.LENGTH_SHORT).show();
                uploadFilesInChat(Uri.fromFile(new File(AUDIO_FILE_PATH)),"audio","");

            }else if (requestCode == SELECTED_FROM_GALLERY_CODE){
                copyFileToSentFolderAndUpload(data.getData());

            }else if (requestCode == SELECTED_AUDIO_CODE){
                copyFileToSentFolderAndUpload(data.getData());
                
            }else if (requestCode == SELECTED_DOCUMENT_CODE){
                copyFileToSentFolderAndUpload(data.getData());
                
            }else if (requestCode == SELECTED_CONTACT_CODE){

                Cursor cursor;
                try {
                    String phoneNo;
                    String name;

                    Uri uri = data.getData();
                    cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    phoneNo = cursor.getString(phoneIndex);
                    name = cursor.getString(nameIndex);

                    SendMessage(phoneNo,"contact",name);
                    

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Sorry there was an error...", Toast.LENGTH_SHORT).show();
                }

            }else if (requestCode ==REQUEST_CHECK_SETTINGS){
                getLocation();
            }
        }
    }

    private void displayAttchments() {

        //Check and close emojiGifStickerKeyboard if opend
        if (emoticonGIFKeyboardFragment.isOpen()){
            emoticonGIFKeyboardFragment.toggle();
            toggleKeyboardEmoji.setImageResource(R.drawable.ic_smiley);
        }

        //Get attachment layout paramenters
        ViewGroup.LayoutParams params = attachment_layouout.getLayoutParams();
        if (attachment_layouout.getHeight()>0){
            //Set layout height to 0dp
            params.height =0;
            attachment_layouout.setVisibility(View.INVISIBLE);

            // Show Keyboard when attachment is closed
            if (inputMethodManager != null){
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                messageEditText.requestFocus();
            }
        }else{
            //Check if keyboard is open and close it if it is
            if (inputMethodManager.isAcceptingText()){
                inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(),0);
            }

            //Set layout height to wrap content
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            attachment_layouout.setVisibility(View.VISIBLE);

        }
        //Apply set the parameters setted above to the layout
        attachment_layouout.setLayoutParams(params);
    }
    private void sendMyLocation(){
        getLocation();

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            getAddress();

        } else {
            showToast("Couldn't get the location. Make sure location is enabled on the device");
        }

    }
    private void openFindMe() {
        Intent intent = new Intent(ChatActivity.this, FindMeMapsActivity.class);
        intent.putExtra("friend_uid",receiver_user_id);
        intent.putExtra("friend_name",receiver_user_name);
        intent.putExtra("connect_auto","no");
        startActivity(intent);
    }
    
    private String getExternalDirectory_andFolder(String directory,String fileName){

        //Get SD Card and directory
        File SDCard = Environment.getExternalStorageDirectory();
        File folder = new File(SDCard, directory);

        String filePath = "";

//        Create Directory if does not exist
        if (!folder.exists() && !folder.mkdirs()){
            Toast.makeText(this, "Can't create Directory to save image", Toast.LENGTH_SHORT).show();
        }else{
            filePath = folder.getAbsolutePath()+"/"+fileName;
        }

        return filePath;

    }
    private void copyFileToSentFolderAndUpload(Uri data) {

        String fileName = null;
        String fileMediaType = null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMMddhhmmss");
        String date = simpleDateFormat.format(new Date());


            String  itsLocalName = data.getLastPathSegment();
            String fileType = getContentResolver().getType(data);
            assert fileType != null;
        String ext = fileType.substring(fileType.lastIndexOf("/")+1);
        itsLocalName = itsLocalName.substring(itsLocalName.lastIndexOf("/")+1);

            if (fileType.contains("image")){
                fileMediaType = "image";
                fileName = getExternalDirectory_andFolder("UChat/Image/Sent","IMG" +date+"."+ext);
            }else if (fileType.contains("video")){
                fileName = getExternalDirectory_andFolder("UChat/Video/Sent","VID" +date+"."+ext);
                fileMediaType = "video";

            }else if (fileType.contains("audio")){
                fileName = getExternalDirectory_andFolder("UChat/Audio/Sent","AUD_FILE" +date+"."+ext);
                fileMediaType = "audio";

            }else if (fileType.contains("application")){
                fileName = getExternalDirectory_andFolder("UChat/Documents/Sent","DOC" +date+"."+ext);
                fileMediaType = "document";
            }

        //Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();

        assert fileMediaType != null;
        uploadFilesInChat(data,fileMediaType,itsLocalName);




    }

    public void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    private void updateStatus(String state){

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("state",state);

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(sender_user_id).child("userState")
                .updateChildren(onlineState);

    }

    @Override
    public void onBackPressed() {
        ViewGroup.LayoutParams params = attachment_layouout.getLayoutParams();
        if (attachment_layouout.getHeight()>0) {
            params.height = 0;
            attachment_layouout.setVisibility(View.INVISIBLE);
            attachment_layouout.setLayoutParams(params);
        } else if (emoticonGIFKeyboardFragment == null || !emoticonGIFKeyboardFragment.handleBackPressed())


            super.onBackPressed();
    }



    /*///////////////////////////--------------------------------------------------
                 Get MY location and send to a friend on realtime
    ----------------------------------------------/////////////////////////////////*/
    public void getAddress() {

        Address locationAddress=getAddress(latitude,longitude);

        if(locationAddress!=null)
        {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation+="\n"+address1;

                if (!TextUtils.isEmpty(city))
                {
                    currentLocation+="\n"+city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else
                {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+="\n"+postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation+="\n"+state;

                if (!TextUtils.isEmpty(country))
                    currentLocation+="\n"+country;


////              Intent intent = new Intent(ChatActivity.this,SingLocationActivity.class);
//                Intent intent = new Intent(ChatActivity.this,MapboxSingleLocationActivity.class);
//                intent.putExtra("long",longitude);
//                intent.putExtra("lat",latitude);
//                intent.putExtra("friendName", receiver_user_name);
//                startActivity(intent);

                alertDialog = new AlertDialog.Builder(ChatActivity.this).create();
                alertDialog.setTitle("Send Current Location...");
                alertDialog.setMessage("\n"+currentLocation+"\n\n");
                final String finalCurrentLocation = currentLocation;
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = finalCurrentLocation;
                        String type = "Lng:"+longitude;
                        String caption = "Lat:"+latitude;
                        SendMessage(message,type,caption);
                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();

            }

        }

    }

    /**
     * Method to display the location on UI
     * */
    private void getLocation() {

        if (isPermissionGranted) {

            try
            {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }

        }

    }
    public Address getAddress(double latitude, double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(ChatActivity.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(ChatActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }
    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this,resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }
    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation();
    }
    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }
    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
        isPermissionGranted=true;
    }
    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }
    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }
    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }



}
