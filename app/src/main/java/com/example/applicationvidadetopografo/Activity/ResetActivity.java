package com.example.applicationvidadetopografo.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.applicationvidadetopografo.DAO.ConfiguracaoFirebase;
import com.example.applicationvidadetopografo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private BootstrapEditText et_mail_rec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        et_mail_rec = (BootstrapEditText) findViewById(R.id.et_rec_email);
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
    }

    public void resetPassword(View view){
        autenticacao.sendPasswordResetEmail(et_mail_rec.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            et_mail_rec.setText("");
                            Toast.makeText(ResetActivity.this, "Foi enviado um email para recuperação de acesso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ResetActivity.this, "Falha ao envia o email de recuperação", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
