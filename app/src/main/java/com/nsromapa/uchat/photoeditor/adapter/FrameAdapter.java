package com.nsromapa.uchat.photoeditor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nsromapa.uchat.R;

import java.util.ArrayList;
import java.util.List;

public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.FrameViewHolder> {

    Context context;
    List<Integer> frameList;
    FrameAdapterListener listener;

    int row_selected = -1;

    public FrameAdapter(Context context, FrameAdapterListener listener) { 
        this.context = context;
        this.frameList = getFrameList();
        this.listener = listener;
    }

    private List<Integer> getFrameList() {
        List<Integer> result = new ArrayList<>();

        result.add(R.drawable.frame1);
        result.add(R.drawable.frame2);
        result.add(R.drawable.frame3);
        result.add(R.drawable.frame4);
        result.add(R.drawable.frame5);
        result.add(R.drawable.frame6);
        result.add(R.drawable.frame7);
        result.add(R.drawable.frame8);
        result.add(R.drawable.frame9);
        result.add(R.drawable.frame10);
        result.add(R.drawable.frame11);
        result.add(R.drawable.frame12);
        result.add(R.drawable.frame13);
        result.add(R.drawable.frame14);
        result.add(R.drawable.frame15);
        result.add(R.drawable.frame16);
        result.add(R.drawable.frame17);
        result.add(R.drawable.frame18);
        result.add(R.drawable.frame19);
        result.add(R.drawable.frame20);
        result.add(R.drawable.frame21);
        result.add(R.drawable.frame22);
        result.add(R.drawable.frame23);
        result.add(R.drawable.frame24);
        result.add(R.drawable.frame25);
        result.add(R.drawable.frame26);
        result.add(R.drawable.frame27);
        result.add(R.drawable.frame28);
        result.add(R.drawable.frame29);
        return result;
    }

    @NonNull
    @Override
    public FrameViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.frame_item,viewGroup,false);

        return new FrameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FrameViewHolder frameViewHolder, int i) {
        if (row_selected==i)
            frameViewHolder.image_check.setVisibility(View.VISIBLE);
        else
            frameViewHolder.image_check.setVisibility(View.INVISIBLE);

            frameViewHolder.image_frame.setImageResource(frameList.get(i));

    }

    @Override
    public int getItemCount() {
        return frameList.size();
    }

    public class FrameViewHolder extends RecyclerView.ViewHolder {
        ImageView image_check, image_frame;

        public FrameViewHolder(View itemView) {
            super(itemView);
            image_check = (ImageView)itemView.findViewById(R.id.image_check);
            image_frame = (ImageView)itemView.findViewById(R.id.image_frame);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onFrameSelected(frameList.get(getAdapterPosition()));

                    row_selected = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface FrameAdapterListener{
        void onFrameSelected(int frame);
    }
}
