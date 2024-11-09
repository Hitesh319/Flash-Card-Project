package com.example.flashcard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FlashcardViewActivity extends AppCompatActivity {

    private TextView questionText, answerText;
    private Button markAsKnownButton;
    private boolean isShowingQuestion = true;
    private Flashcard flashcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_view);

        questionText = findViewById(R.id.questionText);
        answerText = findViewById(R.id.answerText);
        markAsKnownButton = findViewById(R.id.markAsKnownButton);

        // Retrieve the flashcard passed from the adapter
        flashcard = (Flashcard) getIntent().getSerializableExtra("flashcard");

        // Set initial text
        if (flashcard != null) {
            questionText.setText(flashcard.getQuestion());
            answerText.setText(flashcard.getAnswer());
            answerText.setVisibility(View.GONE); // Initially hide answer
        }

        // Flip card on click
        questionText.setOnClickListener(v -> flipCard());
        answerText.setOnClickListener(v -> flipCard());

        // Mark as known functionality
        markAsKnownButton.setOnClickListener(v -> markAsKnown());
    }

    private void flipCard() {
        if (isShowingQuestion) {
            questionText.setVisibility(View.GONE);
            answerText.setVisibility(View.VISIBLE);
        } else {
            questionText.setVisibility(View.VISIBLE);
            answerText.setVisibility(View.GONE);
        }
        isShowingQuestion = !isShowingQuestion;
    }

    private void markAsKnown() {
        if (flashcard != null) {
            flashcard.setKnown(true);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("flashcards");
            databaseReference.child(flashcard.getId()).setValue(flashcard)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Marked as known", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to previous screen after marking as known
                        } else {
                            Toast.makeText(this, "Failed to mark as known", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
