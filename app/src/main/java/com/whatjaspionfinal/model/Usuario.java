package com.whatjaspionfinal.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.whatjaspionfinal.activity.ConfiguracoesActivity;
import com.whatjaspionfinal.config.ConfiguracaoFirebase;
import com.whatjaspionfinal.helper.UsuarioFireBase;

import java.util.HashMap;
import java.util.Map;

public class Usuario {



    private String id_usuario;
    private String nome;
    private String email;
    private String senha;
    private String foto;

    public Usuario() {
    }

    public Usuario(String id_usuario, String nome, String email, String senha, String foto) {
        this.id_usuario = id_usuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.foto = foto;
    }

    public void salvar(){
        DatabaseReference fireBaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = fireBaseRef.child("usuarios").child(getId_usuario());

        usuario.setValue( this );
    }

    public void atualizar(){
        String identificadorUsuario = UsuarioFireBase.getIdentificadorUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuarioRef = database.child("usuarios")
                .child(identificadorUsuario);

        Map<String,Object> valoresUsuario = converterParaMap();

        usuarioRef.updateChildren(valoresUsuario);
    }

    @Exclude
    public Map<String,Object> converterParaMap(){
        HashMap<String,Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email",getEmail());
        usuarioMap.put("nome",getNome());
        usuarioMap.put("foto",getFoto());
        return usuarioMap;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
