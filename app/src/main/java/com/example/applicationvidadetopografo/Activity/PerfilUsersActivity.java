package com.example.applicationvidadetopografo.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicationvidadetopografo.DAO.ConfiguracaoFirebase;
import com.example.applicationvidadetopografo.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PerfilUsersActivity extends AppCompatActivity {

    private String idUser;
    private DatabaseReference reference;

    private TextView txtNomeUser, txtCpfUser, txtEmailUser, txtTelefUser, txtDataNascUser, txtEnderecoUser;
    private TextView txtBairroUser, txtCidadeUsuario, txtEstadoUser, txtEscolaridadeUser, txtOcupUser;
    private TextView txtExperienciaUser, txtExpEquipUser, txtDispViagemUser, txtExpSoftUser;

    private String strAux, strOcupacao, strEquip, strSoft;;
    private ArrayList<String> ocupacao = new ArrayList<>();
    private ArrayList<String> equipamentos = new ArrayList<>();
    private ArrayList<String> software = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_users);

        Intent intent = getIntent();
        if (intent != null){
            Bundle params = intent.getExtras();
            if (params != null ){
                String keyUser = params.getString("keyUser");
                idUser = keyUser;
            }
        }

        reference = ConfiguracaoFirebase.getFirebase();
        recoversvalues();
        queryDatabase ();

    }

    private void recoversvalues(){
        txtNomeUser = findViewById(R.id.txtNomeUser);
        txtCpfUser = findViewById(R.id.txtCpfUser);
        txtEmailUser = findViewById(R.id.txtEmailUser);
        txtTelefUser = findViewById(R.id.txtTelefUser);
        txtDataNascUser = findViewById(R.id.txtDataNascUser);
        txtEnderecoUser = findViewById(R.id.txtEnderecoUser);
        txtBairroUser = findViewById(R.id.txtBairroUser);
        txtCidadeUsuario = findViewById(R.id.txtCidadeUsuario);
        txtEstadoUser = findViewById(R.id.txtEstadoUser);
        txtEscolaridadeUser = findViewById(R.id.txtEscolaridadeUser);
        txtOcupUser = findViewById(R.id.txtOcupUser);
        txtExperienciaUser = findViewById(R.id.txtExperienciaUser);
        txtExpEquipUser = findViewById(R.id.txtExpEquipUser);
        txtDispViagemUser = findViewById(R.id.txtDispViagemUser);
        txtExpSoftUser = findViewById(R.id.txtExpSoftUser);
    }

    private void queryDatabase (){
        reference.child("usuarios").orderByChild("keyUsuario").equalTo(idUser)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            txtNomeUser.setText(postSnapshot.child("nome").getValue().toString());
                            txtCpfUser.setText(postSnapshot.child("cpf").getValue().toString());
                            txtEmailUser.setText(postSnapshot.child("email").getValue().toString());
                            txtTelefUser.setText(postSnapshot.child("telefone").getValue().toString());
                            txtDataNascUser.setText(postSnapshot.child("dataNascimento").getValue().toString());
                            txtEnderecoUser.setText(postSnapshot.child("rua").getValue().toString()+","+ postSnapshot.child("numero").getValue().toString());
                            txtBairroUser.setText(postSnapshot.child("bairro").getValue().toString());
                            txtCidadeUsuario.setText(postSnapshot.child("cidade").getValue().toString());
                            txtEstadoUser.setText(postSnapshot.child("estado").getValue().toString());
                            txtExperienciaUser.setText(postSnapshot.child("tempodeexperiencia").getValue().toString());
                            txtDispViagemUser.setText(postSnapshot.child("dispViagem").getValue().toString());

                            ocupacao = (ArrayList<String>) postSnapshot.child("ocupacao").getValue();
                            equipamentos = (ArrayList<String>) postSnapshot.child("expEquipamentos").getValue();
                            software = (ArrayList<String>) postSnapshot.child("softExp").getValue();
                            strOcupacao = ocupacao.toString();
                            strEquip = equipamentos.toString();
                            strSoft = software.toString();

                            strAux = strOcupacao.replace("[","");
                            strAux = strAux.replace("]","");
                            strOcupacao = strAux;
                            txtOcupUser.setText(strOcupacao);

                            strAux = strEquip.replace("[","");
                            strAux = strAux.replace("]","");
                            strEquip = strAux;
                            txtExpEquipUser.setText(strEquip);

                            strAux = strSoft.replace("[","");
                            strAux = strAux.replace("]","");
                            strSoft = strAux;
                            txtExpSoftUser.setText(strSoft);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(PerfilUsersActivity.this,"Erro de conexão com banco de dados", Toast.LENGTH_LONG).show();
                    }
                });

    }
}
