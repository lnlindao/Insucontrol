package com.example.espol.insucontrol.DataBase;


import com.example.espol.insucontrol.Conexion.Constantes;


public class Projection {



    /**
     * Proyección para las consultas bajada
     */
    public static final String[] PROJECTION_PROVINCIA = new String[]{
            ContractProvincia.Columnas._ID,
            Constantes.ID_REMOTA,
            ContractProvincia.Columnas.NOMBRE
    };



    public static final String[] PROJECTION_CIUDAD = new String[]{
            ContractCiudad.Columnas._ID,
            Constantes.ID_REMOTA,
            ContractCiudad.Columnas.NOMBRE,
            ContractCiudad.Columnas.ID_PROVINCIA
    };


    /**
     * Proyección para las consultas subida
     */

//    public static final String[] PROJECTION_INSERTEXHIBICION = new String[]{
//            InsertExhibicion.Columnas._ID,
//            Constantes.ID_REMOTA,
//            InsertExhibicion.Columnas.CODIGO,
//            InsertExhibicion.Columnas.RE,
//            InsertExhibicion.Columnas.LOCAL,
//            InsertExhibicion.Columnas.SUPERVISOR,
//            InsertExhibicion.Columnas.USUARIO,
//            InsertExhibicion.Columnas.EXHIBICION,
//            InsertExhibicion.Columnas.CATEGORIA,
//            InsertExhibicion.Columnas.ESTADO,
//            InsertExhibicion.Columnas.FECHA,
//            InsertExhibicion.Columnas.HORA
//    };





    /*
    *** Indices para las columnas indicadas en la proyección
     */

    public static final int COLUMNA_ID = 0;
    public static final int COLUMNA_ID_REMOTA = 1;

    //LOGIN
//    public static final int COLUMNA_SUPERVISOR = 2;
//    public static final int COLUMNA_MERCADERISTA = 3;
//    public static final int COLUMNA_PASS = 4;

    //PROVINCIA
    public static final int COLUMNA_NOMBRE = 2;

    //CIUDAD
    public static final int COLUMNA_CIUDAD = 2;
    public static final int COLUMNA_ID_PROV2 = 3;

}
