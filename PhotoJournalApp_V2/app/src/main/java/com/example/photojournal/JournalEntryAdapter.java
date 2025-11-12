package com.example.photojournal;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class JournalEntryAdapter extends ArrayAdapter<JournalEntry> {
    public JournalEntryAdapter(Context context, List<JournalEntry> entries) {
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JournalEntry entry = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_journal_entry, parent, false);
        }
        ImageView imageViewThumbnail = convertView.findViewById(R.id.imageViewThumbnail);
        TextView textViewNote = convertView.findViewById(R.id.textViewNote);

        imageViewThumbnail.setImageURI(Uri.parse(entry.getPhotoPath()));
        textViewNote.setText(entry.getNote());

        return convertView;
    }
}