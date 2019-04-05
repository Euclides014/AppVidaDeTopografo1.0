package com.example.applicationvidadetopografo.Activity;

import android.Manifest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.location.Location;

import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapDropDown;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.applicationvidadetopografo.Classes.Endereco;
import com.example.applicationvidadetopografo.Classes.Usuario;
import com.example.applicationvidadetopografo.DAO.ConfiguracaoFirebase;
import com.example.applicationvidadetopografo.Providers.Util;
import com.example.applicationvidadetopografo.Providers.ZipCodeListener;
import com.example.applicationvidadetopografo.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class FormularioActivity extends AppCompatActivity {

    private BootstrapEditText nome;
    private BootstrapEditText cpf;
    private BootstrapEditText dataNascimento;
    private BootstrapEditText email;
    private BootstrapEditText telefone;
    private BootstrapEditText experiencia;
    private BootstrapEditText cep;
    private BootstrapEditText rua;
    private BootstrapEditText numero;
    private BootstrapEditText bairro;
    private BootstrapEditText cidade;
    private BootstrapEditText estado;
    private BootstrapEditText edtCadExpSoft;
    private BootstrapEditText edtCadInfor;
    private BootstrapDropDown selectEscolaridade;
    private ArrayList<String> listaOcupacao = new ArrayList();
    private ArrayList<String> listaEquipamento = new ArrayList<>();
    private ArrayList<String> listaMobilidade = new ArrayList<>();
    private ArrayList<String> listaExpEquip = new ArrayList<>();
    private ArrayList<String> listaExpSoft = new ArrayList<>();
    private ArrayList<String> listadeContratacao = new ArrayList<>();


    private RadioButton rbDisp, rbNDisp;

    private CheckBox checkTopo, checkAux, checkNivl, checkDesen, checkPilot, checkMoto, checkCarro, checkCarroOFFRoad, checkReceptor, checkReceptorGeodesico;
    private CheckBox checkNivel, checkEstacao, checkDrone, checkBoxLocalizacao, checkTeodolito;
    private CheckBox checkExpRGN, checkExpET, checkExpRGG, checkExpVantDrone, checkExpNT, checkExpTeo;
    private CheckBox checkExpSoftAC, checkExpSoftACC3D, checkExpSoftTEVN, checkExpSoftP4D, checkExpSoftBTG, checkExpSoftRe, checkExpSoftAG;
    private CheckBox checkExpSoftTBC, checkExpSoftQG, checkExpSoftTT, checkExpSoftPS, checkExpSoftGM;
    private CheckBox checkCLT, checkPJ, checkFreelancer;

    private BootstrapButton btnConclui, btnCancel, btEnviaCurriculo;

    private static final long UPDATE_INTERVAL = 10000;
    private static final long FASTEST_INTERVAL = 5000;

    private LocationRequest mLocationRequest;

    private Double latitude;
    private ImageView imageView;
    private String emailUsuarioLogado;
    private FirebaseAuth autenticacao;
    private StorageReference storageReference;
    private Double longitude;
    private DatabaseReference reference;
    private Usuario usuario;
    private Endereco endereco;
    private Util util;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        recuperarvalores();
        startLocationUpdates();

        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();
        imageView = (ImageView) findViewById(R.id.imagePerfil);
        carregaImagemPadrao();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,"Selecione uma imagem" ), 123);
            }
        });

        util = new Util(this,
                R.id.edtCadtCEP,
                R.id.edtCadtEndereco,
                R.id.edtCadtBairro,
                R.id.edtCadtCidade,
                R.id.edtCadtEstado);

        btnConclui = (BootstrapButton) findViewById(R.id.btnConclui);
        btEnviaCurriculo =(BootstrapButton) findViewById(R.id.btnEnviarCurriculo);
        btnCancel = (BootstrapButton) findViewById(R.id.btnCancel);

        btnConclui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                preencheDadosUsuario();
                cadastrarFotoUsuario();
                cadastrarUsuario(usuario);
                limparCampos();
                Intent intent = new Intent(FormularioActivity.this, TelaMapaActivity.class);
                startActivity(intent);
                finish();

            }
        });
        btEnviaCurriculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preencheDadosUsuario();
                cadastrarFotoUsuario();
                cadastrarUsuario(usuario);
                limparCampos();
                Intent intent = new Intent(FormularioActivity.this, UploadCurriculo.class);
                startActivity(intent);
                finish();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormularioActivity.this, TelaMapaActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void recuperarvalores() {

        nome = (BootstrapEditText) findViewById(R.id.edtCadtNome);
        cpf = (BootstrapEditText) findViewById(R.id.edtCadtCPF);
        numero = (BootstrapEditText) findViewById(R.id.edtCadtNumero);
        dataNascimento = (BootstrapEditText) findViewById(R.id.edtCadtDataNasc);
        email = (BootstrapEditText) findViewById(R.id.edtCadtEmail);
        telefone = (BootstrapEditText) findViewById(R.id.edtCadtTel);
        experiencia = (BootstrapEditText) findViewById(R.id.edtCadtExperiencia);
        cep = (BootstrapEditText) findViewById(R.id.edtCadtCEP);
        edtCadInfor = (BootstrapEditText) findViewById(R.id.edtCadInfor);
        edtCadExpSoft = (BootstrapEditText) findViewById(R.id.edtCadExpSoft);
        selectEscolaridade = (BootstrapDropDown) findViewById(R.id.selecEscolaridade);
        checkBoxLocalizacao = (CheckBox) findViewById(R.id.checkBoxLocalizacao);

        //valores das Checkbox ocupacao

        checkTopo = (CheckBox) findViewById(R.id.checkTopo);
        checkAux = (CheckBox) findViewById(R.id.checkAux);
        checkNivl = (CheckBox) findViewById(R.id.checkNivl);
        checkDesen = (CheckBox) findViewById(R.id.checkDesen);
        checkPilot = (CheckBox) findViewById(R.id.checkPilot);

        //valores das Checkbox Tipo de contratacao
        checkCLT = (CheckBox) findViewById(R.id.checkCLT);
        checkPJ = (CheckBox) findViewById(R.id.checkPJ);
        checkFreelancer = (CheckBox) findViewById(R.id.checkFreelancer);

        //valores das CheckBox experiencia com equipamentos

        checkExpRGN = (CheckBox) findViewById(R.id.checkExpRGN);
        checkExpET = (CheckBox) findViewById(R.id.checkExpET);
        checkExpRGG = (CheckBox) findViewById(R.id.checkExpRGG);
        checkExpVantDrone = (CheckBox) findViewById(R.id.checkExpVantDrone);
        checkExpNT = (CheckBox) findViewById(R.id.checkExpNT);
        checkExpTeo = (CheckBox) findViewById(R.id.checkExpTeo);

        //valores das CheckBox mobilidade

        checkMoto = (CheckBox) findViewById(R.id.checkMoto);
        checkCarro = (CheckBox) findViewById(R.id.checkCarro);
        checkCarroOFFRoad = (CheckBox) findViewById(R.id.checkCarroOFFRoad);

        //valores das CheckBox Equipamentos

        checkReceptor = (CheckBox) findViewById(R.id.checkReceptor);
        checkReceptorGeodesico = (CheckBox) findViewById(R.id.checkReceptorGeodesico);
        checkNivel = (CheckBox) findViewById(R.id.checkNivel);
        checkEstacao = (CheckBox) findViewById(R.id.checkEstacao);
        checkDrone = (CheckBox) findViewById(R.id.checkDrone);
        checkTeodolito = (CheckBox) findViewById(R.id.checkTeodolito);

        //valores das CheckBox Experiencia em software

        checkExpSoftAC = (CheckBox) findViewById(R.id.checkExpSoftAC);
        checkExpSoftACC3D = (CheckBox) findViewById(R.id.checkExpSoftACC3D);
        checkExpSoftTEVN = (CheckBox) findViewById(R.id.checkExpSoftTEVN);
        checkExpSoftP4D = (CheckBox) findViewById(R.id.checkExpSoftP4D);
        checkExpSoftBTG = (CheckBox) findViewById(R.id.checkExpSoftBTG);
        checkExpSoftRe = (CheckBox) findViewById(R.id.checkExpSoftRe);
        checkExpSoftAG = (CheckBox) findViewById(R.id.checkExpSoftAG);
        checkExpSoftTBC = (CheckBox) findViewById(R.id.checkExpSoftTBC);
        checkExpSoftQG = (CheckBox) findViewById(R.id.checkExpSoftQG);
        checkExpSoftTT = (CheckBox) findViewById(R.id.checkExpSoftTT);
        checkExpSoftPS = (CheckBox) findViewById(R.id.checkExpSoftPS);
        checkExpSoftGM = (CheckBox) findViewById(R.id.checkExpSoftGM);

        rbDisp = (RadioButton) findViewById(R.id.rbDisp);
        rbNDisp = (RadioButton) findViewById(R.id.rbNDisp);

        cep.addTextChangedListener(new ZipCodeListener(this));

    }

    private void preencheDadosUsuario() {
        usuario = new Usuario();
        usuario.setNome(nome.getText().toString());
        usuario.setEmail(email.getText().toString());
        usuario.setTelefone(telefone.getText().toString());
        usuario.setCpf(cpf.getText().toString());
        usuario.setDataNascimento(dataNascimento.getText().toString());
        usuario.setTempodeexperiencia(experiencia.getText().toString());
        usuario.setInforAdicionais(edtCadInfor.getText().toString());
        usuario.setRua(endereco.getEndereco());
        usuario.setNumero(numero.getText().toString());
        usuario.setBairro(endereco.getBairro());
        usuario.setCidade(endereco.getCidade());
        usuario.setEstado(endereco.getEstado());
        usuario.setTipoUsuario("Comum");

        if (rbDisp.isChecked()) {
            usuario.setDispViagem("Disponível para viagem");
        } else if (rbNDisp.isChecked()) {
            usuario.setDispViagem("Não disponível para viagens");
        }

        if (checkFreelancer.isChecked()){
            listadeContratacao.add("Frelancer");
        }
        if (checkCLT.isChecked()){
            listadeContratacao.add("CLT");
        }
        if (checkPJ.isChecked()){
            listadeContratacao.add("PJ");
        }
        usuario.setTipodecontrato(listadeContratacao);

        if (checkBoxLocalizacao.isChecked()) {
            usuario.setLatitude(String.valueOf(latitude));
            usuario.setLongitude(String.valueOf(longitude));
        }
        if (checkTopo.isChecked()) {
            listaOcupacao.add("Topografo");
        }
        if (checkNivl.isChecked()) {
            listaOcupacao.add("Nivelador");
        }
        if (checkDesen.isChecked()) {
            listaOcupacao.add("Desenhista/Projestista");
        }
        if (checkAux.isChecked()) {
            listaOcupacao.add("Auxiliar");
        }
        if (checkPilot.isChecked()) {
            listaOcupacao.add("Piloto de Drone");
        }
        usuario.setOcupacao(listaOcupacao);

        if (checkReceptor.isChecked()) {
            listaEquipamento.add("Receptor GNSS de Navegação");
        }
        if (checkReceptorGeodesico.isChecked()) {
            listaEquipamento.add("Receptor GNSS Geodesico");
        }
        if (checkNivel.isChecked()) {
            listaEquipamento.add("Nível Topográfico");
        }
        if (checkEstacao.isChecked()) {
            listaEquipamento.add("Estação Total");
        }
        if (checkDrone.isChecked()) {
            listaEquipamento.add("VANT/Drone");
        }
        if (checkTeodolito.isChecked()) {
            listaEquipamento.add("Teodolito");
        }
        usuario.setEquipamentos(listaEquipamento);

        if (checkMoto.isChecked()) {
            listaMobilidade.add("Moto");
        }
        if (checkCarro.isChecked()) {
            listaMobilidade.add("Carro");
        }
        if (checkCarroOFFRoad.isChecked()) {
            listaMobilidade.add("Carro 4x4");
        }
        usuario.setVeiculo(listaMobilidade);

        if (checkExpRGN.isChecked()) {
            listaExpEquip.add("Receptor GNSS de Navegação");
        }
        if (checkExpRGG.isChecked()) {
            listaExpEquip.add("Receptor GNSS Geodesico");
        }
        if (checkExpNT.isChecked()) {
            listaExpEquip.add("Nível Topográfico");
        }
        if (checkExpET.isChecked()) {
            listaExpEquip.add("Estação Total");
        }
        if (checkExpVantDrone.isChecked()) {
            listaExpEquip.add("VANT/Drone");
        }
        if (checkExpTeo.isChecked()) {
            listaExpEquip.add("Teodolito");
        }
        usuario.setExpEquipamentos(listaExpEquip);

        if (checkExpSoftAC.isChecked()) {
            listaExpSoft.add("AutoCad");
        }
        if (checkExpSoftACC3D.isChecked()) {
            listaExpSoft.add("AutoCad Civil 3D");
        }
        if (checkExpSoftTEVN.isChecked()) {
            listaExpSoft.add("TopoEVN");
        }
        if (checkExpSoftP4D.isChecked()) {
            listaExpSoft.add("Pix4D");
        }
        if (checkExpSoftBTG.isChecked()) {
            listaExpSoft.add("Bentley TopoGRAPH");
        }
        if (checkExpSoftRe.isChecked()) {
            listaExpSoft.add("Revit");
        }
        if (checkExpSoftAG.isChecked()) {
            listaExpSoft.add("ArcGis");
        }
        if (checkExpSoftTBC.isChecked()) {
            listaExpSoft.add("Trimble Business Center");
        }
        if (checkExpSoftQG.isChecked()) {
            listaExpSoft.add("Qgis");
        }
        if (checkExpSoftTT.isChecked()) {
            listaExpSoft.add("TopconTools");
        }
        if (checkExpSoftPS.isChecked()) {
            listaExpSoft.add("PhotoSCAN");
        }
        if (checkExpSoftGM.isChecked()) {
            listaExpSoft.add("Global Mapper");
        }
        listaExpSoft.add(edtCadExpSoft.getText().toString());
        usuario.setSoftExp(listaExpSoft);

    }

    private boolean cadastrarUsuario(Usuario usuario) {
        try {
            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            String key = reference.push().getKey();
            usuario.setKeyUsuario(key);
            reference.child(key).setValue(usuario);
            Toast.makeText(FormularioActivity.this, "Cadastro completo com sucesso", Toast.LENGTH_LONG).show();
            return true;

        } catch (Exception e) {
            Toast.makeText(FormularioActivity.this, "Erro ao gravar os dados", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
    }

    private void carregaImagemPadrao(){
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

    private void cadastrarFotoUsuario() {
        StorageReference montaImagem = storageReference.child("fotoPerfilUsuario/" + emailUsuarioLogado + ".jpg");
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte [] data = byteArray.toByteArray();

        UploadTask uploadTask = montaImagem.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri dowloadUrl = taskSnapshot.getUploadSessionUri();
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
                Picasso.get().load(imagemSelecionada.toString()).resize(width, height).centerCrop().into(imageView);

            }
        }
    }

    private void limparCampos() {
        nome.setText("");
        cpf.setText("");
        dataNascimento.setText("");
        email.setText("");
        telefone.setText("");
        experiencia.setText("");
        cep.setText("");
        rua.setText("");
        numero.setText("");
        bairro.setText("");
        cidade.setText("");
        estado.setText("");
    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
                latitude =location.getLatitude();
                longitude = location.getLongitude();

    }

    public void lockFields (boolean isToLock){
        util.lockFields( isToLock );
    }

    public String getUriCEP(){
        return "https://viacep.com.br/ws/"+cep.getText()+"/json/";
    }

    public void setDataViews( Endereco endereco){
        setField(R.id.edtCadtEndereco, endereco.getEndereco());
        setField(R.id.edtCadtBairro, endereco.getBairro());
        setField(R.id.edtCadtCidade, endereco.getCidade());
        setField(R.id.edtCadtEstado, endereco.getEstado());

    }

    private void setField (int id, String data){
        ((BootstrapEditText) findViewById(id)).setText( data );
    }
}