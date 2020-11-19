package com.example.notework.CustomAdapter;

import android.content.Context;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notework.Models.Message;
import com.example.notework.Models.Note;
import com.example.notework.R;

import java.util.ArrayList;

import retrofit2.Callback;

public class GhiChuAdapter extends RecyclerView.Adapter<GhiChuAdapter.ViewHolder> implements View.OnCreateContextMenuListener {
    ArrayList<Note> Notes;
    private OnItemClickListener mListener;
    private ViewHolder viewHolder;
    private int ViTri;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(viewHolder.getAdapterPosition(), 101, 0, "Xoá Ghi Chú").setIcon(R.drawable.ic_baseline_delete_24);
    }

    public int GetPosition(){
        return ViTri;
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public GhiChuAdapter(ArrayList<Note> notes, OnItemClickListener listener) {
        Notes = notes;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.note_item, parent, false);
        view.setOnCreateContextMenuListener(this::onCreateContextMenu);
        viewHolder = new ViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setViewHolder(Notes.get(position));

        //Long Click Item
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ViTri = position;
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return Notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        TextView tvTitleNote, tvSummaryNote;
        OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            tvTitleNote = (TextView) itemView.findViewById(R.id.tvTitleNote);
            tvSummaryNote = (TextView) itemView.findViewById(R.id.tvSummaryContent);

            itemView.setOnClickListener(this);
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

        @Override
        public void onClick(View v) {
            mListener.onItemClick(v, getAdapterPosition());
        }

        public int GetViTri(){
            return  getAdapterPosition();
        }
    }
}
