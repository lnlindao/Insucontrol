package com.example.espol.insucontrol.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.espol.insucontrol.Conexion.Constantes;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createTableProvincia(database); // Crear taba "tb_provincia"
        createTableCiudad(database);    // Crear tabla "tb_ciudad"
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try
        {
//            db.execSQL("drop table " + InsertRastreo.INSERT_GEO);
        }
        catch (SQLiteException e) { }
        onCreate(db);
    }



    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

    /*
     *  CREAR REPOSITORIOS
     */



    private void createTableProvincia(SQLiteDatabase database){
        String cmd = "CREATE TABLE " + ContractProvincia.PROVINCIA + " (" +
                ContractProvincia.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractProvincia.Columnas.NOMBRE + " TEXT, " +
                Constantes.ID_REMOTA + " TEXT UNIQUE," +
                Constantes.ESTADO + " INTEGER NOT NULL DEFAULT "+ Constantes.ESTADO_OK+"," +
                Constantes.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
        database.execSQL(cmd);
    }


    private void createTableCiudad(SQLiteDatabase database){
        String cmd = "CREATE TABLE " + ContractCiudad.CIUDAD + " (" +
                ContractCiudad.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractCiudad.Columnas.NOMBRE + " TEXT, " +
                ContractCiudad.Columnas.ID_PROVINCIA + " TEXT, " +
                Constantes.ID_REMOTA + " TEXT UNIQUE," +
                Constantes.ESTADO + " INTEGER NOT NULL DEFAULT "+ Constantes.ESTADO_OK+"," +
                Constantes.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
//                Constantes.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0," +
//                "FOREIGN KEY(" +ContractCiudad.Columnas.ID_PROVINCIA + ") REFERENCES " +
//                ContractProvincia.PROVINCIA+"(" + ContractProvincia.Columnas._ID + "))";
        database.execSQL(cmd);
    }


//    private void createTableInsertExhibicion(SQLiteDatabase database){
//        String cmd = "CREATE TABLE " + InsertExhibicion.INSERT_EXHIBICION + " (" +
//                InsertExhibicion.Columnas._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                InsertExhibicion.Columnas.CODIGO + " TEXT, " +
//                InsertExhibicion.Columnas.RE + " TEXT, " +
//                InsertExhibicion.Columnas.LOCAL + " TEXT, " +
//                InsertExhibicion.Columnas.SUPERVISOR + " TEXT," +
//                InsertExhibicion.Columnas.USUARIO + " TEXT," +
//                InsertExhibicion.Columnas.EXHIBICION + " TEXT," +
//                InsertExhibicion.Columnas.CATEGORIA + " TEXT," +
//                InsertExhibicion.Columnas.ESTADO + " TEXT," +
//                InsertExhibicion.Columnas.FECHA + " TEXT," +
//                InsertExhibicion.Columnas.HORA + " TEXT," +
//                Constantes.ID_REMOTA + " TEXT UNIQUE," +
//                Constantes.ESTADO + " INTEGER NOT NULL DEFAULT "+ Constantes.ESTADO_OK+"," +
//                Constantes.PENDIENTE_INSERCION + " INTEGER NOT NULL DEFAULT 0)";
//        database.execSQL(cmd);
//    }

    /*
    ***     OPERACIONES
     */


    public List<String> getProvincia(){
        List<String> provincias = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ContractProvincia.PROVINCIA + " GROUP BY " + ContractProvincia.Columnas.NOMBRE;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                provincias.add(cursor.getString(cursor.getColumnIndexOrThrow(ContractProvincia.Columnas.NOMBRE)));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return provincias;
    }


    public String getIdProvincia(String prov){
        String provincias = "";

        // Select All Query
        String selectQuery = "SELECT DISTINCT " + ContractProvincia.Columnas._ID +" FROM " + ContractProvincia.PROVINCIA + " WHERE " +
                ContractProvincia.Columnas.NOMBRE + "=?";
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,new String[]{prov});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                provincias  = cursor.getString(cursor.getColumnIndexOrThrow(ContractProvincia.Columnas._ID));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return provincias;
    }




    public List<String> getCiudades(String id_prov){
        List<String> ciudades = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ContractCiudad.CIUDAD + " WHERE " +
                ContractCiudad.Columnas.ID_PROVINCIA + "=?" + " GROUP BY " + ContractCiudad.Columnas.NOMBRE;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,new String[]{id_prov});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ciudades.add(cursor.getString(cursor.getColumnIndexOrThrow(ContractCiudad.Columnas.NOMBRE)));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return ciudades;
    }
/*
    public Boolean verificarLogin(String name){

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ContractExhibicion.TIPOEXH + " WHERE "+
                ContractExhibicion.Columnas.SUPERVISOR + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{name});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            return true;
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return false;
    }


    public List<String> getRe(String name){
        List<String> ciudades = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ContractExhibicion.TIPOEXH + " WHERE "+
                ContractExhibicion.Columnas.USER + "=?" + " GROUP BY " + ContractExhibicion.Columnas.RE;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,new String[]{name});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ciudades.add(cursor.getString(cursor.getColumnIndexOrThrow(ContractExhibicion.Columnas.RE)));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return ciudades;
    }


    public String getPdv(){
        String pdv = "";

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ContractPuntoVentas.POS + " GROUP BY " + ContractPuntoVentas.Columnas.LOCAL;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (cursor.moveToLast()) {
            pdv = cursor.getString(cursor.getColumnIndexOrThrow(ContractPuntoVentas.Columnas.LOCAL));
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return pdv;
    }

    public List<String> getCategorias(String name, String re){
        List<String> cat = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ContractExhibicion.TIPOEXH + " WHERE "+
                ContractExhibicion.Columnas.USER + "=? AND " +
                ContractExhibicion.Columnas.RE + "=?" +
                " GROUP BY " + ContractExhibicion.Columnas.CATEGORIA;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,new String[]{name, re});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cat.add(cursor.getString(cursor.getColumnIndexOrThrow(ContractExhibicion.Columnas.CATEGORIA)));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return cat;
    }


    public List<String> getExhibiciones(String name){
        List<String> cat = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ContractExhibicion.TIPOEXH + " WHERE "+
                ContractExhibicion.Columnas.CATEGORIA + "=?" + " GROUP BY " + ContractExhibicion.Columnas.TIPO;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,new String[]{name});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cat.add(cursor.getString(cursor.getColumnIndexOrThrow(ContractExhibicion.Columnas.TIPO)));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        // returning lables
        return cat;
    }

*/
}
