package com.whatjaspionfinal.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.whatjaspionfinal.R;
import com.whatjaspionfinal.adapter.ContatosAdapter;
import com.whatjaspionfinal.config.ConfiguracaoFirebase;
import com.whatjaspionfinal.fragment.ContatosFragment;
import com.whatjaspionfinal.fragment.ConversasFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WhatJaspion");
        setSupportActionBar( toolbar );

        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                .add("Conversas", ConversasFragment.class)
                .add("Contatos", ContatosFragment.class)
                .create()
        );
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

        //Config SearchView
        searchView = findViewById(R.id.materialSearchPrincipal);

        //listener para o searchView
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                ConversasFragment fragmentConversas = (ConversasFragment) adapter.getPage(0);
                ContatosFragment fragmentContatos = (ContatosFragment) adapter.getPage(1);
                fragmentConversas.recarregarConversas();
                fragmentContatos.recarregarContatos();
            }
        });

        //listener para a caixa de texto
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("evento","onQuerySubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Log.d("evento","onQueryChange");

                ConversasFragment fragmentConversas = (ConversasFragment) adapter.getPage(0);
                ContatosFragment fragmentContatos = (ContatosFragment) adapter.getPage(1);

                if (newText != null && !newText.isEmpty()){
                    fragmentConversas.pesquisarConversas(newText.toLowerCase());
                    fragmentContatos.pesquisarContatos(newText.toLowerCase());
                }else if (newText.equals("")){
                    fragmentConversas.recarregarConversas();
                    fragmentContatos.recarregarContatos();
                }


                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //configurar botão de pesquisa

        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                finish();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario(){
        try {
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void abrirConfiguracoes(){
        Intent intent = new Intent(MainActivity.this, ConfiguracoesActivity.class);
        startActivity(intent);
    }
}
