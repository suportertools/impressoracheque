/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Claudemir Rtools
 */
@XmlRootElement
public class WSImpressoraCheque {

    private int id;
    private String apelido;
    private Boolean ativo;
    private String banco;
    private String valor;
    private String favorecido;
    private String cidade;
    private String data;
    private String mensagem;

    public WSImpressoraCheque() {
        this.id = -1;
        this.apelido = "";
        this.ativo = false;
        this.banco = "";
        this.valor = "";
        this.favorecido = "";
        this.cidade = "";
        this.data = "";
        this.mensagem = "";
    }
    
    public WSImpressoraCheque(int id, String apelido, Boolean ativo, String banco, String valor, String favorecido, String cidade, String data, String mensagem) {
        this.id = id;
        this.apelido = apelido;
        this.ativo = ativo;
        this.banco = banco;
        this.valor = valor;
        this.favorecido = favorecido;
        this.cidade = cidade;
        this.data = data;
        this.mensagem = mensagem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getFavorecido() {
        return favorecido;
    }

    public void setFavorecido(String favorecido) {
        this.favorecido = favorecido;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
