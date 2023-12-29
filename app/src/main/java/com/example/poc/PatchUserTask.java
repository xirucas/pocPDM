package com.example.poc;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PatchUserTask extends AsyncTask<String, Void, Boolean> {
    private static final String BASE_URL = "https://webservice-rsnp.onrender.com";
    private Context context;

    public PatchUserTask(Context context) {
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(String... params) {
        String jsonData = params[0];

        try {
            URL url = new URL(BASE_URL + "/user");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PATCH");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            try (OutputStream os = urlConnection.getOutputStream()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonData);
                writer.flush();
            }
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
            Toast.makeText(context, "Alterações salvas com sucesso", Toast.LENGTH_SHORT).show();
            ((EditUserActivity) context).finish();
        } else {
            Toast.makeText(context, "Erro ao salvar as alterações", Toast.LENGTH_SHORT).show();
        }
    }
}