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

        // Set up delete button click listener
        holder.deleteButton.setOnClickListener(v -> deleteFlashcard(flashcard, position));

        // Set up edit button click listener (if needed for edit functionality)
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

    // Method to delete flashcard from Firestore and update RecyclerView
    private void deleteFlashcard(Flashcard flashcard, int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Log the ID to ensure it’s correct
        Log.d("DeleteDebug", "Attempting to delete flashcard with ID: " + flashcard.getId());

        // Delete flashcard from Firestore by ID
        firestore.collection("flashcards").document(flashcard.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove the flashcard from the local list and notify the adapter
                    flashcards.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, flashcards.size());
                    Toast.makeText(context, "Flashcard deleted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete flashcard!", Toast.LENGTH_SHORT).show();
                    Log.d("DeleteDebug", "Deletion failed", e);
                });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

