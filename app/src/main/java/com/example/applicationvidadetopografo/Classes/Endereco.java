package com.example.applicationvidadetopografo.Classes;

public class Endereco {

    private  String cep;
    private  String logradouro;
    private  String bairro;
    private  String localidade;
    private  String uf;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return logradouro;
    }

    public void setEndereco(String endereco) {
        this.logradouro = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return localidade;
    }

    public void setCidade(String cidade) {
        this.localidade = cidade;
    }

    public String getEstado() {
        return uf;
    }

    public void setEstado(String estado) {
        this.uf = estado;
    }
}
