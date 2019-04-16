package com.example.applicationvidadetopografo.Activity;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.applicationvidadetopografo.Classes.Usuario;
import com.example.applicationvidadetopografo.DAO.ConfiguracaoFirebase;
import com.example.applicationvidadetopografo.Helper.Preferencias;
import com.example.applicationvidadetopografo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private BootstrapEditText nome;
    private BootstrapEditText email;
    private BootstrapEditText senha1;
    private BootstrapEditText senha2;
    private BootstrapEditText telefone;
    private BootstrapButton btncadastar;
    private BootstrapButton btncancelar;
    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome = (BootstrapEditText) findViewById(R.id.edtCadNome);
        email = (BootstrapEditText) findViewById(R.id.edtCadEmail);
        telefone = (BootstrapEditText) findViewById(R.id.edtCadTelefone);
        senha1 = (BootstrapEditText) findViewById(R.id.edtCadSenha1);
        senha2 = (BootstrapEditText) findViewById(R.id.edtCadSenha2);

        btncadastar = (BootstrapButton) findViewById(R.id.btnCadastrar);
        btncancelar = (BootstrapButton) findViewById(R.id.btnCancelar);

        btncadastar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (senha1.getText().toString().equals(senha2.getText().toString())) {
                    usuario = new Usuario();
                    usuario.setNome(nome.getText().toString());
                    usuario.setEmail(email.getText().toString());
                    usuario.setTelefone(telefone.getText().toString());
                    usuario.setSenha(senha1.getText().toString());
                    usuario.setTipoUsuario("Comum");

                    cadastroUsuario();

                    if(insereUsuario(usuario) == true){
                        autenticacao.signOut();
                        Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }else{
                    Toast.makeText(CadastroUsuarioActivity.this, "As senhas não correspondem. Tente novamente!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void cadastroUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    insereUsuario(usuario);
                }else{
                    String erroExcecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha mais forte, contendo no minimo 8 caracteres letras e numeros";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "E-mail inválido! Por favor digite um e-mail válido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "E-mail já cadastrado! Por favor insira um novo e-mail";
                    }catch (Exception e) {
                        erroExcecao = "Erro ao efetuar o cadastro!";
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroUsuarioActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean insereUsuario(Usuario usuario){
        try{
            reference = ConfiguracaoFirebase.getFirebase().child("usuariosPreCadastrados");
            reference.push().setValue(usuario);
            Toast.makeText(CadastroUsuarioActivity.this, "Usuario cadastrado com sucesso", Toast.LENGTH_LONG).show();
            return true;

        }catch(Exception e){
            Toast.makeText(CadastroUsuarioActivity.this, "Erro ao gravar o usuario", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
    }
}
