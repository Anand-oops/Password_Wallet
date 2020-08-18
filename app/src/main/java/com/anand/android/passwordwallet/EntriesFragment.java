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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EntriesFragment extends Fragment {

    private static final String TAG = "EntriesFragment";
    EntryHelper entryHelper;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ListView userEntries;

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
                ArrayList<String> filteredList = new ArrayList<>();

                for (String entry : list) {
                    if (entry.toLowerCase().contains(s.toLowerCase())) {
                        filteredList.add(entry);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, filteredList);
                userEntries.setAdapter(adapter);
                return true;
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        list = new ArrayList<>();

        userEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = userEntries.getItemAtPosition(i).toString();
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
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

        if (cursor.getCount() == 0)
            Toast.makeText(getContext(), "No data to show", Toast.LENGTH_SHORT).show();
        else {
            list.clear();
            while (cursor.moveToNext()) {
                list.add(cursor.getString(1));
            }
            adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, list);
            Log.i(TAG, "viewData: " + list);
            userEntries.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        viewData();
        super.onResume();
    }


}
