package com.whatjaspionfinal.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.whatjaspionfinal.R;
import com.whatjaspionfinal.config.ConfiguracaoFirebase;
import com.whatjaspionfinal.helper.Base64Custon;
import com.whatjaspionfinal.helper.Permissao;
import com.whatjaspionfinal.helper.UsuarioFireBase;
import com.whatjaspionfinal.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {


    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton imageButtonCamera, imageButtonGaleria;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private CircleImageView circleImageViewPerfil;
    private StorageReference storageReference;
    private String identificadorUsuario;
    private EditText nomeUsuarioPerfil;
    private ImageView atualizarNomeUsuario;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        imageButtonCamera = findViewById(R.id.ibCamera);
        imageButtonGaleria = findViewById(R.id.ibGaleria);
        circleImageViewPerfil = findViewById(R.id.circleImageViewFotoPerfil);
        nomeUsuarioPerfil = findViewById(R.id.etNomeUsuario);
        atualizarNomeUsuario = findViewById(R.id.iAtualizarNome);

        storageReference = ConfiguracaoFirebase.getFireBaseStorage();
        identificadorUsuario = UsuarioFireBase.getIdentificadorUsuario();

        usuarioLogado = UsuarioFireBase.getDadosUsuarioLogado();

        //validar permissoes
        Permissao.validarPermissoes(permissoesNecessarias, this,1);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recuperar Dados do Usuario
        FirebaseUser usuario = UsuarioFireBase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        if (url != null){
            Glide.with(ConfiguracoesActivity.this)
                    .load(url)
                    .into(circleImageViewPerfil);
        }else{
            circleImageViewPerfil.setImageResource(R.drawable.padrao);
        }

        nomeUsuarioPerfil.setText(usuario.getDisplayName());



        //onClickCamera
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_CAMERA);
                }

            }
        });

        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        atualizarNomeUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nome = nomeUsuarioPerfil.getText().toString();
                boolean retorno = UsuarioFireBase.atualizarNomeUsuario(nome);

                if (retorno){
                    usuarioLogado.setNome(nome);
                    usuarioLogado.atualizar();

                    Toast.makeText(ConfiguracoesActivity.this,
                            "Nome alterado com sucesso",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){

                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }
                if (imagem != null){
                    circleImageViewPerfil.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);

                    byte[] dadosImagem = baos.toByteArray();

                    //salvar imagem no firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(identificadorUsuario + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfiguracoesActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            Task<Uri> uriTask = task.getResult().getStorage().getDownloadUrl();
                            while(!uriTask.isComplete());
                            Uri downloadUrl = uriTask.getResult();
                            atualizaFotoUsuario(downloadUrl);
                        }
                    });
                }

            }catch (Exception e ){
                e.printStackTrace();
            }
        }
    }

    public void atualizaFotoUsuario(Uri url){
        boolean retorno = UsuarioFireBase.atualizarFotoUsuario(url);

        if (retorno){
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();

            Toast.makeText(ConfiguracoesActivity.this,
                    "Sucesso ao atualizar imagem",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
