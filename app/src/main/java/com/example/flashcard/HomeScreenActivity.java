package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton addFlashcardBtn;
    private FlashcardAdapter adapter;
    private List<Flashcard> flashcardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        recyclerView = findViewById(R.id.recyclerView);
        addFlashcardBtn = findViewById(R.id.addFlashcardBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        flashcardList = new ArrayList<>();
        adapter = new FlashcardAdapter(flashcardList, this);
        recyclerView.setAdapter(adapter);

        // Load flashcards initially
        loadFlashcards();

        addFlashcardBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, FlashcardEditActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFlashcards(); // Refresh flashcards when returning to the screen
    }

    private void loadFlashcards() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("flashcards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    flashcardList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Flashcard flashcard = document.toObject(Flashcard.class);
                        flashcardList.add(flashcard);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load flashcards.", Toast.LENGTH_SHORT).show());
    }
}

