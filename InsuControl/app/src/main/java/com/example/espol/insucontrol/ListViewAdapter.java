package com.example.espol.insucontrol;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<String> {

    private List<String> values;

    public ListViewAdapter(Context context, List<String> values) {
        super(context, 0, values);
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //Obtener Instancia Inflater
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Guardar la referencia del View de la fila
        View v = convertView;

        //Comprobar si el View existe
        //Si no existe inflarlo
        if(null == convertView){
            v= inflater.inflate(R.layout.row,parent,false);
            //Colorear cada 2 lineas
           // if(position % 2 == 0){v.setBackgroundColor(Color.rgb(238, 233, 233));
            // }
        }

        //Obtener instancias de los elementos
        TextView lblModelo = (TextView)v.findViewById(R.id.lblcategoria);
        if (values.size()>0){
            //Obtener instancia de la Tarea en la posicion actual
            lblModelo.setText(values.get(position));
        }

        //Devolver al ListView la fila creada
        return v;
    }
}
