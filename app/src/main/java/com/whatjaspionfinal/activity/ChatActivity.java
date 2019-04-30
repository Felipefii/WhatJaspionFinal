package com.whatjaspionfinal.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.whatjaspionfinal.R;
import com.whatjaspionfinal.config.ConfiguracaoFirebase;
import com.whatjaspionfinal.helper.Base64Custon;
import com.whatjaspionfinal.helper.UsuarioFireBase;
import com.whatjaspionfinal.model.Mensagem;
import com.whatjaspionfinal.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewNome;
    private CircleImageView circleImageViewFoto;
    private Usuario usuarioDestinatario;
    private EditText editMensagem;

    //identificador usuario remetente e destinatario
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageViewFotoChat);
        editMensagem = findViewById(R.id.editMensagem);

        idUsuarioRemetente = UsuarioFireBase.getIdentificadorUsuario();


        //Recuperar dados do usuário destiatario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            usuarioDestinatario = (Usuario) bundle.getSerializable("chatContatos");
            textViewNome.setText(usuarioDestinatario.getNome());

            String foto = usuarioDestinatario.getFoto();
            if (foto != null){
                Uri url = Uri.parse(usuarioDestinatario.getFoto());
                Glide.with(ChatActivity.this)
                .load(url)
                .into(circleImageViewFoto);
            }else{
                circleImageViewFoto.setImageResource(R.drawable.padrao);
            }

            idUsuarioDestinatario = Base64Custon.codificarBase64(usuarioDestinatario.getEmail());
        }
    }

    public void enviarMensagem(View view){
        String textoMensagem = editMensagem.getText().toString();
        if(!textoMensagem.isEmpty()){
            Mensagem mensagem = new Mensagem();
            mensagem.setIdUsuario(idUsuarioRemetente);
            mensagem.setMensagem(textoMensagem);

            //Salvar Mensagem
            salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario,mensagem);

        }else{
            Toast.makeText(ChatActivity.this,
                    "Digite uma mensagem para enviar",
                    Toast.LENGTH_SHORT
                    ).show();
        }
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");

        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(mensagem);

        editMensagem.setText("");
    }

}
