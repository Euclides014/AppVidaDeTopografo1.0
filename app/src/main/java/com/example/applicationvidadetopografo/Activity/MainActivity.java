package com.example.applicationvidadetopografo.Activity;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton login_googlebutton;
    private FirebaseAuth autenticacao;
    private GoogleApiClient mGoogleApiClient;
    private BootstrapEditText edtEmailLogin;
    private BootstrapEditText edtSenhaLogin;
    private BootstrapButton btnLogin;
    private LoginButton loginButtonFacebook;

    private CallbackManager mCallbackManager;
    private TextView textCadastrarUsuario, txt_rec_pass;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmailLogin = (BootstrapEditText) findViewById(R.id.edtEmail);
        edtSenhaLogin = (BootstrapEditText) findViewById(R.id.edtSenha);
        btnLogin = (BootstrapButton) findViewById(R.id.btnLoginMail);

        autenticacao = FirebaseAuth.getInstance();
        login_googlebutton = (SignInButton) findViewById(R.id.login_googlebutton);

        loginButtonFacebook = (LoginButton) findViewById(R.id.loginButtonFacebook);
        loginButtonFacebook.setReadPermissions("email", "public_profile");
        textCadastrarUsuario = (TextView) findViewById(R.id.textCadastrarUsuario);
        txt_rec_pass = findViewById(R.id.txt_rec_pass);
        permissaodeLocalizacao();
        permissaodeExtArmazen();
        inicializarFirebaseCallback();
        conectarGoogleApi();

        if (usuarioLogado()){
            Intent intentMinhaConta = new Intent(MainActivity.this, TelaMapaActivity.class);
            abriNovaActivity(intentMinhaConta);
        }else {
            clickBotao();
            clickButton();

            textCadastrarUsuario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, CadastroUsuarioActivity.class);
                    startActivity(intent);
                }
            });

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!edtEmailLogin.getText().toString().equals("") && !edtSenhaLogin.getText().toString().equals("")){

                        usuario = new Usuario();

                        usuario.setEmail(edtEmailLogin.getText().toString());
                        usuario.setSenha(edtSenhaLogin.getText().toString());

                        validarLogin();

                    }else{
                        Toast.makeText(MainActivity.this,"Preencha os campos de email e senha", Toast.LENGTH_LONG).show();
                    }
                }
            });

            txt_rec_pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ResetActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    private void clickButton() {
        loginButtonFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseLoginFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Erro no login com o Facebook", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void firebaseLoginFacebook(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        autenticacao.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(MainActivity.this, TelaMapaActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(MainActivity.this, "Erro ao autentica no Firebase", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void inicializarFirebaseCallback() {
        autenticacao = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void clickBotao(){
        login_googlebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginIn();
            }
        });
    }

    private void loginIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, 145);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 145){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if(result.isSuccess()){
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseLogin(account);
                }

        }
    }


    private void firebaseLogin(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        autenticacao.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(MainActivity.this, TelaMapaActivity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            alert("Falha na conexão");
                        }
                    }
                });
    }

    private void conectarGoogleApi() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        alert("Falha na conexão");
    }

    private void alert(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    public void permissaodeLocalizacao(){
        int PERMISSION = 1;
        String [] PERMISSION_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(this, PERMISSION_LOCATION, PERMISSION);
    }

    public void permissaodeExtArmazen(){
        int PERMISSION_ALL = 1;
        String [] PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, PERMISSION, PERMISSION_ALL);
    }

    public boolean usuarioLogado(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            return true;
        }else{
            return false;
        }
    }

    public void abriNovaActivity(Intent intent){
        startActivity(intent);
    }

    private void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail().toString(), usuario.getSenha().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            abrirTelaPrincipal();
                            Preferencias preferencias = new Preferencias(MainActivity.this);
                            preferencias.salvarUsuarioPreferencias(usuario.getEmail(), usuario.getSenha());
                            Toast.makeText(MainActivity.this,"Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"E-mail ou senha inválidos! Tente novamente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(MainActivity.this, TelaMapaActivity.class);
        finish();
        startActivity(intent);

    }
}
