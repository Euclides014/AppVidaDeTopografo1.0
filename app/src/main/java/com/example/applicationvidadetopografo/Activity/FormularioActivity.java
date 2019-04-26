package com.example.applicationvidadetopografo.Activity;

import android.Manifest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class FormularioActivity extends AppCompatActivity {

    private BootstrapEditText nome;
    private BootstrapEditText cpf;
    private BootstrapEditText dataNascimento;
    private BootstrapEditText email;
    private BootstrapEditText telefone;
    private BootstrapEditText cep;
    private BootstrapEditText rua;
    private BootstrapEditText numero;
    private BootstrapEditText bairro;
    private BootstrapEditText cidade;
    private BootstrapEditText estado;
    private BootstrapEditText edtCadExpSoft;
    private BootstrapEditText edtCadInfor;
    private Spinner selectEscolaridade;
    private ArrayList<String> listaOcupacao = new ArrayList<>();
    private ArrayList<String> listaEquipamento = new ArrayList<>();
    private ArrayList<String> listaMobilidade = new ArrayList<>();
    private ArrayList<String> listaExpEquip = new ArrayList<>();
    private ArrayList<String> listaExpSoft = new ArrayList<>();
    private ArrayList<String> listadeContratacao = new ArrayList<>();

    private RadioButton rbDisp, rbNDisp, locToGPS, locTOAddress;

    private CheckBox checkTopo, checkAux, checkNivl, checkDesen, checkPilot, checkMoto, checkCarro, checkCarroOFFRoad, checkReceptor, checkReceptorGeodesico;
    private CheckBox checkNivel, checkEstacao, checkDrone, checkTeodolito;
    private CheckBox checkExpRGN, checkExpET, checkExpRGG, checkExpVantDrone, checkExpNT, checkExpTeo;
    private CheckBox checkExpSoftAC, checkExpSoftACC3D, checkExpSoftTEVN, checkExpSoftP4D, checkExpSoftBTG, checkExpSoftRe, checkExpSoftAG;
    private CheckBox checkExpSoftTBC, checkExpSoftQG, checkExpSoftTT, checkExpSoftPS, checkExpSoftGM;
    private CheckBox checkCLT, checkPJ, checkFreelancer;

    private BootstrapButton btnConclui, btnCancel;

    private static final long UPDATE_INTERVAL = 10000;
    private static final long FASTEST_INTERVAL = 5000;

    private LocationRequest mLocationRequest;

    private Double latitude;
    private ImageView imageView, btnCalendar, selectDoc;
    private TextView exibirData, txt_name_arq;
    private String emailUsuarioLogado;
    private int tempoDeExp;
    private FirebaseAuth autenticacao;
    private StorageReference storageReference;
    private Double longitude;
    private DatabaseReference reference;
    private Usuario usuario;
    private Endereco endereco;
    private String endereçoToLoc;
    private Util util;
    Uri pdfURI;
    private Double endLat, endLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(null);

        startLocationUpdates();
        carregaImagemPadrao();
        recuperarvalores();

        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pegarData();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), 123);
            }
        });

        selectDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    uploadCurriculo();
                } else {
                    ActivityCompat.requestPermissions(FormularioActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        util = new Util(this,
                R.id.edtCadtCEP,
                R.id.edtCadtEndereco,
                R.id.edtCadtBairro,
                R.id.edtCadtCidade,
                R.id.edtCadtEstado);

        btnConclui = (BootstrapButton) findViewById(R.id.btnConclui);
        btnCancel = (BootstrapButton) findViewById(R.id.btnCancel);

        btnConclui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pdfURI != null) {
                    uploadfile(pdfURI);
                }
                preencheDadosUsuario();
                cadastrarFotoUsuario();
                validateFields();
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

    private void pegarData() {
        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int ano = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(FormularioActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                exibirData.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
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

    private void recuperarvalores() {

        imageView = (ImageView) findViewById(R.id.imagePerfil);
        txt_name_arq = findViewById(R.id.txt_name_arq);
        btnCalendar = (ImageView) findViewById(R.id.btnCalendar);
        selectDoc = findViewById(R.id.img_select_arq);
        exibirData = (TextView) findViewById(R.id.showDateSelect);

        nome = (BootstrapEditText) findViewById(R.id.edtCadtNome);
        cpf = (BootstrapEditText) findViewById(R.id.edtCadtCPF);
        numero = (BootstrapEditText) findViewById(R.id.edtCadtNumero);
        dataNascimento = (BootstrapEditText) findViewById(R.id.edtCadtDataNasc);
        email = (BootstrapEditText) findViewById(R.id.edtCadtEmail);
        telefone = (BootstrapEditText) findViewById(R.id.edtCadtTel);
        cep = (BootstrapEditText) findViewById(R.id.edtCadtCEP);
        rua = (BootstrapEditText) findViewById(R.id.edtCadtEndereco);
        bairro = (BootstrapEditText) findViewById(R.id.edtCadtBairro);
        cidade = (BootstrapEditText) findViewById(R.id.edtCadtCidade);
        estado = (BootstrapEditText) findViewById(R.id.edtCadtEstado);
        edtCadInfor = findViewById(R.id.edtCadInfor);
        edtCadExpSoft = (BootstrapEditText) findViewById(R.id.edtCadExpSoft);
        selectEscolaridade = findViewById(R.id.selecEscolaridade);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.bootstrap_dropdown_example_data,
                        android.R.layout.simple_spinner_dropdown_item);
        selectEscolaridade.setAdapter(adapter);

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
        locTOAddress = (RadioButton) findViewById(R.id.locToAddress);
        locToGPS = (RadioButton) findViewById(R.id.locToGPS);

        cep.addTextChangedListener(new ZipCodeListener(this));

    }

    private void preencheDadosUsuario() {
        usuario = new Usuario();
        usuario.setNome(nome.getText().toString());
        usuario.setEmail(email.getText().toString());
        usuario.setTelefone(telefone.getText().toString());
        usuario.setCpf(cpf.getText().toString());
        usuario.setDataNascimento(dataNascimento.getText().toString());
        usuario.setTempodeexperiencia((tempoDeExp + "anos"));
        if (edtCadInfor.getText() == null) {
            edtCadInfor.setText("");
        }
        usuario.setInforAdicionais(edtCadInfor.getText().toString());
        usuario.setRua(rua.getText().toString());
        usuario.setNumero(numero.getText().toString());
        usuario.setBairro(bairro.getText().toString());
        usuario.setCidade(cidade.getText().toString());
        usuario.setEstado(estado.getText().toString());
        usuario.setTipoUsuario("Comum");
        selectEscolaridade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                usuario.setFormacao(parent.getOnItemClickListener().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (rbDisp.isChecked()) {
            usuario.setDispViagem("Disponível para viagem");
        } else if (rbNDisp.isChecked()) {
            usuario.setDispViagem("Não disponível para viagens");
        }

        if (checkFreelancer.isChecked()) {
            listadeContratacao.add("Frelancer");
        }
        if (checkCLT.isChecked()) {
            listadeContratacao.add("CLT");
        }
        if (checkPJ.isChecked()) {
            listadeContratacao.add("PJ");
        }
        usuario.setTipodecontrato(listadeContratacao);

        if (locToGPS.isChecked()) {
            usuario.setLatitude(String.valueOf(latitude));
            usuario.setLongitude(String.valueOf(longitude));
        } else if (locTOAddress.isChecked()) {
            toLocation();
            usuario.setLatitude(String.valueOf(endLat));
            usuario.setLongitude(String.valueOf(endLong));
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

    private void cadastrarFotoUsuario() {
        StorageReference montaImagem = storageReference.child("fotoPerfilUsuario/" + emailUsuarioLogado + ".jpg");
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        Bitmap bitmap = imageView.getDrawingCache();
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
                Picasso.get().load(imagemSelecionada.toString()).resize(width, height).centerCrop().into(imageView);

            }
        }
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfURI = data.getData();
            txt_name_arq.setText(data.getData().getLastPathSegment());
        } else {
            Toast.makeText(FormularioActivity.this, "Selecione o arquivo", Toast.LENGTH_LONG).show();
        }
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
        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }

    public void lockFields(boolean isToLock) {
        util.lockFields(isToLock);
    }

    public String getUriCEP() {
        return "https://viacep.com.br/ws/" + cep.getText() + "/json/";
    }

    public void setDataViews(Endereco endereco) {
        setField(R.id.edtCadtEndereco, endereco.getEndereco());
        setField(R.id.edtCadtBairro, endereco.getBairro());
        setField(R.id.edtCadtCidade, endereco.getCidade());
        setField(R.id.edtCadtEstado, endereco.getEstado());

    }

    private void setField(int id, String data) {
        ((BootstrapEditText) findViewById(id)).setText(data);
    }

    private void toLocation() {
        endereçoToLoc = (cep.getText().toString());
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(endereçoToLoc, 1);
            Address address = addresses.get(0);
            endLat = address.getLatitude();
            endLong = address.getLongitude();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadCurriculo() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            uploadCurriculo();
        } else {
            Toast.makeText(FormularioActivity.this, "É necessário permitir que o aplicativo acesse o armazenamento interno", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadfile(Uri pdfURI) {
        String fileName = System.currentTimeMillis() + "";
        StorageReference uploadCurrículo = storageReference.child("curriculo_Usuario/" + fileName + emailUsuarioLogado);
        uploadCurrículo.putFile(pdfURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String url = taskSnapshot.getUploadSessionUri().toString();
                usuario.setUrlCurriculo(url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FormularioActivity.this, "Falha ao envia o arquivo", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

            }
        });
    }

    private void validateFields() {

        boolean res = false;

        String nameValidation = nome.getText().toString();
        String emailValidation = email.getText().toString();
        String cpfValidation = cpf.getText().toString();
        String dateBornValidation = dataNascimento.getText().toString();
        String numberPhone = telefone.getText().toString();
        String cepValidation = cep.getText().toString();

        boolean rbLocValidation = locTOAddress.isSelected() || locToGPS.isSelected();

        boolean checkOcuValidation = checkTopo.isSelected();


        if (res = isFieldsNull(nameValidation)) {
            nome.requestFocus();
        } else if (res = !isEmailValid(emailValidation)) {
            email.requestFocus();
        } else if (res = isFieldsNull(cpfValidation)) {
            cpf.requestFocus();
        } else if (res = isFieldsNull(dateBornValidation)) {
            dataNascimento.requestFocus();
        } else if (res = isFieldsNull(numberPhone)) {
            telefone.requestFocus();
        } else if (res = isFieldsNull(cepValidation)) {
            cep.requestFocus();
        } else if (rbLocValidation == false){
            locTOAddress.requestFocus();
        } else if (checkOcuValidation == false){
            checkTopo.requestFocus();
        }

        if (res){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setIcon(R.drawable.ic_warning_yellow_24dp);
            dlg.setTitle("Atenção");
            dlg.setMessage("Há campos inválidos ou em branco!");
            dlg.setNeutralButton("OK", null);
            dlg.show();
        } else {
            cadastrarUsuario(usuario);
            Intent intent = new Intent(FormularioActivity.this, TelaMapaActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean isFieldsNull(String value) {

        boolean result = (TextUtils.isEmpty(value) || value.trim().isEmpty());
        return result;
    }

    private boolean isEmailValid(String emailValid) {
        boolean result = (!isFieldsNull(emailValid) && Patterns.EMAIL_ADDRESS.matcher(emailValid).matches());
        return result;
    }


}