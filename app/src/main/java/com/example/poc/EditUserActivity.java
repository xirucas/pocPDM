package com.example.poc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class EditUserActivity extends AppCompatActivity {
    private String userId;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        userId = getIntent().getStringExtra("_id");
        String nome = getIntent().getStringExtra("nome");
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        editTextName.setText(nome);
        editTextEmail.setText(email);
        editTextPassword.setText(password);

        Button buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void saveChanges() {
        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("_id", userId);
            jsonParam.put("name", name);
            jsonParam.put("email", email);
            jsonParam.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new PatchUserTask(EditUserActivity.this).execute(jsonParam.toString());
    }
}