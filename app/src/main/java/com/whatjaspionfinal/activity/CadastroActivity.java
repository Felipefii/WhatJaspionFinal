package com.whatjaspionfinal.activity;

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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.whatjaspionfinal.R;
import com.whatjaspionfinal.config.ConfiguracaoFirebase;
import com.whatjaspionfinal.helper.Base64Custon;
import com.whatjaspionfinal.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
    }

    public void salvarUsuarioFirebase(final Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
            usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if( task.isSuccessful() ){
                    Toast.makeText(CadastroActivity.this,"Sucesso ao cadastrar usuario", Toast.LENGTH_SHORT).show();
                    finish();

                    try{

                        String identificadorUsuario = Base64Custon.codificarBase64(usuario.getEmail());
                        usuario.setId_usuario(identificadorUsuario);
                        usuario.salvar();


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    String excecao = "";
                    try{
                        throw task.getException();
                    } catch ( FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte!";
                    } catch ( FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Digite um email valido!";
                    } catch ( FirebaseAuthUserCollisionException e) {
                        excecao = "Esta conta já foi cadastrada!";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void validarCadastroUsuario(View view){
        String txtNome = campoNome.getText().toString();
        String txtEmail = campoEmail.getText().toString();
        String txtSenha = campoSenha.getText().toString();

        if( !txtNome.isEmpty() ){
            if( !txtEmail.isEmpty() ){
                if( !txtSenha.isEmpty() ){
                    Usuario usuario = new Usuario();
                    usuario.setNome( txtNome );
                    usuario.setEmail( txtEmail );
                    usuario.setSenha( txtSenha );

                    salvarUsuarioFirebase(usuario);
                }else{
                    Toast.makeText(this, "Preencha o senha!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
        }
    }
}
