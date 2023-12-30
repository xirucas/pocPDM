package com.example.poc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class webservices extends AppCompatActivity {
    private static final String BASE_URL = "https://webservice-rsnp.onrender.com";

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ListView listViewUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webservices);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonSave = findViewById(R.id.buttonSave);
        listViewUsers = findViewById(R.id.listViewUsers);

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                UserAPI selectedUser = (UserAPI) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(webservices.this, UserDetailActivity.class);
                intent.putExtra("_id", selectedUser.get_id());
                intent.putExtra("nome", selectedUser.getNome());
                intent.putExtra("email", selectedUser.getEmail());
                intent.putExtra("password", selectedUser.getPassword());
                startActivity(intent);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _id = null;
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                UserAPI user = new UserAPI(_id, name, email, password);

                new SendUserDataTask().execute(user);
            }
        });

        Button buttonBack = findViewById(R.id.buttonVoltar);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new GetUserListTask().execute();
    }

    private class SendUserDataTask extends AsyncTask<UserAPI, Void, Boolean> {
        @Override
        protected Boolean doInBackground(UserAPI... users) {
            try {
                URL url = new URL(BASE_URL + "/user");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setDoOutput(true);

                UserAPI user = users[0];

                String userJson = "{\"name\":\"" + user.getNome() + "\",\"email\":\"" + user.getEmail() + "\",\"password\":\"" + user.getPassword() + "\"}";

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(userJson);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(webservices.this, "User criado com sucesso!", Toast.LENGTH_SHORT).show();
                refreshUserList();
            } else {
                Toast.makeText(webservices.this, "Erro ao criar o user!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshUserList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserDeletedEvent(UserDeletedEvent event) {
        refreshUserList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
    public void refreshUserList() {
        new GetUserListTask().execute();
    }
    public void updateListView(List<UserAPI> userList) {
        ArrayAdapter<UserAPI> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, userList);
        listViewUsers.setAdapter(adapter);
    }
    private class GetUserListTask extends AsyncTask<Void, Void, List<UserAPI>> {
        @Override
        protected List<UserAPI> doInBackground(Void... voids) {
            try {
                URL url = new URL(BASE_URL + "/user");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return parseUserList(response.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(List<UserAPI> userList) {
            if (userList != null) {
                updateListView(userList);
                ArrayAdapter<UserAPI> adapter = new ArrayAdapter<>(webservices.this, android.R.layout.simple_list_item_1, android.R.id.text1, userList);
                listViewUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(webservices.this, "Erro ao obter lista de usuários", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private List<UserAPI> parseUserList(String json) {
        List<UserAPI> userList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String _id = jsonObject.getString("_id");
                String nome = jsonObject.getString("name");
                String email = jsonObject.getString("email");
                String password = jsonObject.getString("password");

                UserAPI user = new UserAPI(_id, nome, email, password);
                userList.add(user);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userList;
    }
}