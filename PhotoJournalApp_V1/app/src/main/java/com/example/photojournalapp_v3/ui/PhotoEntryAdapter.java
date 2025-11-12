package com.example.photojournalapp_v3.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photojournalapp_v3.R;
import com.example.photojournalapp_v3.model.PhotoEntry;

public class PhotoEntryAdapter extends ListAdapter<PhotoEntry, PhotoEntryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PhotoEntry entry);
    }

    private OnItemClickListener listener;

    public PhotoEntryAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<PhotoEntry> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PhotoEntry>() {
                @Override
                public boolean areItemsTheSame(@NonNull PhotoEntry oldItem, @NonNull PhotoEntry newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull PhotoEntry oldItem, @NonNull PhotoEntry newItem) {
                    return oldItem.id == newItem.id
                            && oldItem.title.equals(newItem.title)
                            && ((oldItem.description == null && newItem.description == null)
                                || (oldItem.description != null && oldItem.description.equals(newItem.description)))
                            && oldItem.imagePath.equals(newItem.imagePath)
                            && oldItem.updatedAt == newItem.updatedAt;
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PhotoEntry entry = getItem(position);
        holder.title.setText(entry.title);
        Glide.with(holder.image.getContext())
                .load(entry.imagePath)
                .centerCrop()
                .into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(entry);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
        }
    }
}


