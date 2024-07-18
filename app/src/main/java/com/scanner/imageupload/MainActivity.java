package com.scanner.imageupload;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Magnifier;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    TextView tvDisplay;
    ImageView imageView,imageEdit;
    Button uploadBtn;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay=findViewById(R.id.tvDisplay);
        imageView=findViewById(R.id.imageView);
        imageEdit=findViewById(R.id.imageEdit);
        uploadBtn=findViewById(R.id.uploadBtn);
        progressBar=findViewById(R.id.progressBar);

        uploadBtn.setOnClickListener(v -> {
            BitmapDrawable bitmapDrawable=(BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,outputStream);

            byte[] imageBytes = outputStream.toByteArray();
            String image64 = Base64.encodeToString(imageBytes,Base64.DEFAULT);
            tvDisplay.setText(image64);

            stringRequest(image64);
        });

        ActivityResultLauncher<Intent>imagepicker =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode()==Activity.RESULT_OK){
                            Intent intent = result.getData();
                            Uri uri =intent.getData();


                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                                imageView.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }
                });

        imageEdit.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagepicker.launch(intent);
                            return null;
                        }
                    });
        });
    }

    //=============

private boolean checkCameraPermission(){
        boolean hasPermission = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            hasPermission=true;
        }else
            hasPermission= false;
        String[] permission ={Manifest.permission.CAMERA};
    ActivityCompat.requestPermissions(this,permission,102);

    return hasPermission;
}


    //=============

    ActivityResultLauncher<Intent> gellaryLauncher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK){
                        tvDisplay.setText("image Selected");

                        Intent intent = result.getData();
                        Uri uri = intent.getData();

                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            imageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }else
                        tvDisplay.setText("image Not Selected");
                }
            });

    //=============


    private void stringRequest(String image62){

        String uri ="https://appsourcecode.xyz/apps/file.php";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                tvDisplay.setText(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tvDisplay.setText("Error");
                progressBar.setVisibility(View.GONE);

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map myMap = new HashMap<String,String>();

                myMap.put("pass","123");
                myMap.put("email","tapu@gmail.com");
                myMap.put("image",image62);

                return myMap;
            }
        };


        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
}