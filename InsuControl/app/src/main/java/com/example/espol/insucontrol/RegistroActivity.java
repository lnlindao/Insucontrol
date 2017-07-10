package com.example.espol.insucontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.espol.insucontrol.Conexion.Constantes;
import com.example.espol.insucontrol.Conexion.VolleySingleton;
import com.example.espol.insucontrol.DataBase.DatabaseHelper;
import com.example.espol.insucontrol.DataBase.Provider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private DatabaseHelper handler;

    EditText txtNombre;
    EditText txtApellido;
    EditText txtCorreo;
    EditText txtClave;
    EditText txtPaciente;
    EditText txtFecha;

    RadioButton r1;
    RadioButton r2;

    Spinner spnProvincia;
    Spinner spnCiudad;
    Button btnGuardar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtNombre = (EditText) findViewById(R.id.txtUser);
        txtApellido = (EditText) findViewById(R.id.txtApellidoPaciente);
        txtCorreo = (EditText) findViewById(R.id.txtCorreo);
        txtClave = (EditText) findViewById(R.id.txtPass);
        txtPaciente = (EditText) findViewById(R.id.txtUserPaciente);
        txtFecha = (EditText) findViewById(R.id.txtFecha);

        r1 = (RadioButton) findViewById(R.id.rM);
        r2 = (RadioButton) findViewById(R.id.rF);

        spnProvincia = (Spinner)findViewById(R.id.spnProvincia);
        spnCiudad = (Spinner)findViewById(R.id.spnCiudad);
        btnGuardar = (Button)findViewById(R.id.btnSig);
        btnGuardar.setOnClickListener(this);
        poblarProvincia();
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

    public String idCiudad(String ciudad){
        handler = new DatabaseHelper(this, Provider.DATABASE_NAME ,null,1);
        String id = handler.getIdCiudad(ciudad);
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
    public void onNothingSelected(AdapterView<?> adapterView) { }


    @Override
    public void onClick(View view) {

        if(btnGuardar==view){
            String nombre = txtNombre.getText().toString();
            String apellido = txtApellido.getText().toString();
            String correo = txtCorreo.getText().toString();
            String clave = txtClave.getText().toString();
            String paciente = txtPaciente.getText().toString();
            String fecha = txtFecha.getText().toString();
            String genero ="";
            if(r1.isChecked()==true){
                genero = getString(R.string.m);
            }else if(r2.isChecked()==true){
                genero = getString(R.string.f);
            }

            String ciudad = spnCiudad.getSelectedItem().toString();
            String id_ciudad = idCiudad(ciudad);
//            insertarRegistro(nombre,apellido,correo,genero,clave,paciente,fecha,id_ciudad);

            Intent intent = new Intent(RegistroActivity.this, InfoMedicaActivity.class);
            startActivity(intent);
        }
    }


    public void insertarRegistro(String nombre,String apellido, String correo,String genero, String clave, String paciente, String fecha, String id_ciudad){

        HashMap<String, String> map = new HashMap<>();
        map.put("nombre",nombre);
        map.put("apellido",apellido);
        map.put("correo",correo);
        map.put("sexo",genero);
        map.put("fecha_nacimiento",fecha);
        map.put("clave",clave);
        map.put("id_ciudad",id_ciudad);
        map.put("nombre_representante",paciente);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERT_REGISTRO,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                procesarRespuestaInsertRegistro(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Registro", "Error Volley al Insertar Registro : " + error.getMessage());
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );

    }

    /**
     * Procesa la respuesta obtenida desde el sevidor
     *
     * response Objeto Json
     */
    private void procesarRespuestaInsertRegistro(JSONObject response) {
        try {
            // Obtener estado
            String estado = response.getString(Constantes.ESTADO);
            // Obtener mensaje
            String mensaje = response.getString(Constantes.MENSAJE);

            switch (estado) {
                case Constantes.SUCCESS:

                    String lastid = response.getString(Constantes.ID);
                    // Mostrar mensaje
                    Toast.makeText(getApplicationContext(),mensaje + lastid,Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegistroActivity.this, InfoMedicaActivity.class);
                    intent.putExtra(Constantes.ID,lastid);
                    startActivity(intent);
                    break;

                case Constantes.FAILED:
                    // Mostrar mensaje
                    Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
