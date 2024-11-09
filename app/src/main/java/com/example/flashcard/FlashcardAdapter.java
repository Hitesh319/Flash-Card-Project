package com.example.flashcard;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.ViewHolder> {

    private List<Flashcard> flashcards;
    private Context context;

    public FlashcardAdapter(List<Flashcard> flashcards, Context context) {
        this.flashcards = flashcards;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flashcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flashcard flashcard = flashcards.get(position);
        holder.questionText.setText(flashcard.getQuestion());

        // Display "Known" status if the flashcard is marked as known
        if (flashcard.isKnown()) {
            holder.knownStatusText.setText("Known");
            holder.knownStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.knownStatusText.setText("Not Known");
            holder.knownStatusText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Set up edit and delete button click listeners
        holder.deleteButton.setOnClickListener(v -> deleteFlashcard(flashcard, position));
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlashcardEditActivity.class);
            intent.putExtra("flashcard", flashcard);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    // ViewHolder class with knownStatusText
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionText, knownStatusText;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            knownStatusText = itemView.findViewById(R.id.knownStatusText); // Status text
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private void deleteFlashcard(Flashcard flashcard, int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("flashcards").document(flashcard.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    flashcards.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Flashcard deleted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete flashcard!", Toast.LENGTH_SHORT).show();
                });
    }
}

