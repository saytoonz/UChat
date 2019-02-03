package com.nsromapa.say.emogifstickerkeyboard.internal.sound;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nsromapa.say.R;

import java.io.File;
import java.util.List;

public class SoundAdapter extends BaseAdapter {

    private List<File> soundImages;
    private Context context;
    private LayoutInflater thisInflater;

    private ItemSelectListener mListener;

    public SoundAdapter(List<File> soundImages, Context context, ItemSelectListener listener) {
        this.soundImages = soundImages;
        this.context = context;
        this.thisInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return soundImages.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        View v = convertView;
        SoundAdapter.ViewHolder holder;
        if (v == null) {
            v = thisInflater.inflate(R.layout.item_sticker, parent, false);

            holder = new SoundAdapter.ViewHolder();
            holder.soundIv = v.findViewById(R.id.sticker_iv);
            v.setTag(holder);
        } else {
            holder = (SoundAdapter.ViewHolder) v.getTag();
        }



        final File soundImage = soundImages.get(position);
        if (soundImage != null) {
            Glide.with(context)
                    .load(soundImages.get(position))
                    .into(holder.soundIv);

            holder.soundIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.OnListItemSelected(soundImage);
                }
            });
        }
        return v;
    }



    /**
     * Callback listener to get notify when item is clicked.
     */
    interface ItemSelectListener {

        void OnListItemSelected(@NonNull File sticker);
    }


    private class ViewHolder {

        /**
         * Image view to display GIFs.
         */
        private ImageView soundIv;
    }
}
