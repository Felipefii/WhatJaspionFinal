package com.whatjaspionfinal.model;

import com.google.firebase.database.DatabaseReference;
import com.whatjaspionfinal.config.ConfiguracaoFirebase;

public class Usuario {



    private String id_usuario;
    private String nome;
    private String email;
    private String senha;

    public Usuario() {
    }

    public Usuario(String id_usuario, String nome, String email, String senha) {
        this.id_usuario = id_usuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public void salvar(){
        DatabaseReference fireBaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = fireBaseRef.child("usuarios").child(getId_usuario());

        usuario.setValue( this );
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }
}
