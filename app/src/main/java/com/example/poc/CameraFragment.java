package com.example.poc;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

public class CameraFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    private ImageView imgCamera;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        imgCamera = view.findViewById(R.id.imgCamera);
        Button btnCamera = view.findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), CAMERA_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{CAMERA_PERMISSION}, REQUEST_CAMERA_PERMISSION);
                } else {
                    startCameraIntent();
                }
            }
        });

        return view;
    }

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
                Toast.makeText(requireContext(), "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), "Erro ao obter a imagem da câmera", Toast.LENGTH_SHORT).show();
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
}
