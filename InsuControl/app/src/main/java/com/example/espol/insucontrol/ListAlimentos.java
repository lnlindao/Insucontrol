package com.example.espol.insucontrol;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.espol.insucontrol.Conexion.Constantes;
import com.example.espol.insucontrol.Conexion.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAlimentos extends AppCompatActivity implements AdapterView.OnItemClickListener {

    List<String> alimentosList = new ArrayList<>();
    ListView listview;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_alimentos);

        id = getIntent().getExtras().getString(Constantes.ID);

        Toast.makeText(getApplicationContext(),id,Toast.LENGTH_SHORT).show();

        listview = (ListView)findViewById(android.R.id.list);
        poblarList(id);

        /*BARRA CON ÍCONO*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(R.string.tituloListaAlimentos);
    }


    public void poblarList(String id_categoria){

        HashMap<String, String> map = new HashMap<>();
        map.put("id_categoria", id_categoria);

        JSONObject jobject = new JSONObject(map);
        Log.d("TAG", jobject.toString());

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.GET_ALIMENTOS,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuesta(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ListAlimentos.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

    public void procesarRespuesta(JSONObject response){
        try{
            //Obtener atributo estado
            String estado = response.getString(Constantes.ESTADO);
            switch (estado){
                case Constantes.SUCCESS:
                    JSONArray retorno = response.getJSONArray("carbohidratos");
                    //Iniciar Adaptador
                    for(int i=0;i<retorno.length();i++)
                    {
                        JSONObject jb1 = retorno.getJSONObject(i);
                        alimentosList.add(jb1.getString("nombre"));
                    }

                    ListViewAdapter adapter = new ListViewAdapter(getBaseContext(), alimentosList);
                    listview.setAdapter(adapter);
                    listview.setOnItemClickListener(this);

                    break;
                case Constantes.FAILED:
                    String mensaje = response.getString(Constantes.MENSAJE);
                    Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_SHORT).show();
                    break;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        alertDialog();
    }

    public void alertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog, null);
        //Title
        builder.setIcon(R.mipmap.ic_alimentos);
        builder.setTitle(R.string.infoAlimentos);
        builder.setView(LayoutInflater.from(this).inflate(R.layout.alertdialog,null));

        //Traer Views
//        txtusuario = (EditText)dialogView.findViewById(R.id.txtOperador);
        builder.setPositiveButton("Guardar",null);
        builder.setView(dialogView);
        AlertDialog ad = builder.create();
        ad.show();
    }
}
