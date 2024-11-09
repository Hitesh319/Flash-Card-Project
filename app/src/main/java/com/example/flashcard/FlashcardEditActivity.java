package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FlashcardEditActivity extends AppCompatActivity {

    private EditText questionInput, answerInput;
    private Button saveButton;
    private String flashcardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_edit);

        questionInput = findViewById(R.id.questionInput);
        answerInput = findViewById(R.id.answerInput);
        saveButton = findViewById(R.id.saveButton);

        Flashcard flashcard = (Flashcard) getIntent().getSerializableExtra("flashcard");
        if (flashcard != null) {
            flashcardId = flashcard.getId();
            questionInput.setText(flashcard.getQuestion());
            answerInput.setText(flashcard.getAnswer());
        }

        saveButton.setOnClickListener(v -> {
            String question = questionInput.getText().toString();
            String answer = answerInput.getText().toString();
            saveFlashcardToFirestore(question, answer);
        });
    }

    private void saveFlashcardToFirestore(String question, String answer) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Generate a unique document ID and store it in the Flashcard object
        String id = firestore.collection("flashcards").document().getId();
        Flashcard flashcard = new Flashcard(id, question, answer, false);

        firestore.collection("flashcards").document(id)
                .set(flashcard)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Flashcard saved!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FlashcardEditActivity.this, HomeScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save flashcard!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

}
