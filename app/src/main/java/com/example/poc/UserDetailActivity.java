package com.example.poc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UserDetailActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://webservice-rsnp.onrender.com";

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        String nome = getIntent().getStringExtra("nome");
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        userId = getIntent().getStringExtra("_id");

        TextView textViewNome = findViewById(R.id.textViewNome);
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        TextView textViewPassword = findViewById(R.id.textViewPassword);

        textViewNome.setText("Nome: " + nome);
        textViewEmail.setText("Email: " + email);
        textViewPassword.setText("Password: " + password);

        Button buttonEdit = findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDetailActivity.this, EditUserActivity.class);
                intent.putExtra("_id", userId);
                intent.putExtra("nome", nome);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteUserTask().execute();
            }
        });
    }
    public void refreshUserList() {
        if (getParent() instanceof webservices) {
            ((webservices) getParent()).refreshUserList();
        }
    }
    private class DeleteUserTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(BASE_URL + "/user/");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("_id", userId);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(UserDetailActivity.this, "Usuário eliminado com sucesso", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new UserDeletedEvent());
                refreshUserList();
                finish();
            } else {
                Toast.makeText(UserDetailActivity.this, "Erro ao eliminar o usuário", Toast.LENGTH_SHORT).show();
            }
        }
    }
}