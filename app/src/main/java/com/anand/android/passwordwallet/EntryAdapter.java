package com.anand.android.passwordwallet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    private static final String TAG = "EntryAdapter";
    public ArrayList<EntryClass> userEntries;
    public Context context;

    public EntryAdapter(Context ctx, ArrayList<EntryClass> values) {
        this.userEntries = values;
        this.context = ctx;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.activity_entry_item, parent, false);
        return new EntryViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, final int position) {
        holder.mTextView.setText(userEntries.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = userEntries.get(position).getId();
                Intent intent = new Intent(context, EditEntry.class);
                intent.putExtra("id", id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userEntries.size();
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.catName);
        }
    }
}
