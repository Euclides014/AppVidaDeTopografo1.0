package com.example.applicationvidadetopografo.Activity;

import android.app.usage.NetworkStatsManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicationvidadetopografo.Classes.Usuario;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private TextView txtNomeUsuario;
    private TextView txtCpfUsuario;
    private TextView txtEmailUsuario;
    private TextView txtTelefUsuario;
    private TextView txtDataNasc;
    private TextView txtEnderecoUsuario;
    private TextView txtBairroUsuario;
    private TextView txtCidadeUsuario;
    private TextView txtEstadoUsuario;
    private ImageView imageView;

    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private StorageReference storageReference;

    private String emailUsarioLogado;
    private ListView recycleViewOcupacao;
    private ArrayList<String> ocupacao = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        autenticacao = FirebaseAuth.getInstance();
        reference = ConfiguracaoFirebase.getFirebase();
        emailUsarioLogado = autenticacao.getCurrentUser().getEmail();
        recycleViewOcupacao = (ListView) findViewById(R.id.listViewOcupacao);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ocupacao);
        recycleViewOcupacao.setAdapter(adapter);

        reference.child("usuarios").child("ocupacao").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    ocupacao = (ArrayList<String>) s.child("usuario").child("ocupacao").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



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
        txtBairroUsuario = (TextView) findViewById(R.id.txtBairroUsuario);
        txtCidadeUsuario = (TextView) findViewById(R.id.txtCidadeUsuario);
        txtEstadoUsuario = (TextView) findViewById(R.id.txtEstadoUsuario);
        imageView = (ImageView) findViewById(R.id.imageView5);
    }

    private void consultaBanco(){
        reference.child("usuarios").orderByChild("email").equalTo(emailUsarioLogado)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                            Usuario usuario = postSnapShot.getValue(Usuario.class);
                            txtNomeUsuario.setText(usuario.getNome());
                            txtCpfUsuario.setText(usuario.getCpf());
                            txtEmailUsuario.setText(usuario.getEmail());
                            txtTelefUsuario.setText(usuario.getTelefone());
                            txtDataNasc.setText(usuario.getDataNascimento());
                            txtEnderecoUsuario.setText(usuario.getRua() + "," + usuario.getNumero());
                            txtBairroUsuario.setText(usuario.getBairro());
                            txtCidadeUsuario.setText(usuario.getCidade());
                            txtEstadoUsuario.setText(usuario.getEstado());
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
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://applicationvidadetopografo.appspot.com/iconUser.png");

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