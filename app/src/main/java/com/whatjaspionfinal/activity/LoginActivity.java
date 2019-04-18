package com.whatjaspionfinal.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.whatjaspionfinal.R;
import com.whatjaspionfinal.config.ConfiguracaoFirebase;
import com.whatjaspionfinal.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);

    }

    public void logarUsuario(Usuario usuario){
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if( task.isSuccessful() ){
                    abrirTelaPrincipal();
                }else{

                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e){
                        excecao = "Usuário não está cadastrado!";
                    }catch ( FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch ( Exception e){
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                    }
                    Toast.makeText(
                            LoginActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT
                            ).show();
                }

            }
        });
    }

    public void validarAutenticacaoUsuario(View view){

        String txtEmail = campoEmail.getText().toString();
        String txtSenha = campoSenha.getText().toString();

        if( !txtEmail.isEmpty() ){
            if( !txtSenha.isEmpty() ){

                Usuario usuario = new Usuario();

                usuario.setEmail( txtEmail);
                usuario.setSenha( txtSenha );

                logarUsuario( usuario );
            }else{
                Toast.makeText(this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Preencha o email!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = autenticacao. getCurrentUser();
        if( usuarioAtual != null ){
            abrirTelaPrincipal();
        }
    }

    public void abrirTelaCadastro(View view){
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }

    public void abrirTelaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
