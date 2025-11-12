package com.example.photojournal.ui.main;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.photojournal.R;
import com.example.photojournal.data.database.entities.Entry;
import java.util.List;

public class EntryRowAdapter extends RecyclerView.Adapter<EntryRowAdapter.EntryRowViewHolder> {
    private List<Entry> entries;
    private OnEntryClickListener onEntryClickListener;
    
    public interface OnEntryClickListener {
        void onEntryClick(Entry entry);
    }
    
    public EntryRowAdapter(List<Entry> entries, OnEntryClickListener onEntryClickListener) {
        this.entries = entries;
        this.onEntryClickListener = onEntryClickListener;
    }
    
    @NonNull
    @Override
    public EntryRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_entry_card, parent, false);
        return new EntryRowViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull EntryRowViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.bind(entry);
        // Animate item appearance
        animateItem(holder.itemView, position);
    }
    
    private void animateItem(View itemView, int position) {
        itemView.setAlpha(0f);
        itemView.setScaleX(0.8f);
        itemView.setScaleY(0.8f);
        itemView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(position * 50)
            .start();
    }
    
    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }
    
    public void updateEntries(List<Entry> newEntries) {
        this.entries = newEntries;
        notifyDataSetChanged();
    }
    
    class EntryRowViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewThumbnail;
        private TextView textViewTitle;
        
        EntryRowViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
        }
        
        void bind(Entry entry) {
            textViewTitle.setText(entry.getTitle() != null && !entry.getTitle().isEmpty() 
                ? entry.getTitle() : "Untitled");
            
            // Load image with Glide
            if (entry.getPhotoPath() != null && !entry.getPhotoPath().isEmpty()) {
                String photoPath = entry.getPhotoPath();
                Uri photoUri;
                if (photoPath.startsWith("file://") || photoPath.startsWith("/")) {
                    photoUri = photoPath.startsWith("file://") ? Uri.parse(photoPath) : Uri.parse("file://" + photoPath);
                } else {
                    photoUri = Uri.parse(photoPath);
                }
                Glide.with(itemView.getContext())
                    .load(photoUri)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(imageViewThumbnail);
            }
            
            itemView.setOnClickListener(v -> {
                if (onEntryClickListener != null) {
                    onEntryClickListener.onEntryClick(entry);
                }
            });
        }
    }
}

