package com.example.poc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;



public class ListaBD extends AppCompatActivity {

    ListAdapter listAdapter;
    ArrayList<ListData> dataArrayList = new ArrayList<>();

    ArrayList<User> users = new ArrayList<>();
    ListData listData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_bd);

        Button btnCreate = findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaBD.this, DataBaseTestActivity.class);
                startActivity(intent);
            }
        });

        try (DataBaseHelper dataBaseHelper = new DataBaseHelper(this)) {
            users = dataBaseHelper.readUsers();

            for (int i = 0; i < users.size(); i++) {
                listData = new ListData(users.get(i).getId(), users.get(i).getName(), users.get(i).getUsername());
                dataArrayList.add(listData);
            }

            listAdapter = new ListAdapter(this, dataArrayList);
            ListView listUsers = findViewById(R.id.listUsers);
            listUsers.setAdapter(listAdapter);
            listUsers.setClickable(true);
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error reading users", Toast.LENGTH_SHORT);
            toast.show();
            users.add(new User(-1, "error", "error", "error"));
        }


    }
}