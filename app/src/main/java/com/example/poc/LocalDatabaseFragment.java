package com.example.poc;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.poc.DataBaseHelper;
import com.example.poc.ListData;
import com.example.poc.User;
import java.util.ArrayList;

public class LocalDatabaseFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_database, container, false);

        // Initialize UI components
        Button openActivityButton = view.findViewById(R.id.buttonToOpenActivity);
        listView = view.findViewById(R.id.listUsers);
        userList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(adapter);

        // Load data from the local database
        loadDataFromDatabase();

        openActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ListaBD.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadDataFromDatabase() {
        try (DataBaseHelper dbHelper = new DataBaseHelper(requireContext())) {
            ArrayList<User> users = dbHelper.readUsers();

            userList.clear();

            for (User user : users) {
                userList.add(user.getName());
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

