package com.example.flashcard;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Retrieve the flashcard from the intent
        flashcard = (Flashcard) getIntent().getSerializableExtra("flashcard");

        if (flashcard != null) {
            questionText.setText(flashcard.getQuestion());
            answerText.setText(flashcard.getAnswer());
            answerText.setVisibility(View.GONE); // Hide answer initially
        }

        markAsKnownButton.setOnClickListener(v -> markAsKnown());

        questionText.setOnClickListener(v -> flipCard());
        answerText.setOnClickListener(v -> flipCard());
    }

    private void flipCard() {
        AnimatorSet flipOutAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_out);
        AnimatorSet flipInAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_in);

        if (isShowingQuestion) {
            // Flip from question to answer
            flipOutAnimator.setTarget(questionText);
            flipInAnimator.setTarget(answerText);
            flipOutAnimator.start();
            flipOutAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    questionText.setVisibility(View.GONE);
                    answerText.setVisibility(View.VISIBLE);
                    flipInAnimator.start();
                }

                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
        } else {
            // Flip from answer to question
            flipOutAnimator.setTarget(answerText);
            flipInAnimator.setTarget(questionText);
            flipOutAnimator.start();
            flipOutAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    answerText.setVisibility(View.GONE);
                    questionText.setVisibility(View.VISIBLE);
                    flipInAnimator.start();
                }

                @Override
                public void onAnimationStart(Animator animation) {}
                @Override
                public void onAnimationCancel(Animator animation) {}
                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
        }
        isShowingQuestion = !isShowingQuestion;
    }

    private void markAsKnown() {
        if (flashcard != null) {
            // Set the flashcard as known
            flashcard.setKnown(true);

            // Get the Firestore instance
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            // Update the document with the known state
            firestore.collection("flashcards").document(flashcard.getId())
                    .set(flashcard) // This overwrites the document with the updated flashcard data
                    .addOnSuccessListener(aVoid -> {
                        // Show confirmation to the user and update button text
                        Toast.makeText(this, "Marked as known", Toast.LENGTH_SHORT).show();
                        markAsKnownButton.setText("Known");
                        markAsKnownButton.setEnabled(false); // Optionally, disable the button to prevent repeated clicks
                    })
                    .addOnFailureListener(e -> {
                        // Show error if the update fails
                        Toast.makeText(this, "Failed to mark as known", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });
        }
    }
}
