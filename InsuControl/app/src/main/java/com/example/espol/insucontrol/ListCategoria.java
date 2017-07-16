package com.example.espol.insucontrol;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.List;

public class ListCategoria extends AppCompatActivity implements AdapterView.OnItemClickListener {


    List<String> categoriaList = new ArrayList<>();
    List<String> idList = new ArrayList<>();
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categoria);

        listview = (ListView)findViewById(android.R.id.list);
        poblarList();


        /*BARRA CON ÍCONO*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(R.string.tituloCategorias);
    }


    public void poblarList(){
        //GET METHOD
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue
                (new JsonObjectRequest(Request.Method.POST, Constantes.GET_CATEGORIA, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                procesarRespuesta(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Mensaje de Respuesta
                        Toast.makeText(ListCategoria.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ));
    }

    public void procesarRespuesta(JSONObject response){
        try{
            //Obtener atributo estado
            String estado = response.getString(Constantes.ESTADO);
            switch (estado){
                case Constantes.SUCCESS:
                    JSONArray retorno = response.getJSONArray("categoria");
                    //Iniciar Adaptador
                    for(int i=0;i<retorno.length();i++)
                    {
                        JSONObject jb1 = retorno.getJSONObject(i);
                        categoriaList.add(jb1.getString("nombre"));
                    }

                    for(int i=0;i<retorno.length();i++)
                    {
                        JSONObject jb1 = retorno.getJSONObject(i);
                        idList.add(jb1.getString("id_categoria"));
                    }

                    ListViewAdapter adapter = new ListViewAdapter(getBaseContext(), categoriaList);
                    //Setear adaptador a la lista
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
        String id = idList.get(i);

        Intent intent = new Intent(ListCategoria.this,ListAlimentos.class);
        intent.putExtra(Constantes.ID, id);
        startActivity(intent);

    }
}
