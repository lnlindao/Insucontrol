package com.example.espol.insucontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.espol.insucontrol.DataBase.DatabaseHelper;
import com.example.espol.insucontrol.DataBase.Provider;

import java.util.List;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private DatabaseHelper handler;
    Button btnGuardar;
    Spinner spnProvincia;
    Spinner spnCiudad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        spnProvincia = (Spinner)findViewById(R.id.spnProvincia);
        spnCiudad = (Spinner)findViewById(R.id.spnCiudad);
        btnGuardar = (Button)findViewById(R.id.btnSig);
        btnGuardar.setOnClickListener(this);
        poblarProvincia();
    }

    @Override
    public void onClick(View view) {

        if(btnGuardar==view){
            Intent intent = new Intent(RegistroActivity.this, InfoMedicaActivity.class);
            startActivity(intent);
        }
    }


    public void poblarProvincia(){
        handler = new DatabaseHelper(this, Provider.DATABASE_NAME ,null,1);

        List<String> provincia = handler.getProvincia();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, provincia);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProvincia.setAdapter(dataAdapter);
        spnProvincia.setOnItemSelectedListener(this);



    }

    public String idProvincia(String prov){
        handler = new DatabaseHelper(this, Provider.DATABASE_NAME ,null,1);
        String id = handler.getIdProvincia(prov);
        return id;
    }

    public void poblarCiudad(String id){
        handler = new DatabaseHelper(this, Provider.DATABASE_NAME ,null,1);

        List<String> provincia = handler.getCiudades(id);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, provincia);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCiudad.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String prov = spnProvincia.getSelectedItem().toString();
        String id = idProvincia(prov);
        poblarCiudad(id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
