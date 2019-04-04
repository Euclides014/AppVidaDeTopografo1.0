package com.example.applicationvidadetopografo.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.applicationvidadetopografo.DAO.ConfiguracaoFirebase;
import com.example.applicationvidadetopografo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class UploadCurriculo extends AppCompatActivity {

    private BootstrapButton btnEscolheArq;
    private BootstrapButton btnUploadArq;
    private BootstrapButton btnCancelUpload;
    Uri pdfURI;

    private FirebaseAuth autenticacao;
    private StorageReference storageReference;
    private String emailUsuarioLogado;
    private FirebaseDatabase database;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_curriculo);

        btnEscolheArq = (BootstrapButton) findViewById(R.id.btnEscolheArq);
        btnUploadArq = (BootstrapButton) findViewById(R.id.btnUploadArq);
        btnCancelUpload = (BootstrapButton) findViewById(R.id.btnCancelUpload);

        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        btnEscolheArq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UploadCurriculo.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
                        uploadCurriculo();
                }else {
                    ActivityCompat.requestPermissions(UploadCurriculo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        btnUploadArq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdfURI != null){
                    uploadfile(pdfURI);
                }
            }
        });

        btnCancelUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(UploadCurriculo.this, TelaMapaActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if( requestCode == 9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            uploadCurriculo();
        } else {
            Toast.makeText(UploadCurriculo.this, "É necessário permitir que o aplicativo acesse o armazenamento interno", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadCurriculo (){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 86 && resultCode ==RESULT_OK && data !=null){
            pdfURI = data.getData();
        } else {
            Toast.makeText(UploadCurriculo.this, "Selecione o arquivo", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadfile(Uri pdfURI){
        progressBar = new ProgressBar(this);
        progressBar.setProgress(0, true);
        progressBar.isShown();
        StorageReference uploadCurrículo = storageReference.child("currículoUsuário" + emailUsuarioLogado + "pdf");
            uploadCurrículo.putFile(pdfURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getUploadSessionUri().toString();
                    DatabaseReference reference = ConfiguracaoFirebase.getFirebase();
                    Toast.makeText(UploadCurriculo.this,"Currículo enviado com sucesso", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadCurriculo.this, "Falha ao envia o arquivo", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress(currentProgress);
                }
            });


    }
}

