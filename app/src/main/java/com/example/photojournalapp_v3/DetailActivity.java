package com.example.photojournalapp_v3;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.photojournalapp_v3.model.PhotoEntry;
import com.example.photojournalapp_v3.vm.DetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "extra_id";

    private DetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);

        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        if (id == -1) {
            finish();
            return;
        }

        ImageView image = findViewById(R.id.image);
        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        MaterialButton btnDelete = findViewById(R.id.btnDelete);

        toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        viewModel.load(id);
        viewModel.getEntry().observe(this, entry -> {
            if (entry == null) return;
            title.setText(entry.title);
            description.setText(entry.description);
            Glide.with(this).load(entry.imagePath).centerCrop().into(image);
        });

        btnDelete.setOnClickListener(v -> {
            PhotoEntry entry = viewModel.getEntry().getValue();
            if (entry != null) {
                viewModel.delete(entry);
                finish();
            }
        });
    }
}


