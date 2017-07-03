package com.example.espol.insucontrol.Sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.espol.insucontrol.Clases.Ciudad;
import com.example.espol.insucontrol.Clases.Provincia;
import com.example.espol.insucontrol.Conexion.Constantes;
import com.example.espol.insucontrol.Conexion.Mensajes;
import com.example.espol.insucontrol.Conexion.VolleySingleton;
import com.example.espol.insucontrol.DataBase.ContractCiudad;
import com.example.espol.insucontrol.DataBase.ContractProvincia;
import com.example.espol.insucontrol.DataBase.Projection;
import com.example.espol.insucontrol.R;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucky Ecuador on 15/07/2016.
 */
//Maneja la transferencia de datos entre el servidor y el cliente
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    private static String valueActivity;
    ContentResolver resolver;
    private Gson gson = new Gson();
    private static String user;
    private static String codigo;



    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        resolver = context.getContentResolver();
    }

    /**
     * Constructor para mantener compatibilidad en versiones inferiores a 3.0
     */
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        resolver = context.getContentResolver();
    }

    public static void inicializarSyncAdapter(Context context) {
        obtenerCuentaASincronizar(context);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              final SyncResult syncResult) {

        Log.i(TAG, "onPerformSync()...");

        boolean soloSubida = extras.getBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, false);

        if (!soloSubida) {//Bajando
            //Llena la Base/Datos Local con los datos del Servidor
            if(valueActivity.equals(Constantes.bajar_data)){
                realizarSincronizacionLocalProvincia(syncResult);
                realizarSincronizacionLocalCiudad(syncResult);
            }

        }else{
            if(valueActivity.equals(Constantes.insert_data)){
//                realizarSincronizacionRemotaInsert();
            }
        }
    }

    /**
     * Inicia manualmente la sincronización
     *
     * @param context    Contexto para crear la petición de sincronización
     * @param onlyUpload Usa true para sincronizar el servidor o false para sincronizar el cliente
     */
    public static void sincronizarAhora(Context context, boolean onlyUpload, String valueActivity1, String userName, String usercodigo) {
        valueActivity = valueActivity1;
        user = userName;
        codigo = usercodigo;
        //Toast.makeText(context,"Realizando petición de sincronización remota manual.",Toast.LENGTH_LONG).show();
        Log.i(TAG, "Realizando petición de sincronización remota manual.");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (onlyUpload)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_UPLOAD, true);
        ContentResolver.requestSync(obtenerCuentaASincronizar(context),
                context.getString(R.string.provider_authority), bundle);
    }

    /**
     * Crea u obtiene una cuenta existente
     *
     * @param context Contexto para acceder al administrador de cuentas
     * @return cuenta auxiliar.
     */
    private static Account obtenerCuentaASincronizar(Context context) {
        // Obtener instancia del administrador de cuentas
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Crear cuenta por defecto
        Account newAccount = new Account(
                context.getString(R.string.app_name), Constantes.ACCOUNT_TYPE);

        // Comprobar existencia de la cuenta
        if (null == accountManager.getPassword(newAccount)) {

            // Añadir la cuenta al account manager sin password y sin datos de usuario
            if (!accountManager.addAccountExplicitly(newAccount, "", null))
                return null;

        }
        Log.i(TAG, "SyncAdapter: Cuenta de usuario obtenida.");
        return newAccount;
    }


