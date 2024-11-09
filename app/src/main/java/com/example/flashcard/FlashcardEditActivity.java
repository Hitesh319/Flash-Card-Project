package com.example.flashcard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class FlashcardEditActivity extends AppCompatActivity {

    private EditText questionInput, answerInput;
    private Button saveButton;
    private String flashcardId; // Store flashcard ID for editing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_edit);

        questionInput = findViewById(R.id.questionInput);
        answerInput = findViewById(R.id.answerInput);
        saveButton = findViewById(R.id.saveButton);

        // Retrieve the flashcard data from the intent
        Flashcard flashcard = (Flashcard) getIntent().getSerializableExtra("flashcard");

        if (flashcard != null) {
            flashcardId = flashcard.getId(); // Set the flashcard ID for updating
            questionInput.setText(flashcard.getQuestion());
            answerInput.setText(flashcard.getAnswer());
        }

        saveButton.setOnClickListener(v -> {
            String question = questionInput.getText().toString();
            String answer = answerInput.getText().toString();
            saveFlashcardToFirebase(question, answer);
        });
    }

    private void saveFlashcardToFirebase(String question, String answer) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (flashcardId == null) {
            // If flashcardId is null, create a new flashcard
            flashcardId = firestore.collection("flashcards").document().getId();
        }

        Flashcard flashcard = new Flashcard(flashcardId, question, answer, false);

        // Update or create the flashcard document in Firestore
        firestore.collection("flashcards").document(flashcardId)
                .set(flashcard)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Flashcard saved!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after saving
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save flashcard!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
