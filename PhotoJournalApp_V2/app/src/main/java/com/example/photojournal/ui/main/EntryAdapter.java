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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private List<Entry> entries;
    private OnEntryClickListener onEntryClickListener;
    private OnEntryDeleteListener onEntryDeleteListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    
    public interface OnEntryClickListener {
        void onEntryClick(Entry entry);
    }
    
    public interface OnEntryDeleteListener {
        void onEntryDelete(Entry entry);
    }
    
    public EntryAdapter(List<Entry> entries, OnEntryClickListener onEntryClickListener, OnEntryDeleteListener onEntryDeleteListener) {
        this.entries = entries;
        this.onEntryClickListener = onEntryClickListener;
        this.onEntryDeleteListener = onEntryDeleteListener;
    }
    
    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_entry, parent, false);
        return new EntryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.bind(entry);
        // Animate item appearance
        animateItem(holder.itemView, position);
    }
    
    private void animateItem(View itemView, int position) {
        itemView.setAlpha(0f);
        itemView.setTranslationY(50f);
        itemView.animate()
            .alpha(1f)
            .translationY(0f)
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
    
    class EntryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewThumbnail;
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewDate;
        
        EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
        
        void bind(Entry entry) {
            textViewTitle.setText(entry.getTitle() != null && !entry.getTitle().isEmpty() 
                ? entry.getTitle() : "Untitled");
            textViewDescription.setText(entry.getDescription());
            
            // Format date
            if (entry.getDateTaken() > 0) {
                textViewDate.setText(dateFormat.format(new Date(entry.getDateTaken())));
            } else {
                textViewDate.setText(dateFormat.format(new Date(entry.getCreatedAt())));
            }
            
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
            
            itemView.setOnLongClickListener(v -> {
                if (onEntryDeleteListener != null) {
                    onEntryDeleteListener.onEntryDelete(entry);
                }
                return true;
            });
        }
    }
}

