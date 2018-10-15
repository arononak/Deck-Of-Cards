package com.example.deckofcard.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.deckofcard.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<String> imageUrls;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Picasso.get().load(imageUrls.get(position)).into(viewHolder.imageCard);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageCard;
        ViewHolder(View v) {
            super(v);
            imageCard = v.findViewById(R.id.image_card);
        }
    }
}
