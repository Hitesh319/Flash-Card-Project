package com.example.flashcard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
            saveFlashcardToFirebase(question, answer);
        });
    }

    private void saveFlashcardToFirebase(String question, String answer) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("flashcards");

        if (flashcardId == null) { // New flashcard
            flashcardId = databaseReference.push().getKey();
        }

        Flashcard flashcard = new Flashcard(flashcardId, question, answer, false);
        databaseReference.child(flashcardId).setValue(flashcard)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Flashcard saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to save flashcard!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
