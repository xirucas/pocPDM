package com.example.poc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    ImageView imgCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_main);

        imgCamera = findViewById(R.id.imgCamera);
        Button btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{CAMERA_PERMISSION}, REQUEST_CAMERA_PERMISSION);
                } else {
                    startCameraIntent();
                }
            }
        });
        //internet
        Button btnCheckConnection = findViewById(R.id.btnInternet);
        btnCheckConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verifica a ligação à internet
                if (isConnectedToInternet()) {
                    // Apresenta um popup que informa se está conectado
                    showToast("Conectado à Internet");
                } else {
                    // Apresenta um popup que informa que não está conectado
                    showToast("Sem Internet");
                }
            }
        });

        //localDataBase
        Button btnLocalDataBase = findViewById(R.id.btnLocalBD);
        btnLocalDataBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListaBD.class);
                startActivity(intent);
            }
        });

        //webServices
        Button btnWebServices = findViewById(R.id.btnWebServices);
        btnWebServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), webservices.class);
                startActivity(intent);
            }
        });
    }

    //camara
    private void startCameraIntent() {
        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(iCamera);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraIntent();
            } else {
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleCameraResult(Intent data) {
        if (data != null) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.containsKey("data")) {
                // Obtém os bytes da imagem diretamente
                byte[] imageData = convertIntentDataToBase64(data);

                // Converte os bytes para Base64
                Base64.encodeToString(imageData, Base64.DEFAULT);

                imgCamera.setImageBitmap(data.getParcelableExtra("data"));
            } else {
                Toast.makeText(this, "Erro ao obter a imagem da câmera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            handleCameraResult(data);
                        }
                    });

    private byte[] convertIntentDataToBase64(Intent data) {
        // Converte a imagem para bytes diretamente
        Bitmap img = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream imgoutput = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100,imgoutput);
        return imgoutput.toByteArray();
    }

    //Internet
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }

        return false;
    }

    // Função para mostrar um Toast
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    

}
