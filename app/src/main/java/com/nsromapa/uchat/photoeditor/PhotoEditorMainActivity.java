package com.nsromapa.uchat.photoeditor;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nsromapa.uchat.ShowCapturedActivity;
import com.nsromapa.uchat.cameraUtils.Config;
import com.nsromapa.uchat.photoeditor.Interfaces.AddFrameListener;
import com.nsromapa.uchat.photoeditor.Interfaces.AddTextFragmentListner;
import com.nsromapa.uchat.photoeditor.Interfaces.BrushFragmentListener;
import com.nsromapa.uchat.photoeditor.Interfaces.EditImageFragmentListner;
import com.nsromapa.uchat.photoeditor.Interfaces.EmojiFragmentListener;
import com.nsromapa.uchat.photoeditor.Interfaces.FiltersListFragmentListener;
import com.nsromapa.uchat.photoeditor.Utils.BitmapUtils;
import com.nsromapa.uchat.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class PhotoEditorMainActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListner, BrushFragmentListener, EmojiFragmentListener, AddTextFragmentListner, AddFrameListener {
    public final static String pictureName = "img.jpg";
    public final static int PERMISSION_PICK_IMAGE = 100;
    public final static int PERMISSION_INSERT_IMAGE = 1001;
    private static final int CAMERA_REQUEST = 1002;


    ImageView doneEditing, cancelEditing;
    PhotoEditorView photoEditorView;
    PhotoEditor photoEditor;
    CardView but_filter_list, but_edit, but_brush, but_emoji, but_add_text, but_add_image, but_add_frame, but_crop;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FiltersListFragment filtersListFragment;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float constrantFinal = 1.0f;

    Uri image_selecter_uri;
    File mfile;



    //Load native image filters lib
    static {
        System.loadLibrary("NativeImageProcessor");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_photo);

        image_selecter_uri = Uri.parse(getIntent().getStringExtra("imageLoc"));
        //View
        photoEditorView = (PhotoEditorView) findViewById(R.id.filterImage_preview);
        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(), "emojione-android.ttf"))
                .setPinchTextScalable(true)
                .build();




        cancelEditing = findViewById(R.id.cancel_and_go_back_to_showcaptured_activity);
        doneEditing = findViewById(R.id.done_go_back_to_showcaptured_activity);
        but_edit = (CardView) findViewById(R.id.but_edit_image);
        but_filter_list = (CardView) findViewById(R.id.but_filter_list);
        but_brush = (CardView) findViewById(R.id.but_brush);
        but_emoji = (CardView) findViewById(R.id.but_emoji);
        but_add_text = (CardView) findViewById(R.id.but_add_text);
        but_add_image = (CardView) findViewById(R.id.but_add_image);
        but_add_frame = (CardView) findViewById(R.id.but_add_frame);
        but_crop = (CardView) findViewById(R.id.but_crop);


        but_filter_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if                    (filtersListFragment != null) {

                    filtersListFragment.show(getSupportFragmentManager(), filtersListFragment.getTag());

                } else {

                    FiltersListFragment filtersListFragment = FiltersListFragment.getInstance(null);
                    filtersListFragment.setListener(PhotoEditorMainActivity.this);
                    filtersListFragment.show(getSupportFragmentManager(), filtersListFragment.getTag());
                }
            }
        });

        but_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditImageFragment editImageFragment = EditImageFragment.getInstance();
                editImageFragment.setListner(PhotoEditorMainActivity.this);
                editImageFragment.show(getSupportFragmentManager(), editImageFragment.getTag());
            }
        });

        but_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Enable brush as default
                photoEditor.setBrushDrawingMode(true);

                BrushFragment brushFragment = BrushFragment.getInstance();
                brushFragment.setListener(PhotoEditorMainActivity.this);
                brushFragment.show(getSupportFragmentManager(), brushFragment.getTag());
            }
        });

        but_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmojiFragment emojiFragment = EmojiFragment.getInstance();
                emojiFragment.setListener(PhotoEditorMainActivity.this);
                emojiFragment.show(getSupportFragmentManager(), emojiFragment.getTag());
            }
        });

        but_add_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTextFragment addTextFragment = AddTextFragment.getInstance();
                addTextFragment.setListner(PhotoEditorMainActivity.this);
                addTextFragment.show(getSupportFragmentManager(), addTextFragment.getTag());
            }
        });

        but_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImageToPhoto();
            }
        });

        but_add_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameFragment frameFragment = FrameFragment.getInstance();
                frameFragment.setListener(PhotoEditorMainActivity.this);
                frameFragment.show(getSupportFragmentManager(), frameFragment.getTag());
            }
        });

        but_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCrop(image_selecter_uri);
            }
        });



        loadImage();





        cancelEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEditing();
            }
        });

        doneEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEditor.saveAsBitmap(new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(Bitmap saveBitmap) {

                        Intent intent = new Intent(PhotoEditorMainActivity.this,ShowCapturedActivity.class);
                        intent.putExtra(Config.KeyName.FILEPATH,SaveImageToStorage(saveBitmap));
                        startActivity(intent);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(PhotoEditorMainActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
    }



    private void cancelEditing() {
        Intent intent = new Intent(PhotoEditorMainActivity.this,ShowCapturedActivity.class);
        intent.putExtra(Config.KeyName.FILEPATH,image_selecter_uri.toString());
        startActivity(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }


    public String SaveImageToStorage(Bitmap bitmap){
        mfile = new File(getApplicationContext().getExternalFilesDir("/Images/Captured/"), "UChatNewImage.jpg");;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        FileOutputStream fo;
        try{
            fo = new FileOutputStream(mfile);
            fo.write(bytes.toByteArray());
            fo.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return mfile.toString();
    }



    private void startCrop(Uri uri) {
        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        uCrop.start(PhotoEditorMainActivity.this);

    }

    private void addImageToPhoto() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PERMISSION_INSERT_IMAGE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(PhotoEditorMainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }


    private void loadImage() {

        originalBitmap = BitmapFactory.decodeFile(image_selecter_uri.toString());
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(originalBitmap);


        filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
        filtersListFragment.setListener(this);

    }



    @Override
    public void onBrightnessChanged(int Brightness) {
        brightnessFinal = Brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(Brightness));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }
    @Override
    public void onSaturationChanged(float Saturation) {
        saturationFinal = Saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(Saturation));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }
    @Override
    public void onContrastChanged(float Contrast) {
        constrantFinal = Contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(Contrast));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }
    @Override
    public void onEditStarted() {

    }
    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(constrantFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));

        finalBitmap = myFilter.processFilter(bitmap);
    }
    @Override
    public void onFilterSelecter(Filter filter) {
        //resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == CAMERA_REQUEST) {

                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, image_selecter_uri, 800, 800);

                //clear bitmap memory
                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);

            } else if (requestCode == PERMISSION_PICK_IMAGE) {

                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

                image_selecter_uri = data.getData();

                //clear bitmap memory
                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);

            } else if (requestCode == PERMISSION_INSERT_IMAGE) {

                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 250, 250);
                photoEditor.addImage(bitmap);

            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);

            }


        } else if (requestCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    private void handleCropError(Intent data) {
        final Throwable cropErorr = UCrop.getError(data);
        if (cropErorr != null) {
            Toast.makeText(this, "" + cropErorr.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unexpected Error...", Toast.LENGTH_SHORT).show();
        }
    }
    private void handleCropResult(Intent data) {
       // final Uri resultUri = UCrop.getOutput(data);

        if (image_selecter_uri != null){

            photoEditorView.getSource().setImageURI(image_selecter_uri);

            Bitmap bitmap = ((BitmapDrawable)photoEditorView.getSource().getDrawable()).getBitmap();
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
            finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        } else {
            Toast.makeText(this, "Cannot retrieve crop image...", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBrushSizeChangedListener(float size) {
        photoEditor.setBrushSize(size);
    }
    @Override
    public void onBrushOpacityChangedListener(int opacity) {
        photoEditor.setOpacity(opacity);
    }
    @Override
    public void onBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
    }
    @Override
    public void onBrushStateChangedListener(boolean isEraser) {
        if (isEraser)
            photoEditor.brushEraser();
        else
            photoEditor.setBrushDrawingMode(true);
    }
    @Override
    public void onEmojiSelected(String emoji) {
        photoEditor.addEmoji(emoji);
    }
    @Override
    public void onAddTextButtonClicked(Typeface typeface, String text, int color) {
        photoEditor.addText(typeface, text, color);
    }
    @Override
    public void onAddFrame(int frame) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), frame);
        photoEditor.addImage(bitmap);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateStatus("online");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelEditing();
    }


    private void updateStatus(String state) {

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("state", state);

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userState")
                .updateChildren(onlineState);
    }
}
