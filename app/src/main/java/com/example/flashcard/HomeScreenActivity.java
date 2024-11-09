package com.example.flashcard;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HomeScreenActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton addFlashcardBtn;
    private Button shuffleButton, randomQuestionButton;
    private FlashcardAdapter adapter;
    private List<Flashcard> flashcardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        recyclerView = findViewById(R.id.recyclerView);
        addFlashcardBtn = findViewById(R.id.addFlashcardBtn);
        shuffleButton = findViewById(R.id.shuffleButton);
        randomQuestionButton = findViewById(R.id.randomQuestionButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        flashcardList = new ArrayList<>();
        adapter = new FlashcardAdapter(flashcardList, this);
        recyclerView.setAdapter(adapter);

        loadFlashcards();

        addFlashcardBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, FlashcardEditActivity.class);
            startActivity(intent);
        });

        shuffleButton.setOnClickListener(v -> shuffleFlashcards());
        randomQuestionButton.setOnClickListener(v -> showRandomQuestion());
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
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Flashcard flashcard = document.toObject(Flashcard.class);
                        flashcardList.add(flashcard);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load flashcards.", Toast.LENGTH_SHORT).show());
    }

    private void shuffleFlashcards() {
        // Shuffle the list of flashcards
        Collections.shuffle(flashcardList);
        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
    }

    private void showRandomQuestion() {
        if (!flashcardList.isEmpty()) {
            // Select a random flashcard
            Random random = new Random();
            int randomIndex = random.nextInt(flashcardList.size());
            Flashcard randomFlashcard = flashcardList.get(randomIndex);

            // Start FlashcardViewActivity to display the random flashcard
            Intent intent = new Intent(HomeScreenActivity.this, FlashcardViewActivity.class);
            intent.putExtra("flashcard", randomFlashcard);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No flashcards available", Toast.LENGTH_SHORT).show();
        }
    }
}
