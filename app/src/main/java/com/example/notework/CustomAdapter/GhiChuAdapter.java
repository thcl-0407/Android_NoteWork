package com.example.notework.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notework.Models.Note;
import com.example.notework.R;

import java.util.ArrayList;

public class GhiChuAdapter extends RecyclerView.Adapter<GhiChuAdapter.ViewHolder> {
    Context context;
    ArrayList<Note> Notes;

    public GhiChuAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        Notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setViewHolder(Notes.get(position));
    }

    @Override
    public int getItemCount() {
        return Notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitleNote, tvSummaryNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitleNote = (TextView) itemView.findViewById(R.id.tvTitleNote);
            tvSummaryNote = (TextView) itemView.findViewById(R.id.tvSummaryContent);
        }

        void setViewHolder(@NonNull Note note) {
            tvTitleNote.setText(note.getTitle());
            int last_index = (note.getContentNote().length() / 2);

            if (last_index < 10) {
                tvSummaryNote.setText(note.getContentNote() + ". . .");
                return;
            }

            tvSummaryNote.setText(note.getContentNote().substring(0, 20) + ". . .");
        }
    }
}
