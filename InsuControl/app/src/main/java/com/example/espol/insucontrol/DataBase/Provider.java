package com.example.espol.insucontrol.DataBase;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.espol.insucontrol.Conexion.Constantes;


public class Provider extends ContentProvider {
    /**
     * Nombre de la base de datos
     */
    public static final String DATABASE_NAME = "insucontrol.db";
    /**
     * Versión actual de la base de datos
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * Instancia global del Content Resolver
     */
    private ContentResolver resolver;
    /**
     * Instancia del administrador de BD
     */
    private DatabaseHelper databaseHelper;

    @Override
    public boolean onCreate() {
        // Inicializando gestor BD
        databaseHelper = new DatabaseHelper(
                getContext(),
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );

        resolver = getContext().getContentResolver();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Obtener base de datos
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor c;
        // Comparar Uri
        if(ContractCiudad.uriMatcher.match(uri)== ContractCiudad.ALLROWS){
            // Consultando todos los registros
            c = db.query(ContractCiudad.CIUDAD, projection,
                    selection, selectionArgs,
                    null, null, sortOrder);
            c.setNotificationUri(
                    resolver,
                    ContractCiudad.CONTENT_URI);
        }else if(ContractCiudad.uriMatcher.match(uri)== ContractCiudad.SINGLE_ROW){
            // Consultando un solo registro basado en el Id del Uri
            long idGasto = ContentUris.parseId(uri);
            c = db.query(ContractCiudad.CIUDAD, projection,
                    ContractCiudad.Columnas._ID + " = " + idGasto,
                    selectionArgs, null, null, sortOrder);
            c.setNotificationUri(
                    resolver,
                    ContractCiudad.CONTENT_URI);
        }


        else if(ContractProvincia.uriMatcher.match(uri)== ContractProvincia.ALLROWS){
            // Consultando todos los registros
            c = db.query(ContractProvincia.PROVINCIA, projection,
                    selection, selectionArgs,
                    null, null, sortOrder);
            c.setNotificationUri(
                    resolver,
                    ContractProvincia.CONTENT_URI);
        }else if(ContractProvincia.uriMatcher.match(uri)== ContractProvincia.SINGLE_ROW){
            // Consultando un solo registro basado en el Id del Uri
            long idGasto = ContentUris.parseId(uri);
            c = db.query(ContractProvincia.PROVINCIA, projection,
                    ContractProvincia.Columnas._ID + " = " + idGasto,
                    selectionArgs, null, null, sortOrder);
            c.setNotificationUri(
                    resolver,
                    ContractProvincia.CONTENT_URI);
        }

        /*
         *      INSERTS
         */

//        else if(InsertExhibicion.uriMatcher.match(uri)==InsertExhibicion.ALLROWS){
//            // Consultando todos los registros
//            c = db.query(InsertExhibicion.INSERT_EXHIBICION, projection,
//                    selection, selectionArgs,
//                    null, null, sortOrder);
//            c.setNotificationUri(
//                    resolver,
//                    InsertExhibicion.CONTENT_URI);
//        }else if(InsertExhibicion.uriMatcher.match(uri)==InsertExhibicion.SINGLE_ROW){
//            // Consultando un solo registro basado en el Id del Uri
//            long idGasto = ContentUris.parseId(uri);
//            c = db.query(InsertExhibicion.INSERT_EXHIBICION, projection,
//                    InsertExhibicion.Columnas._ID + " = " + idGasto,
//                    selectionArgs, null, null, sortOrder);
//            c.setNotificationUri(
//                    resolver,
//                    InsertExhibicion.CONTENT_URI);
//        }


        else{
            throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {

        if(ContractCiudad.uriMatcher.match(uri)== ContractCiudad.ALLROWS){

            return ContractCiudad.MULTIPLE_MIME;

        }else if(ContractCiudad.uriMatcher.match(uri)== ContractCiudad.SINGLE_ROW){

            return ContractCiudad.SINGLE_MIME;

        }


        else if(ContractProvincia.uriMatcher.match(uri)== ContractProvincia.ALLROWS){

            return ContractProvincia.MULTIPLE_MIME;

        }else if(ContractProvincia.uriMatcher.match(uri)== ContractProvincia.SINGLE_ROW){

            return ContractProvincia.SINGLE_MIME;

        }



        else{
            throw new IllegalArgumentException("Tipo de URI desconocida: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if (ContractCiudad.uriMatcher.match(uri) == ContractCiudad.ALLROWS) {
            ContentValues contentValues;
            if (values != null) {
                contentValues = new ContentValues(values);
            } else {
                contentValues = new ContentValues();
            }

            // Inserción de nueva fila
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long rowId = db.insert(ContractCiudad.CIUDAD, null, contentValues);
            if (rowId > 0) {
                Uri uri_stock = ContentUris.withAppendedId(
                        ContractCiudad.CONTENT_URI, rowId);
                resolver.notifyChange(uri_stock, null, false);
                return uri_stock;
            }
            throw new SQLException("Falla al insertar fila en : " + uri);
        }

        if(ContractProvincia.uriMatcher.match(uri) == ContractProvincia.ALLROWS){
            ContentValues contentValues;
            if (values != null) {
                contentValues = new ContentValues(values);
            } else {
                contentValues = new ContentValues();
            }

            // Inserción de nueva fila
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            long rowId = db.insert(ContractProvincia.PROVINCIA, null, contentValues);
            if (rowId > 0) {
                System.out.println("INSERT ContractProvincia: insertando localmente");
                Uri uri_stock = ContentUris.withAppendedId(
                        ContractProvincia.CONTENT_URI, rowId);
                resolver.notifyChange(uri_stock, null, false);
                return uri_stock;
            }
            throw new SQLException("Falla al insertar fila en ContractProvincia: " + uri);
        }



//        if (InsertExhibicion.uriMatcher.match(uri) == InsertExhibicion.ALLROWS) {
//            ContentValues contentValues;
//            if (values != null) {
//                contentValues = new ContentValues(values);
//            } else {
//                contentValues = new ContentValues();
//            }
//
//            // Inserción de nueva fila
//            SQLiteDatabase db = databaseHelper.getWritableDatabase();
//            long rowId = db.insert(InsertExhibicion.INSERT_EXHIBICION, null, contentValues);
//            if (rowId > 0) {
//                Uri uri_stock = ContentUris.withAppendedId(
//                        InsertExhibicion.CONTENT_URI, rowId);
//                resolver.notifyChange(uri_stock, null, false);
//                return uri_stock;
//            }
//            throw new SQLException("Falla al insertar fila en : " + uri);
//        }

        throw new IllegalArgumentException("URI desconocida : " + uri);
    }






    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

         int affected;


        if(ContractCiudad.uriMatcher.match(uri)==ContractCiudad.ALLROWS){
            affected = db.delete(ContractCiudad.CIUDAD,
                    selection,
                    selectionArgs);
        }else if(ContractCiudad.uriMatcher.match(uri)==ContractCiudad.SINGLE_ROW){
            long idStock = ContentUris.parseId(uri);
            affected = db.delete(ContractCiudad.CIUDAD,
                    ContractCiudad.CIUDAD + "=" + idStock
                            + (!TextUtils.isEmpty(selection) ?
                            " AND (" + selection + ')' : ""),
                    selectionArgs);
            // Notificar cambio asociado a la uri
            resolver.
                    notifyChange(uri, null, false);
        }

        else if(ContractProvincia.uriMatcher.match(uri)== ContractProvincia.ALLROWS){
            affected = db.delete(ContractProvincia.PROVINCIA,
                    selection,
                    selectionArgs);
        }else if(ContractProvincia.uriMatcher.match(uri)== ContractProvincia.SINGLE_ROW){
            long idPos = ContentUris.parseId(uri);
            affected = db.delete(ContractProvincia.PROVINCIA,
                    Constantes.ID_REMOTA + "=" + idPos
                            + (!TextUtils.isEmpty(selection) ?
                            " AND (" + selection + ')' : ""),
                    selectionArgs);
            // Notificar cambio asociado a la uri
            resolver.
                    notifyChange(uri, null, false);
        }


//        else if(InsertExhibicion.uriMatcher.match(uri)==InsertExhibicion.ALLROWS){
//            affected = db.delete(InsertExhibicion.INSERT_EXHIBICION,
//                    selection,
//                    selectionArgs);
//        }else if(InsertExhibicion.uriMatcher.match(uri)==InsertExhibicion.SINGLE_ROW){
//            long idStock = ContentUris.parseId(uri);
//            affected = db.delete(InsertExhibicion.INSERT_EXHIBICION,
//                    Constantes.ID_REMOTA + "=" + idStock
//                            + (!TextUtils.isEmpty(selection) ?
//                            " AND (" + selection + ')' : ""),
//                    selectionArgs);
//            // Notificar cambio asociado a la uri
//            resolver.
//                    notifyChange(uri, null, false);
//        }

        else{
            throw new IllegalArgumentException("Elemento local desconocido: " + uri);
        }

        return affected;
    }




    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int affected;

        if(ContractCiudad.uriMatcher.match(uri)==ContractCiudad.ALLROWS){
            System.out.println("UPDATE");
            affected = db.update(ContractCiudad.CIUDAD, values,
                    selection, selectionArgs);
        }else if(ContractCiudad.uriMatcher.match(uri)==ContractCiudad.SINGLE_ROW){
            String idStock = uri.getPathSegments().get(1);
            affected = db.update(ContractCiudad.CIUDAD, values,
                    Constantes.ID_REMOTA + "=" + idStock
                            + (!TextUtils.isEmpty(selection) ?
                            " AND (" + selection + ')' : ""),
                    selectionArgs);
        }

        else if(ContractProvincia.uriMatcher.match(uri)== ContractProvincia.ALLROWS){
            System.out.println("UPDATE ContractProvincia: Actualiza el contenido Local");
            affected = db.update(ContractProvincia.PROVINCIA, values,
                    selection, selectionArgs);
        }else if(ContractProvincia.uriMatcher.match(uri)== ContractProvincia.SINGLE_ROW){
            String idStock = uri.getPathSegments().get(1);
            affected = db.update(ContractProvincia.PROVINCIA, values,
                    Constantes.ID_REMOTA + "=" + idStock
                            + (!TextUtils.isEmpty(selection) ?
                            " AND (" + selection + ')' : ""),
                    selectionArgs);
        }


//        else if(InsertExhibicion.uriMatcher.match(uri)==InsertExhibicion.ALLROWS){
//            System.out.println("UPDATE");
//            affected = db.update(InsertExhibicion.INSERT_EXHIBICION, values,
//                    selection, selectionArgs);
//        }else if(InsertExhibicion.uriMatcher.match(uri)==InsertExhibicion.SINGLE_ROW){
//            String idStock = uri.getPathSegments().get(1);
//            affected = db.update(InsertExhibicion.INSERT_EXHIBICION, values,
//                    Constantes.ID_REMOTA + "=" + idStock
//                            + (!TextUtils.isEmpty(selection) ?
//                            " AND (" + selection + ')' : ""),
//                    selectionArgs);
//        }


        else{
            throw new IllegalArgumentException("URI desconocida: " + uri);
        }

        resolver.notifyChange(uri, null, true);
        return affected;
    }
}
