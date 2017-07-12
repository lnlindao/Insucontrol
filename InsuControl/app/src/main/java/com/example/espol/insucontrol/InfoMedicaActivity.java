package com.example.espol.insucontrol;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.espol.insucontrol.Conexion.Constantes;
import com.example.espol.insucontrol.Conexion.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InfoMedicaActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txtPeso;
    EditText txtTalla;
    EditText txtGlucosaMin;
    EditText txtGlucosaMax;
    EditText txtRatio;
    EditText txtSens;
    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_medica);

        txtPeso = (EditText) findViewById(R.id.txtPeso);
        txtTalla = (EditText) findViewById(R.id.txtTalla);
        txtGlucosaMin = (EditText) findViewById(R.id.txtMin);
        txtGlucosaMax = (EditText) findViewById(R.id.txtMax);
        txtRatio = (EditText) findViewById(R.id.txtRatio);
        txtSens = (EditText) findViewById(R.id.txtSens);

        btnGuardar = (Button)findViewById(R.id.btnSave);
        btnGuardar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(btnGuardar==view){
            String peso = txtPeso.getText().toString();
            String talla = txtTalla.getText().toString();
            String min = txtGlucosaMin.getText().toString();
            String max = txtGlucosaMax.getText().toString();
            String ratio = txtRatio.getText().toString();
            String sens = txtSens.getText().toString();

            String id = txtPeso.getText().toString();

//            insertarRegistro(peso, talla, min, max, ratio, sens, id);

            Intent intent = new Intent(InfoMedicaActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }


    public void insertarRegistro(String peso,String talla, String min,String max, String ratio, String sens, String id){

        HashMap<String, String> map = new HashMap<>();
        map.put("peso",peso);
        map.put("talla",talla);
        map.put("obj_gs_min",min);
        map.put("obj_gs_max",max);
        map.put("ratio_ch",ratio);
        map.put("sensibilidad_ins",sens);
        map.put("id_usuario",id);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERT_INFOMEDICA,
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
                    Intent intent = new Intent(InfoMedicaActivity.this, HomeActivity.class);
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
