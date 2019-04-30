package com.example.applicationvidadetopografo.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.applicationvidadetopografo.Classes.Usuario;
import com.example.applicationvidadetopografo.DAO.ConfiguracaoFirebase;
import com.example.applicationvidadetopografo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditPerfilActivity extends AppCompatActivity {

    private BootstrapEditText et_edit_nome, et_edit_email, et_edit_cpf, et_edit_Date_Nasc, et_edit_phone;
    private BootstrapEditText et_edit_CEP, et_edit_endereco, et_edit_numResi, et_edit_Bairro, et_edit_Cidade;
    private BootstrapEditText et_edit_Estado, et_edit_Cad_ExpSoft;

    private ImageView iv_edit_imagePerfil, btn_edit_Calendar;
    private Spinner select_edit_Escolaridade;

    private CheckBox check_edit_Aux, check_edit_Topo, check_edit_Nivl, check_edit_Desen, check_edit_Pilot;
    private CheckBox check_edit_Receptor, check_edit_Estacao, check_edit_Receptor_Geodesico, check_edit_Drone;
    private CheckBox check_edit_Nivel, check_edit_Teodolito, check_edit_Laser, check_edit_exp__Receptor;
    private CheckBox check_edit_exp__Estacao, check_edit_exp_Receptor_Geodesico, check_edit_exp_Drone;
    private CheckBox check_edit_exp_Nivel, check_edit_exp_Teodolito, check_edit_exp_Laser, check_edit_Exp_SoftAC;
    private CheckBox check_edit_Exp_SoftACC3D, check_edit_Exp_SoftTEVN, check_edit_Exp_SoftP4D, check_edit_Exp_SoftBTG;
    private CheckBox check_edit_Exp_SoftRe, check_edit_Exp_SoftAG, check_edit_Exp_SoftTBC, check_edit_Exp_SoftQG;
    private CheckBox check_edit_Exp_SoftTT, check_edit_Exp_SoftPS, check_edit_Exp_SoftGM;

    private RadioGroup rg_edit_DispViagem;

    private TextView showDateSelect;

    private BootstrapButton btn_edit_Conclui, btn_edit_Cancelar;

    private String txtOrigem = "";
    private String txtNome = "";
    private String txtEmail = "";
    private String txtCpf = "";
    private String txtDataNasc = "";
    private String txtTelefone = "";
    private String txtCEP = "";
    private String txtLogradouro = "";
    private String txtNumberResi = "";
    private String txtBairro = "";
    private String txtCity = "";
    private String txtEstado = "";
    private String txtKeyUser;
    private String emailUsarioLogado, enderecoToLoc, auxDisp, auxGraduation;
    private int tempoDeExp;
    private double endLat, endLong;

    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    private StorageReference storageReference;

    private Usuario usuario;

    private ArrayList<String> listaOcupacao = new ArrayList<>();
    private ArrayList<String> listaEquipamento = new ArrayList<>();
    private ArrayList<String> listaExpEquip = new ArrayList<>();
    private ArrayList<String> listaExpSoft = new ArrayList<>();
    private ArrayList<String> listaMobilidade = new ArrayList<>();
    private ArrayList<String> listaContrato = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        autenticacao = FirebaseAuth.getInstance();
        reference = ConfiguracaoFirebase.getFirebase();
        emailUsarioLogado = autenticacao.getCurrentUser().getEmail();
        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        carregaImagemPadrao();

        recuperarValores();
        rescueInformation();
        checkChoose();
        //selectGraduation();

        iv_edit_imagePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), 123);
            }
        });

        btn_edit_Calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catchData();
            }
        });

        btn_edit_Conclui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillDates();
                cadastrarFotoUsuario();
                editUser(usuario);
                if (editUser(usuario) == true){
                    Intent intent = new Intent(getApplicationContext(), PerfilUsuarioActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btn_edit_Cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToScreenMain();
            }

        });
    }

    private void catchData() {
        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int ano = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(EditPerfilActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                showDateSelect.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                calcularTempoExperiencia(dayOfMonth, month, year);
            }
        }, ano, mes, dia);
        datePickerDialog.show();
    }

    private void calcularTempoExperiencia(int diaIni, int mesIni, int anoIni) {
        Calendar calendar = Calendar.getInstance();
        int diaA = calendar.get(Calendar.DAY_OF_MONTH);
        int mesA = calendar.get(Calendar.MONTH);
        int anoA = calendar.get(Calendar.YEAR);

        int tempoDeExperiencia = anoA - anoIni;
        if (mesIni > mesA) {
            tempoDeExperiencia--;
        } else if (mesA == mesIni) {
            if (diaIni > diaA) {
                tempoDeExperiencia--;
            }
        }
        tempoDeExp = tempoDeExperiencia;
    }

    private void recuperarValores() {

        iv_edit_imagePerfil = findViewById(R.id.iv_edit_imagePerfil);
        btn_edit_Calendar = findViewById(R.id.btn_edit_Calendar);

        showDateSelect = findViewById(R.id.showDateSelect);

        et_edit_nome = findViewById(R.id.et_edit_nome);
        et_edit_email = findViewById(R.id.et_edit_email);
        et_edit_cpf = findViewById(R.id.et_edit_cpf);
        et_edit_Date_Nasc = findViewById(R.id.et_edit_Date_Nasc);
        et_edit_phone = findViewById(R.id.et_edit_phone);
        et_edit_CEP = findViewById(R.id.et_edit_CEP);
        et_edit_endereco = findViewById(R.id.et_edit_endereco);
        et_edit_numResi = findViewById(R.id.et_edit_numResi);
        et_edit_Bairro = findViewById(R.id.et_edit_Bairro);
        et_edit_Cidade = findViewById(R.id.et_edit_Cidade);
        et_edit_Estado = findViewById(R.id.et_edit_Estado);
        et_edit_Cad_ExpSoft = findViewById(R.id.et_edit_Cad_ExpSoft);

        check_edit_Aux = findViewById(R.id.check_edit_Aux);
        check_edit_Topo = findViewById(R.id.check_edit_Topo);
        check_edit_Nivl = findViewById(R.id.check_edit_Nivl);
        check_edit_Desen = findViewById(R.id.check_edit_Desen);
        check_edit_Pilot = findViewById(R.id.check_edit_Pilot);

        select_edit_Escolaridade = findViewById(R.id.select_edit_Escolaridade);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.bootstrap_dropdown_example_data,
                        android.R.layout.simple_spinner_dropdown_item);
        select_edit_Escolaridade.setAdapter(adapter);

        check_edit_Receptor = findViewById(R.id.check_edit_Receptor);
        check_edit_Estacao = findViewById(R.id.check_edit_Estacao);
        check_edit_Receptor_Geodesico = findViewById(R.id.check_edit_Receptor_Geodesico);
        check_edit_Drone = findViewById(R.id.check_edit_Drone);
        check_edit_Nivel = findViewById(R.id.check_edit_Nivel);
        check_edit_Teodolito = findViewById(R.id.check_edit_Teodolito);
        check_edit_Laser = findViewById(R.id.check_edit_Laser);

        check_edit_exp__Receptor = findViewById(R.id.check_edit_exp__Receptor);
        check_edit_exp__Estacao = findViewById(R.id.check_edit_exp__Estacao);
        check_edit_exp_Receptor_Geodesico = findViewById(R.id.check_edit_exp_Receptor_Geodesico);
        check_edit_exp_Drone = findViewById(R.id.check_edit_exp_Drone);
        check_edit_exp_Nivel = findViewById(R.id.check_edit_exp_Nivel);
        check_edit_exp_Teodolito = findViewById(R.id.check_edit_exp_Teodolito);
        check_edit_exp_Laser = findViewById(R.id.check_edit_exp_Laser);

        check_edit_Exp_SoftAC = findViewById(R.id.check_edit_Exp_SoftAC);
        check_edit_Exp_SoftACC3D = findViewById(R.id.check_edit_Exp_SoftACC3D);
        check_edit_Exp_SoftTEVN = findViewById(R.id.check_edit_Exp_SoftTEVN);
        check_edit_Exp_SoftP4D = findViewById(R.id.check_edit_Exp_SoftP4D);
        check_edit_Exp_SoftBTG = findViewById(R.id.check_edit_Exp_SoftBTG);
        check_edit_Exp_SoftRe = findViewById(R.id.check_edit_Exp_SoftRe);
        check_edit_Exp_SoftAG = findViewById(R.id.check_edit_Exp_SoftAG);
        check_edit_Exp_SoftTBC = findViewById(R.id.check_edit_Exp_SoftTBC);
        check_edit_Exp_SoftQG = findViewById(R.id.check_edit_Exp_SoftQG);
        check_edit_Exp_SoftTT = findViewById(R.id.check_edit_Exp_SoftTT);
        check_edit_Exp_SoftPS = findViewById(R.id.check_edit_Exp_SoftPS);
        check_edit_Exp_SoftGM = findViewById(R.id.check_edit_Exp_SoftGM);

        rg_edit_DispViagem = findViewById(R.id.rg_edit_DispViagem);

        btn_edit_Conclui = findViewById(R.id.btn_edit_Conclui);
        btn_edit_Cancelar = findViewById(R.id.btn_edit_Cancelar);

    }

    public void fillDates() {
        usuario = new Usuario();
        usuario.setNome(et_edit_nome.getText().toString());
        usuario.setEmail(et_edit_email.getText().toString());
        usuario.setTelefone(et_edit_phone.getText().toString());
        usuario.setCpf(et_edit_cpf.getText().toString());
        usuario.setDataNascimento(et_edit_Date_Nasc.getText().toString());
        usuario.setCep(et_edit_CEP.getText().toString());
        usuario.setRua(et_edit_endereco.getText().toString());
        usuario.setNumero(et_edit_numResi.getText().toString());
        usuario.setBairro(et_edit_Bairro.getText().toString());
        usuario.setCidade(et_edit_Cidade.getText().toString());
        usuario.setEstado(et_edit_Estado.getText().toString());
        usuario.setTempodeexperiencia((tempoDeExp + "anos de experiência"));
        usuario.setKeyUsuario(txtKeyUser);
        usuario.setFormacao(auxGraduation);

        if (check_edit_Topo.isChecked()) {
            listaOcupacao.add("Topografo");
        }
        if (check_edit_Nivl.isChecked()) {
            listaOcupacao.add("Nivelador");
        }
        if (check_edit_Desen.isChecked()) {
            listaOcupacao.add("Desenhista/Projestista");
        }
        if (check_edit_Aux.isChecked()) {
            listaOcupacao.add("Auxiliar");
        }
        if (check_edit_Pilot.isChecked()) {
            listaOcupacao.add("Piloto de Drone");
        }
        usuario.setOcupacao(listaOcupacao);

        if (check_edit_Receptor.isChecked()) {
            listaEquipamento.add("Receptor GNSS de Navegação");
        }
        if (check_edit_Receptor_Geodesico.isChecked()) {
            listaEquipamento.add("Receptor GNSS Geodesico");
        }
        if (check_edit_Nivel.isChecked()) {
            listaEquipamento.add("Nível Topográfico");
        }
        if (check_edit_Estacao.isChecked()) {
            listaEquipamento.add("Estação Total");
        }
        if (check_edit_Drone.isChecked()) {
            listaEquipamento.add("VANT/Drone");
        }
        if (check_edit_Teodolito.isChecked()) {
            listaEquipamento.add("Teodolito");
        }
        if (check_edit_Laser.isChecked()) {
            listaEquipamento.add("Laser Scanner");
        }
        usuario.setEquipamentos(listaEquipamento);

        if (check_edit_exp__Receptor.isChecked()) {
            listaExpEquip.add("Receptor GNSS de Navegação");
        }
        if (check_edit_exp_Receptor_Geodesico.isChecked()) {
            listaExpEquip.add("Receptor GNSS Geodesico");
        }
        if (check_edit_exp_Nivel.isChecked()) {
            listaExpEquip.add("Nível Topográfico");
        }
        if (check_edit_exp__Estacao.isChecked()) {
            listaExpEquip.add("Estação Total");
        }
        if (check_edit_exp_Drone.isChecked()) {
            listaExpEquip.add("VANT/Drone");
        }
        if (check_edit_exp_Teodolito.isChecked()) {
            listaExpEquip.add("Teodolito");
        }
        if (check_edit_exp_Laser.isChecked()) {
            listaExpEquip.add("Laser Scanner");
        }
        usuario.setExpEquipamentos(listaExpEquip);

        if (check_edit_Exp_SoftAC.isChecked()) {
            listaExpSoft.add("AutoCad");
        }
        if (check_edit_Exp_SoftACC3D.isChecked()) {
            listaExpSoft.add("AutoCad Civil 3D");
        }
        if (check_edit_Exp_SoftTEVN.isChecked()) {
            listaExpSoft.add("TopoEVN");
        }
        if (check_edit_Exp_SoftP4D.isChecked()) {
            listaExpSoft.add("Pix4D");
        }
        if (check_edit_Exp_SoftBTG.isChecked()) {
            listaExpSoft.add("Bentley TopoGRAPH");
        }
        if (check_edit_Exp_SoftRe.isChecked()) {
            listaExpSoft.add("Revit");
        }
        if (check_edit_Exp_SoftAG.isChecked()) {
            listaExpSoft.add("ArcGis");
        }
        if (check_edit_Exp_SoftTBC.isChecked()) {
            listaExpSoft.add("Trimble Business Center");
        }
        if (check_edit_Exp_SoftQG.isChecked()) {
            listaExpSoft.add("Qgis");
        }
        if (check_edit_Exp_SoftTT.isChecked()) {
            listaExpSoft.add("TopconTools");
        }
        if (check_edit_Exp_SoftPS.isChecked()) {
            listaExpSoft.add("PhotoSCAN");
        }
        if (check_edit_Exp_SoftGM.isChecked()) {
            listaExpSoft.add("Global Mapper");
        }
        listaExpSoft.add(et_edit_Cad_ExpSoft.getText().toString());
        usuario.setSoftExp(listaExpSoft);

        usuario.setVeiculo(listaMobilidade);
        usuario.setTipodecontrato(listaContrato);

        toLocation();
        usuario.setLatitude(String.valueOf(endLat));
        usuario.setLongitude(String.valueOf(endLong));

        usuario.setDispViagem(auxDisp);

    }

    private void rescueInformation() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        txtOrigem = bundle.getString("origem");

        if (txtOrigem.equals("editarUsuario")) {
            txtNome = bundle.getString("nome");
            txtEmail = bundle.getString("email");
            txtCpf = bundle.getString("CPF");
            txtDataNasc = bundle.getString("DataNasc");
            txtTelefone = bundle.getString("telefone");
            txtCEP = bundle.getString("CEP");
            txtLogradouro = bundle.getString("endereco");
            txtNumberResi = bundle.getString("numero");
            txtBairro = bundle.getString("bairro");
            txtCity = bundle.getString("cidade");
            txtEstado = bundle.getString("estado");
            txtKeyUser = bundle.getString("keyUsuario");
            listaMobilidade = bundle.getStringArrayList("listaMobilidade");
            listaContrato = bundle.getStringArrayList("listaContrato");

            et_edit_nome.setText(txtNome);
            et_edit_email.setText(txtEmail);
            et_edit_cpf.setText(txtCpf);
            et_edit_Date_Nasc.setText(txtDataNasc);
            et_edit_phone.setText(txtTelefone);
            et_edit_CEP.setText(txtCEP);
            et_edit_endereco.setText(txtLogradouro);
            et_edit_numResi.setText(txtNumberResi);
            et_edit_Bairro.setText(txtBairro);
            et_edit_Cidade.setText(txtCity);
            et_edit_Estado.setText(txtEstado);
            et_edit_Estado.setText(txtEstado);

        }
    }

    private void carregaImagemPadrao() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://applicationvidadetopografo.appspot.com/fotoPerfilUsuario/" + emailUsarioLogado + ".jpg");

        final int heigth = 130;
        final int width = 130;

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).resize(width, heigth).centerCrop().into(iv_edit_imagePerfil);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void cadastrarFotoUsuario() {
        StorageReference montaImagem = storageReference.child("fotoPerfilUsuario/" + emailUsarioLogado + ".jpg");
        iv_edit_imagePerfil.setDrawingCacheEnabled(true);
        iv_edit_imagePerfil.buildDrawingCache();

        Bitmap bitmap = iv_edit_imagePerfil.getDrawingCache();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte[] data = byteArray.toByteArray();

        UploadTask uploadTask = montaImagem.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri dowloadUrl = taskSnapshot.getUploadSessionUri();
                usuario = new Usuario();
                usuario.setUrlPerfil(dowloadUrl.toString());
                carregaImagemPadrao();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final int height = 130;
        final int width = 130;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                Uri imagemSelecionada = data.getData();
                Picasso.get().load(imagemSelecionada.toString()).resize(width, height).centerCrop().into(iv_edit_imagePerfil);

            }
        }
    }

    private boolean editUser(final Usuario usuario) {
        btn_edit_Conclui.setEnabled(false);

        try {
            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            reference.child(txtKeyUser).setValue(usuario);
            Toast.makeText(getApplicationContext(), "Dados atualizados com sucesso!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {

        }
        return true;
    }

    private void goToScreenMain() {
        AlertDialog.Builder alert = new AlertDialog.Builder(EditPerfilActivity.this);
        alert.setTitle("Logoff");
        alert.setIcon(R.drawable.ic_aviso)
                .setMessage("Suas alterações serão perdidas. Tem certeza que quer cancelar?")
                .setCancelable(false)
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), TelaMapaActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void toLocation() {
        enderecoToLoc = (et_edit_CEP.getText().toString());
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(enderecoToLoc, 1);
            Address address = addresses.get(0);
            endLat = address.getLatitude();
            endLong = address.getLongitude();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkChoose(){
        rg_edit_DispViagem.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if ( checkedId == R.id.rb_edit_Disp ){
                    auxDisp = "Disponível para viagem";
                } else {
                    auxDisp = "Não disponível para viagem";
                }
            }
        });
    }

    private void selectGraduation(){
        select_edit_Escolaridade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                auxGraduation = (parent.getOnItemClickListener().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
