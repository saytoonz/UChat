package com.nsromapa.uchat.photoeditor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nsromapa.uchat.R;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {
    Context context;
    List<Integer> colorList;
    ColorAdapterListner listner;

    public ColorAdapter(Context context, ColorAdapterListner listner) {
        this.context = context;
        this.colorList = genColorList();
        this.listner = listner;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item,viewGroup,false);
        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        holder.color_section.setCardBackgroundColor(colorList.get(position));
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }


    public class ColorViewHolder extends RecyclerView.ViewHolder {

        public CardView color_section;

        public ColorViewHolder(View itemView) {
            super(itemView);
            color_section = (CardView)itemView.findViewById(R.id.color_section);

            color_section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner.onColorSelected(colorList.get(getAdapterPosition()));
                }
            });
        }
    }



    private List<Integer> genColorList() {
        List<Integer> colorList = new ArrayList<>();
        colorList.add(Color.parseColor("#b29176"));
        colorList.add(Color.parseColor("#735439"));
        colorList.add(Color.parseColor("#00519b"));
        colorList.add(Color.parseColor("#ff2f2f"));
        colorList.add(Color.parseColor("#ff6d8a"));
        colorList.add(Color.parseColor("#ff6a6a"));
        colorList.add(Color.parseColor("#8c001a"));
        colorList.add(Color.parseColor("#ffffff"));
        colorList.add(Color.parseColor("#8c001a"));
        colorList.add(Color.parseColor("#474b52"));
        colorList.add(Color.parseColor("#4f545c"));
        colorList.add(Color.parseColor("#2701d5"));
        colorList.add(Color.parseColor("#00FDD1"));
        colorList.add(Color.parseColor("#FF6A6A"));
        colorList.add(Color.parseColor("#FF6D8A"));
        colorList.add(Color.parseColor("#ff0074"));
        colorList.add(Color.parseColor("#ffdb99"));
        colorList.add(Color.parseColor("#FFD62A"));
        colorList.add(Color.parseColor("#936b08"));
        colorList.add(Color.parseColor("#b8860b"));
        colorList.add(Color.parseColor("#acd9e2"));
        colorList.add(Color.parseColor("#dcd8f3"));
        colorList.add(Color.parseColor("#f7ece8"));
        colorList.add(Color.parseColor("#f0dad1"));
        colorList.add(Color.parseColor("#e9c8ba"));
        colorList.add(Color.parseColor("#e1b6a3"));
        colorList.add(Color.parseColor("#daa48c"));
        colorList.add(Color.parseColor("#b64a1a"));
        colorList.add(Color.parseColor("#a346a3"));
        colorList.add(Color.parseColor("#ff2f2f"));
        colorList.add(Color.parseColor("#f96d11"));
        colorList.add(Color.parseColor("#000000"));
        return colorList;
    }



    public interface ColorAdapterListner {
        void onColorSelected(int color);
    }
}
