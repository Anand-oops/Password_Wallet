package com.anand.android.passwordwallet;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EntriesFragment extends Fragment {

    private static final String TAG = "EntriesFragment";
    EntryHelper entryHelper;
    ArrayList<EntryClass> list;
    EntryAdapter adapter;
    RecyclerView userEntries;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_entries, container, false);
        entryHelper = new EntryHelper(getActivity());
        userEntries = rootView.findViewById(R.id.entriesList);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_bar, menu);
        MenuItem search = menu.findItem(R.id.search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<EntryClass> filteredList = new ArrayList<>();

                for (EntryClass entry : list) {
                    if (entry.getName().toLowerCase().contains(s.toLowerCase())) {
                        filteredList.add(entry);
                    }
                }
                EntryAdapter adapter = new EntryAdapter(getActivity(), filteredList);
                userEntries.setAdapter(adapter);
                return true;
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        list = new ArrayList<>();
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EntryInput.class);
                startActivity(intent);
            }
        });
    }

    public void viewData() {
        Cursor cursor = entryHelper.ViewData();

        if (cursor.getCount() == 0) {
            TextView tv = requireActivity().findViewById(R.id.NoDataTV);
            tv.setVisibility(View.VISIBLE);
        } else {
            TextView tv = requireActivity().findViewById(R.id.NoDataTV);
            if (tv.getVisibility() == View.VISIBLE)
                tv.setVisibility(View.GONE);
            list.clear();
            if (cursor.moveToFirst()) {
                do {
                    Log.i(TAG, "viewData: " + cursor.getInt(0));
                    EntryClass entry = new EntryClass(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4));
                    list.add(entry);
                } while (cursor.moveToNext());
            }
            adapter = new EntryAdapter(getActivity(), list);
            userEntries.setHasFixedSize(true);
            userEntries.setAdapter(adapter);
            userEntries.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    @Override
    public void onResume() {
        viewData();
        super.onResume();
    }


}
