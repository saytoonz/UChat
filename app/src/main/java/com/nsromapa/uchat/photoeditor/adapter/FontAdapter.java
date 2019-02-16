package com.nsromapa.uchat.photoeditor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsromapa.uchat.R;

import java.util.ArrayList;
import java.util.List;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontViewHolder> {

    Context context;
    List<String> fontList;
    FontAdapterClickListener listener;

    int row_selected = -1;


    public FontAdapter(Context context, FontAdapterClickListener listener) {
        this.context = context;
        this.listener = listener;
        fontList = loadFontList();

    }

    private List<String> loadFontList() {
        List<String> result = new ArrayList<>();
        result.add("Jokerman.TTF");
        result.add("JohnHandy.TTF");
        result.add("Orange.TTF");
        result.add("AlexBrush_Regular.ttf");
        result.add("amita_bold.ttf");
        result.add("berkshireswash_regular.ttf");
        result.add("bokr35w.ttf");
        result.add("Bookmndi.TTF");
        result.add("Carrington.ttf");
        result.add("CHASB___.TTF");
        result.add("Cookie_Regular.ttf");
        result.add("DancingScript_Regular.otf");
        result.add("Diploma.TTF");
        result.add("FREEBSCA.ttf");
        result.add("ITCKrist.TTF");
        result.add("kidnap.TTF");
        result.add("latha.ttf");
        result.add("lucasr.ttf");
        result.add("Nautilus.otf");
        result.add("NEWS701I.TTF");
        result.add("OpenSans-Bold.ttf");
        result.add("sans_.ttf");
        result.add("times.ttf");
        return result;
    }

    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.font_item, viewGroup, false);
        return new FontViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder fontViewHolder, int i) {
        if (row_selected == i)
            fontViewHolder.image_check.setVisibility(View.VISIBLE);
        else
            fontViewHolder.image_check.setVisibility(View.INVISIBLE);


        Typeface typeface = Typeface.createFromAsset(context.getAssets(), new StringBuilder("fonts/")
                .append(fontList.get(i)).toString());

        //fontViewHolder.text_font_name.setText(fontList.get(i));
        fontViewHolder.text_font_demo.setTypeface(typeface);

    }

    @Override
    public int getItemCount() {
        return fontList.size();
    }

    public class FontViewHolder extends RecyclerView.ViewHolder {
        TextView text_font_name, text_font_demo;
        ImageView image_check;

        public FontViewHolder(View itemView) {
            super(itemView);

            text_font_demo = (TextView) itemView.findViewById(R.id.text_font_demo);
//            text_font_name = (TextView) itemView.findViewById(R.id.text_font_name);
            image_check = (ImageView) itemView.findViewById(R.id.image_check);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onFontSelected(fontList.get(getAdapterPosition()));
                    row_selected = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface FontAdapterClickListener {
        void onFontSelected(String fontName);
    }
}
