package com.whatjaspionfinal.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.whatjaspionfinal.R;
import com.whatjaspionfinal.helper.UsuarioFireBase;
import com.whatjaspionfinal.model.Mensagem;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public MensagensAdapter(List<Mensagem> lista, Context c) {
        this.mensagens = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View item =  null;
        if (i == TIPO_REMETENTE){

            item = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_mensagem_remetente, viewGroup, false);

        }else if(i == TIPO_DESTINATARIO) {
            item = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.adapter_mensagem_destinatario, viewGroup, false);

        }

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Mensagem mensagem = mensagens.get( i );
        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImagem();

        if ( imagem != null ){
            Uri url = Uri.parse( imagem );
            Glide.with(context).load(url).into( myViewHolder.imagem );

            //Esconder o texto
            myViewHolder.mensagem.setVisibility(View.GONE);

        }else {
            myViewHolder.mensagem.setText( msg );

            //Esconder a imagem
            myViewHolder.imagem.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {

        Mensagem mensagem = mensagens.get( position );

        String idUsuario = UsuarioFireBase.getIdentificadorUsuario();

        if ( idUsuario.equals( mensagem.getIdUsuario() ) ){
            return TIPO_REMETENTE;
        }

        return TIPO_DESTINATARIO;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mensagem;
        ImageView imagem;

        public MyViewHolder(View itemView) {
            super(itemView);

            mensagem = itemView.findViewById(R.id.textMensagemTexto);
            imagem   = itemView.findViewById(R.id.imageMensagemFoto);

        }
    }

}
