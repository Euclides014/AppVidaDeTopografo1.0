package com.example.applicationvidadetopografo.Activity;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicationvidadetopografo.DAO.ConfiguracaoFirebase;
import com.example.applicationvidadetopografo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private TextView txtNomeUsuario;
    private TextView txtCpfUsuario;
    private TextView txtEmailUsuario;
    private TextView txtTelefUsuario;
    private TextView txtDataNasc;
    private TextView txtEnderecoUsuario;
    private TextView txtBairroUsuario;
    private TextView txtCidadeUsuario, txtExpEquipUsuario, txtDispViagemUsuario, txtExpSoftUsuario;
    private TextView txtEstadoUsuario, txtEscolaridadeUsuario, txtOcupUsuario, txtExperienciaUsuario;
    private ImageView imageView;

    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private StorageReference storageReference;

    private String emailUsarioLogado, strOcupacao, strEquip, strSoft;
    private String strAux;
    private ArrayList<String> ocupacao = new ArrayList<>();
    private ArrayList<String> equipamentos = new ArrayList<>();
    private ArrayList<String> software = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        autenticacao = FirebaseAuth.getInstance();
        reference = ConfiguracaoFirebase.getFirebase();
        emailUsarioLogado = autenticacao.getCurrentUser().getEmail();

        carregaImagemPadrao();

        recuperarValores();
        consultaBanco();

    }

    private void recuperarValores() {
        txtNomeUsuario = (TextView) findViewById(R.id.txtNomeUsuario);
        txtCpfUsuario = (TextView) findViewById(R.id.txtCpfUsuario);
        txtEmailUsuario = (TextView) findViewById(R.id.txtEmailUsuario);
        txtTelefUsuario = (TextView) findViewById(R.id.txtTelefUsuario);
        txtDataNasc = (TextView) findViewById(R.id.txtDataNasc);
        txtEnderecoUsuario = (TextView) findViewById(R.id.txtEnderecoUsuario);
        txtBairroUsuario = (TextView) findViewById(R.id.txtBairroUser);
        txtCidadeUsuario = (TextView) findViewById(R.id.txtCidadeUsuario);
        txtEstadoUsuario = (TextView) findViewById(R.id.txtEstadoUser);
        txtEscolaridadeUsuario = (TextView) findViewById(R.id.txtEscolaridadeUser);
        txtOcupUsuario = (TextView) findViewById(R.id.txtOcupUser);
        txtExperienciaUsuario = (TextView) findViewById(R.id.txtExperienciaUser);
        txtExpEquipUsuario = (TextView) findViewById(R.id.txtExpEquipUser);
        txtDispViagemUsuario = (TextView) findViewById(R.id.txtDispViagemUsuario);
        txtExpSoftUsuario = (TextView) findViewById(R.id.txtExpSoftUsuario);

        imageView = (ImageView) findViewById(R.id.imageView5);
    }

    private void consultaBanco(){
        reference.child("usuarios").orderByChild("email").equalTo(emailUsarioLogado)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            txtNomeUsuario.setText(postSnapshot.child("nome").getValue().toString());
                            txtCpfUsuario.setText(postSnapshot.child("cpf").getValue().toString());
                            txtEmailUsuario.setText(postSnapshot.child("email").getValue().toString());
                            txtTelefUsuario.setText(postSnapshot.child("telefone").getValue().toString());
                            txtDataNasc.setText(postSnapshot.child("dataNascimento").getValue().toString());
                            txtEnderecoUsuario.setText(postSnapshot.child("rua").getValue().toString()+","+ postSnapshot.child("numero").getValue().toString());
                            txtBairroUsuario.setText(postSnapshot.child("bairro").getValue().toString());
                            txtCidadeUsuario.setText(postSnapshot.child("cidade").getValue().toString());
                            txtEstadoUsuario.setText(postSnapshot.child("estado").getValue().toString());
                            txtExperienciaUsuario.setText(postSnapshot.child("tempodeexperiencia").getValue().toString());
                            txtDispViagemUsuario.setText(postSnapshot.child("dispViagem").getValue().toString());

                            ocupacao = (ArrayList<String>) postSnapshot.child("ocupacao").getValue();
                            equipamentos = (ArrayList<String>) postSnapshot.child("expEquipamentos").getValue();
                            software = (ArrayList<String>) postSnapshot.child("softExp").getValue();
                            strOcupacao = ocupacao.toString();
                            strEquip = equipamentos.toString();
                            strSoft = software.toString();

                            strAux = strOcupacao.replace("[","");
                            strAux = strAux.replace("]","");
                            strOcupacao = strAux;
                            txtOcupUsuario.setText(strOcupacao);

                            strAux = strEquip.replace("[","");
                            strAux = strAux.replace("]","");
                            strEquip = strAux;
                            txtExpEquipUsuario.setText(strEquip);

                            strAux = strSoft.replace("[","");
                            strAux = strAux.replace("]","");
                            strSoft = strAux;
                            txtExpSoftUsuario.setText(strSoft);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(PerfilUsuarioActivity.this,"Erro de conexão com banco de dados", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void carregaImagemPadrao() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://applicationvidadetopografo.appspot.com/fotoPerfilUsuario/" + emailUsarioLogado + ".jpg");

        final int heigth = 130;
        final int width = 130;

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).resize(width, heigth).centerCrop().into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}