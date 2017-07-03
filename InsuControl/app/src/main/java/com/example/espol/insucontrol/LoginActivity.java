package com.example.espol.insucontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.espol.insucontrol.Conexion.Constantes;
import com.example.espol.insucontrol.Sync.SyncAdapter;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnComenzar;
    Button btnRegistro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnComenzar = (Button)findViewById(R.id.btnLogin);
        btnComenzar.setOnClickListener(this);

        btnRegistro = (Button)findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(this);


        try{
            SyncAdapter.sincronizarAhora(getApplicationContext(), false, Constantes.bajar_data, null, null);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {


        if(btnRegistro==view){
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        }
    }
}
