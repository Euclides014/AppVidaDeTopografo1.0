package com.example.applicationvidadetopografo.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.applicationvidadetopografo.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        recuperarValores();
    }

    private void recuperarValores() {

        iv_edit_imagePerfil = findViewById(R.id.iv_edit_imagePerfil);
        btn_edit_Calendar = findViewById(R.id.btn_edit_Calendar);

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
        btn_edit_Cancelar =  findViewById(R.id.btn_edit_Cancelar);

    }
}
