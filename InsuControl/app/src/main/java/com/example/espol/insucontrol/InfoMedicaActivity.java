package com.example.espol.insucontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InfoMedicaActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_medica);


        btnGuardar = (Button)findViewById(R.id.btnSave);
        btnGuardar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(btnGuardar==view){
            Intent intent = new Intent(InfoMedicaActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
