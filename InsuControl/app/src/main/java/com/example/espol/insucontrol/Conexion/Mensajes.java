package com.example.espol.insucontrol.Conexion;

/**
 * Created by Lucky Ecuador on 02/12/2016.
 */

public class Mensajes {

    //MensajeLogin
    public static final String ERROR_LOGIN = "Credenciales Incorrectas.";
    public static final String ERROR_RED = "No hay conexión a la red.";
    public static final String CODIGO_ERROR = "Por favor ingresar un código válido.";


    public static final String RE_NULL = "RE perdido, por favor volver a insertar el código de Punto de Venta.";


    // Extras para intent local
    public static final String EXTRA_RESULTADO = "extra.resultado";
    public static final String EXTRA_MENSAJE = "extra.mensaje";

    public static final String SYNC_FINALIZADA="Sincronización finalizada: ";
    public static final String SYNC_FINALIZADA_PDV="Sincronización finalizada: PuntoVenta";
    public static final String SYNC_NOREQUERIDA="No se requiere sincronización de: ";
    public static final String SYNC_NODATOSCOLA="No hay datos en cola de sincronización de: ";
    public static final String SYNC_NODATA="No se encontraron registros en el servidor.";
    public static final String SYNC_ERROR= "No se ha podido conectar al servidor.";

    //OnSync
    public static final String ON_SYNC = "Sincronizando, Por Favor Espere...";
    public static final String SYNC_NO_USER = "Debe insertar un nombre de usuario válido para sincronizar.";
    public static final String ON_SYNC_DEVICE = "Almacenando datos en el teléfono.";
    public static final String ON_SYNC_SERVER = "Almacenando datos, No presionar. \n Por Favor Espere...";


    public static final String MENSAJE_PROM="Seleccione un Tipo de Promoción.";
    public static final String MENSAJE_EXH= "Seleccione Zona de Exhibición.";


    public static final String NO_SEND= "Datos no enviados, almacenados en el teléfono.";



}
