package com.example.espol.insucontrol;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    CardView card_gs, card_ch;

    FloatingActionButton listCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listCat = (FloatingActionButton) findViewById(R.id.verListaCh);
        listCat.setOnClickListener(this);


        /*BARRA CON ÍCONO*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_logo_blan);
    }

    @Override
    public void onClick(View view) {
        if(view==listCat){
            Intent intent = new Intent(HomeActivity.this,ListCategoria.class);
            startActivity(intent);

        }
    }
}
