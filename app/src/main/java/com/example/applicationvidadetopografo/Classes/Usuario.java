package com.example.applicationvidadetopografo.Classes;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Usuario {


    private String nome;
    private String cpf;
    private String dataNascimento;
    private String formacao;
    private String tempodeexperiencia;
    private ArrayList<String> tipodecontrato;
    private ArrayList<String> equipamentos;
    private ArrayList<String> veiculo;
    private ArrayList<String> ocupacao;
    private ArrayList<String> expEquipamentos;
    private ArrayList<String> softExp;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String email;
    private String telefone;
    private String site;
    private String senha;
    private String tipoUsuario;
    private String keyUsuario;
    private String latitude;
    private String longitude;
    private String dispViagem;
    private String inforAdicionais;
    private String urlPerfil;
    private String urlCurriculo;


    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getFormacao() {
        return formacao;
    }

    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }

    public String getTempodeexperiencia() {
        return tempodeexperiencia;
    }

    public void setTempodeexperiencia(String tempodeexperiencia) {
        this.tempodeexperiencia = tempodeexperiencia;
    }

    public ArrayList<String> getEquipamentos() {
        return equipamentos;
    }

    public void setEquipamentos(ArrayList<String> equipamentos) {
        this.equipamentos = equipamentos;
    }

    public ArrayList<String> getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(ArrayList<String> veiculo) {
        this.veiculo = veiculo;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    @Exclude
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getKeyUsuario() {
        return keyUsuario;
    }

    public void setKeyUsuario(String keyUsuario) {
        this.keyUsuario = keyUsuario;
    }

    public ArrayList<String> getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(ArrayList<String> ocupacao) {
        this.ocupacao = ocupacao;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public ArrayList<String> getExpEquipamentos() {
        return expEquipamentos;
    }

    public void setExpEquipamentos(ArrayList<String> expEquipamentos) {
        this.expEquipamentos = expEquipamentos;
    }

    public ArrayList<String> getSoftExp() {
        return softExp;
    }

    public void setSoftExp(ArrayList<String> softExp) {
        this.softExp = softExp;
    }

    public String getDispViagem() {
        return dispViagem;
    }

    public void setDispViagem(String dispViagem) {
        this.dispViagem = dispViagem;
    }

    public String getInforAdicionais() {
        return inforAdicionais;
    }

    public void setInforAdicionais(String inforAdicionais) {
        this.inforAdicionais = inforAdicionais;
    }

    public ArrayList<String> getTipodecontrato() {
        return tipodecontrato;
    }

    public void setTipodecontrato(ArrayList<String> tipodecontrato) {
        this.tipodecontrato = tipodecontrato;
    }

    public String getUrlPerfil() {
        return urlPerfil;
    }

    public void setUrlPerfil(String urlPerfil) {
        this.urlPerfil = urlPerfil;
    }

    public String getUrlCurriculo() {
        return urlCurriculo;
    }

    public void setUrlCurriculo(String urlCurriculo) {
        this.urlCurriculo = urlCurriculo;
    }
}