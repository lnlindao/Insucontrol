package com.example.espol.insucontrol.DataBase;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.espol.insucontrol.Conexion.Constantes;


public class ContractCiudad {
    /**
     * Representación de la tabla a consultar
     */
    public static final String CIUDAD = "tb_ciudad";
    /**
     * Tipo MIME que retorna la consulta de una sola fila
     */
    public final static String SINGLE_MIME =
            "vnd.android.cursor.item/vnd." + Constantes.AUTHORITY + CIUDAD;
    /**
     * Tipo MIME que retorna la consulta de {@link //CONTENT_URI}
     */
    public final static String MULTIPLE_MIME =
            "vnd.android.cursor.dir/vnd." + Constantes.AUTHORITY + CIUDAD;
    /**
     * URI de contenido principal
     */
    public final static Uri CONTENT_URI =
            Uri.parse("content://" + Constantes.AUTHORITY + "/" + CIUDAD);
    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;
    /**
     * Código para URIs de multiples registros
     */
    public static final int ALLROWS = 1;
    /**
     * Código para URIS de un solo registro
     */
    public static final int SINGLE_ROW = 2;


    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Constantes.AUTHORITY, CIUDAD, ALLROWS);
        uriMatcher.addURI(Constantes.AUTHORITY, CIUDAD + "/#", SINGLE_ROW);
    }

    // Valores para la columna ESTADO
    public static final int ESTADO_OK = 0;
    public static final int ESTADO_SYNC = 1;


    /**
     * Estructura de la tabla
     */
    public static class Columnas implements BaseColumns {

        private Columnas() {
            // Sin instancias
        }

        public final static String NOMBRE = "nombre";
        public final static String ID_PROVINCIA = "id_provincia";

    }

}
