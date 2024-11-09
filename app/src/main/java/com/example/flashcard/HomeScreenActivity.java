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

        loadFlashcards();

        addFlashcardBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, FlashcardEditActivity.class);
            startActivity(intent);
        });
    }

    private void loadFlashcards() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("flashcards");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flashcardList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Flashcard flashcard = dataSnapshot.getValue(Flashcard.class);
                    flashcardList.add(flashcard);
                }
                Collections.shuffle(flashcardList); // Shuffle feature
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeScreenActivity.this, "Failed to load flashcards.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
