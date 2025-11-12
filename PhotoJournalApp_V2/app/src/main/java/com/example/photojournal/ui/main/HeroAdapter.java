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

public class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.HeroViewHolder> {
    private List<Entry> entries;
    private OnEntryClickListener onEntryClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    
    public interface OnEntryClickListener {
        void onEntryClick(Entry entry);
    }
    
    public HeroAdapter(List<Entry> entries, OnEntryClickListener onEntryClickListener) {
        this.entries = entries;
        this.onEntryClickListener = onEntryClickListener;
    }
    
    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_hero, parent, false);
        return new HeroViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        if (entries != null && !entries.isEmpty()) {
            Entry entry = entries.get(position % entries.size());
            holder.bind(entry);
        }
    }
    
    @Override
    public int getItemCount() {
        return entries != null && !entries.isEmpty() ? Integer.MAX_VALUE : 0;
    }
    
    public void updateEntries(List<Entry> newEntries) {
        this.entries = newEntries;
        notifyDataSetChanged();
    }
    
    class HeroViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewHero;
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewDate;
        
        HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewHero = itemView.findViewById(R.id.imageViewHero);
            textViewTitle = itemView.findViewById(R.id.textViewHeroTitle);
            textViewDescription = itemView.findViewById(R.id.textViewHeroDescription);
            textViewDate = itemView.findViewById(R.id.textViewHeroDate);
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
                    .into(imageViewHero);
            }
            
            itemView.setOnClickListener(v -> {
                if (onEntryClickListener != null) {
                    onEntryClickListener.onEntryClick(entry);
                }
            });
        }
    }
}