/*
     *  SINCRONIZAR TABLA LOCAL
     */

    public void realizarSincronizacionLocalProvincia(final SyncResult syncResult){
        Log.i(TAG, "Realizando Sincronizacion Local de Provincia.");

        //GET METHOD
        VolleySingleton.getInstance(getContext()).addToRequestQueue(new JsonObjectRequest
                (Request.Method.POST, Constantes.GET_PROVINCIA, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        procesarRespuestaGetProvincia(response, syncResult);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG,"Error en sincronizacion del servidor al cliente local: "+error.getMessage());
                                enviarBroadcast(true, Mensajes.SYNC_ERROR);
                            }
                        }
                ));
    }

    /**
     * Procesa la respuesta del servidor al pedir que se retornen todos los productos.
     *
     * @param response   Respuesta en formato Json
     * @param syncResult Registro de resultados de sincronización
     */
    private void procesarRespuestaGetProvincia(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);

            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocalesProvincia(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    enviarBroadcast(true, Mensajes.SYNC_NODATA);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Actualiza los registros locales a través de una comparación con los datos
     * del servidor
     *
     * @param response   Respuesta en formato Json obtenida del servidor
     * @param syncResult Registros de la sincronización
     */
    private void actualizarDatosLocalesProvincia(JSONObject response, SyncResult syncResult) {

        JSONArray loginResult = null;

        try {
            // Obtener array "gastos"
            loginResult = response.getJSONArray(Constantes.PROVINCIAS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        Provincia[] res = gson.fromJson(loginResult != null ? loginResult.toString() : null, Provincia[].class);
        List<Provincia> data = Arrays.asList(res);

        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        // Tabla hash para recibir los datos entrantes
        HashMap<String, Provincia> expenseMap = new HashMap<>();
        for (Provincia e : data) {
            expenseMap.put(e.id_provincia, e);
        }

        // Consultar registros remotos actuales
        Uri uri = ContractProvincia.CONTENT_URI;
        String select = Constantes.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, Projection.PROJECTION_PROVINCIA, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String id;
        String nombre;

        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            id = c.getString(Projection.COLUMNA_ID_REMOTA);
            nombre = c.getString(Projection.COLUMNA_NOMBRE);

            Provincia match = expenseMap.get(id);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(id);

                Uri existingUri = ContractProvincia.CONTENT_URI.buildUpon()
                        .appendPath(id).build();

                // Comprobar si el gasto necesita ser actualizado
                boolean b = match.nombre != null && !match.nombre.equals(nombre);

                if (b) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractProvincia.Columnas.NOMBRE, match.nombre)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractProvincia.CONTENT_URI.buildUpon()
                        .appendPath(id).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (Provincia e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de Tabla Provincia en Base Local: " + e.id_provincia);
            ops.add(ContentProviderOperation.newInsert(ContractProvincia.CONTENT_URI)
                    .withValue(Constantes.ID_REMOTA, e.id_provincia)
                    .withValue(ContractProvincia.Columnas.NOMBRE, e.nombre)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(Constantes.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractProvincia.CONTENT_URI,
                    null,
                    false);
//            Toast.makeText(getContext(),"Sincronización de PuntoVenta finalizada.",Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Sincronización de tabla Provincia finalizada.");
            enviarBroadcast(true, Mensajes.SYNC_FINALIZADA_PDV);

        } else {
            Log.i(TAG, "No se requiere sincronización de tabla Provincia");
            enviarBroadcast(true, Mensajes.SYNC_NOREQUERIDA);
        }

    }



    /*
     *  SINCRONIZAR TABLA Ciudad
    */

    public void realizarSincronizacionLocalCiudad(final SyncResult syncResult){
        Log.i(TAG, "Realizando Sincronizacion Local de Ciudad.");

//        enviarBroadcast(true, Constantes.MENSAJE_READY);
        //GET METHOD
        VolleySingleton.getInstance(getContext()).addToRequestQueue(new JsonObjectRequest
                (Request.Method.POST, Constantes.GET_CIUDADES, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        procesarRespuestaGetTipoCiudad(response, syncResult);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG,"Error en sincronizacion del servidor al cliente local: "+error.getMessage());
                                enviarBroadcast(true, Mensajes.SYNC_ERROR);
                            }
                        }
                ));
    }

    private void procesarRespuestaGetTipoCiudad(JSONObject response, SyncResult syncResult) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString(Constantes.ESTADO);

            switch (estado) {
                case Constantes.SUCCESS: // EXITO
                    actualizarDatosLocalesTipoCiudad(response, syncResult);
                    break;
                case Constantes.FAILED: // FALLIDO
                    String mensaje = response.getString(Constantes.MENSAJE);
                    enviarBroadcast(true, Mensajes.SYNC_NODATA);
                    Log.i(TAG, mensaje);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void actualizarDatosLocalesTipoCiudad(JSONObject response, SyncResult syncResult) {

        JSONArray loginResult = null;

        try {
            // Obtener array "gastos"
            loginResult = response.getJSONArray(Constantes.CIUDADES);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Parsear con Gson
        Ciudad[] res = gson.fromJson(loginResult != null ? loginResult.toString() : null, Ciudad[].class);
        List<Ciudad> data = Arrays.asList(res);

        // Lista para recolección de operaciones pendientes
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // Tabla hash para recibir los datos entrantes
        HashMap<String, Ciudad> expenseMap = new HashMap<String, Ciudad>();
        for (Ciudad e : data) {
            expenseMap.put(e.id_ciudad, e);
        }

        // Consultar registros remotos actuales
        Uri uri = ContractCiudad.CONTENT_URI;
        String select = Constantes.ID_REMOTA + " IS NOT NULL";
        Cursor c = resolver.query(uri, Projection.PROJECTION_CIUDAD, select, null, null);
        assert c != null;

        Log.i(TAG, "Se encontraron " + c.getCount() + " registros locales.");

        // Encontrar datos obsoletos
        String id;
        String nombre;
        String id_prov;

        while (c.moveToNext()) {
            syncResult.stats.numEntries++;

            id = c.getString(Projection.COLUMNA_ID_REMOTA);
            nombre = c.getString(Projection.COLUMNA_CIUDAD);
            id_prov = c.getString(Projection.COLUMNA_ID_PROV2);

            Ciudad match = expenseMap.get(id);

            if (match != null) {
                // Esta entrada existe, por lo que se remueve del mapeado
                expenseMap.remove(id);

                Uri existingUri = ContractCiudad.CONTENT_URI.buildUpon()
                        .appendPath(id).build();


                // Comprobar si el gasto necesita ser actualizado
                boolean b = match.nombre != null && !match.toString().equals(nombre);
                boolean b1 = match.id_provincia != null && !match.toString().equals(id_prov);

                if (b || b1) {

                    Log.i(TAG, "Programando actualización de: " + existingUri);

                    ops.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(ContractCiudad.Columnas.NOMBRE, match.nombre)
                            .withValue(ContractCiudad.Columnas.ID_PROVINCIA, match.id_provincia)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No hay acciones para este registro: " + existingUri);
                }
            } else {
                // Debido a que la entrada no existe, es removida de la base de datos
                Uri deleteUri = ContractCiudad.CONTENT_URI.buildUpon()
                        .appendPath(id).build();
                Log.i(TAG, "Programando eliminación de: " + deleteUri);
                ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Insertar items resultantes
        for (Ciudad e : expenseMap.values()) {
            Log.i(TAG, "Programando inserción de Tabla Ciudad en Base Local: " + e.id_ciudad);
            ops.add(ContentProviderOperation.newInsert(ContractCiudad.CONTENT_URI)
                    .withValue(Constantes.ID_REMOTA, e.id_ciudad)
                    .withValue(ContractCiudad.Columnas.NOMBRE, e.nombre)
                    .withValue(ContractCiudad.Columnas.ID_PROVINCIA, e.id_provincia)
                    .build());
            syncResult.stats.numInserts++;
        }

        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Aplicando operaciones...");
            try {
                resolver.applyBatch(Constantes.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            resolver.notifyChange(
                    ContractCiudad.CONTENT_URI,
                    null,
                    false);
            Log.i(TAG, "Sincronización de tabla Ciudad finalizada.");
//            enviarBroadcast(true, Constantes.MENSAJE_FIN_TIPO);
            enviarBroadcast(true, Mensajes.SYNC_FINALIZADA + "Ciudad");

        } else {
            Log.i(TAG, "No se requiere sincronización de tabla Ciudad");
            enviarBroadcast(true, Mensajes.SYNC_NOREQUERIDA + "Ciudad");
//            enviarBroadcast(true, Constantes.MENSAJE_NO);
        }
    }




    /*
     * * * INSERT
     */
//    private void realizarSincronizacionRemotaInsertExh() {
//        Log.i(TAG, "SyncAdapterSubida: Preparando datos a ser insertados en el servidor...");
//
//        iniciarActualizacion(InsertExhibicion.CONTENT_URI);
//
//        Cursor c = obtenerRegistrosSucios(InsertExhibicion.CONTENT_URI, Projection.PROJECTION_INSERTEXHIBICION);
//
//        Log.i(TAG, "SyncAdapterSubida: Se encontraron " + c.getCount() + " registros sucios.");
//
//        if (c.getCount() > 0) {
//            while (c.moveToNext()) {
//                if(!c.getString(2).equals("0")) {
//                    final int idLocal = c.getInt(Projection.COLUMNA_ID);
//
//                    Log.i(TAG, "SyncAdapterSubida: Insertando datos de Exhibicion en el servidor...");
//
//                    VolleySingleton.getInstance(getContext()).addToRequestQueue(
//                            new JsonObjectRequest(
//                                    Request.Method.POST,
//                                    Constantes.INSERTAR_EXHIBICION,
//                                    UtilidadesExhibicion.deCursorAJSONObject(c),
//                                    new Response.Listener<JSONObject>() {
//                                        @Override
//                                        public void onResponse(JSONObject response) {
//                                            procesarRespuestaInsertExh(response, idLocal);
//                                        }
//                                    },
//                                    new Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            Toast.makeText(getContext(), "Datos no enviados, almacenados en el teléfono.", Toast.LENGTH_SHORT).show();
//                                            Log.i(TAG, "SyncAdapterSubida: Problema en respuesta del servidor...");
//                                            Log.d(TAG, "Error Volley: " + error.getMessage());
//                                        }
//                                    }
//                            ) {
//                                @Override
//                                public Map<String, String> getHeaders() {
//                                    Map<String, String> headers = new HashMap<String, String>();
//                                    headers.put("Content-Type", "application/json; charset=utf-8");
//                                    headers.put("Accept", "application/json");
//                                    return headers;
//                                }
//
//                                @Override
//                                public String getBodyContentType() {
//                                    return "application/json; charset=utf-8" + getParamsEncoding();
//                                }
//                            }
//                    );
//                }
//            }
//
//        } else {
//            //Toast.makeText(getContext(),"No hay datos en cola de sincronizacion",Toast.LENGTH_SHORT).show();
////            StockActivity stockActivity = new StockActivity();
////            stockActivity.showToastFromBackground("No se requiere sincronización");
//            Log.i(TAG, "SyncAdapterSubida: No se requiere sincronización: No hay datos en cola de sincronizacion");
//            enviarBroadcast(true, Mensajes.SYNC_NODATOSCOLA  + "Exhibición");
//        }
//        c.close();
//    }
//
//
//    public void procesarRespuestaInsertExh(JSONObject response, int idLocal) {
//
//        try {
//            // Obtener estado
//            String estado = response.getString(Constantes.ESTADO);
//            // Obtener mensaje
//            String mensaje = response.getString(Constantes.MENSAJE);
//
//            switch (estado) {
//                case Constantes.SUCCESS:
//                    // Obtener identificador del nuevo registro creado en el servidor
//                    String idRemota = response.getString(Constantes.ID_EXH);
//                    Log.i(TAG, mensaje);
//                    //Borrar datos sucios:
//                    finalizarActualizacion(InsertExhibicion.CONTENT_URI, InsertExhibicion.Columnas._ID, idRemota, idLocal);
//                    Toast.makeText(getContext(),"Datos almacenados en el Servidor",Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "SyncAdapterSubida: Registros Insertados en el Servidor y en la Base Local");
//                    break;
//
//                case Constantes.FAILED:
//                    Log.i(TAG, mensaje);
//                    break;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }




    /*
     * ***********************************************************************************************************************
     */


    /**
     * Cambia a estado "de sincronización" el registro que se acaba de insertar localmente
     */
    private void iniciarActualizacion(Uri ContentUri) {//Uri
        Uri uri = ContentUri;
        String selection = Constantes.PENDIENTE_INSERCION + "=? AND "
                + Constantes.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", Constantes.ESTADO_OK + ""};

        ContentValues v = new ContentValues();
        v.put(Constantes.ESTADO, Constantes.ESTADO_SYNC);

        int results = resolver.update(uri, v, selection, selectionArgs);
        Log.i(TAG, "SyncAdapterSubida: Registros puestos en cola de inserción: " + results);
    }

    /**
     * Limpia el registro que se sincronizó y le asigna la nueva id remota proveida
     * por el servidor
     *
     * @param idRemota id remota
     */
    private void finalizarActualizacion(Uri ContentUri, String ID, String idRemota, int idLocal) {
        Log.i(TAG, "SyncAdapterSubida:finalizando actualizacion");
        Uri uri = ContentUri;
        String selection =  ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(idLocal)};

        ContentValues v = new ContentValues();
        v.put(Constantes.PENDIENTE_INSERCION, "0");
        v.put(Constantes.ESTADO, Constantes.ESTADO_OK);
        v.put(Constantes.ID_REMOTA, idRemota);

        resolver.update(uri, v, selection, selectionArgs);
    }

    /**
     * Obtiene el registro que se acaba de marcar como "pendiente por sincronizar" y
     * con "estado de sincronización"
     * @return Cursor con el registro.
     */
    private Cursor obtenerRegistrosSucios(Uri ContentUri, String[] projection) {//Uri, projection
        Uri uri = ContentUri;
        String selection = Constantes.PENDIENTE_INSERCION + "=? AND "
                + Constantes.ESTADO + "=?";
        String[] selectionArgs = new String[]{"1", Constantes.ESTADO_SYNC + ""};

        return resolver.query(uri, projection, selection, selectionArgs, null);
    }




    private void enviarBroadcast(boolean estado, String mensaje) {
        Intent intentLocal = new Intent(Intent.ACTION_SYNC);
        intentLocal.putExtra(Mensajes.EXTRA_RESULTADO, estado);
        intentLocal.putExtra(Mensajes.EXTRA_MENSAJE, mensaje);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intentLocal);
    }




}