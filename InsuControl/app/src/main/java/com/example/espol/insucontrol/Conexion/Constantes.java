package com.example.espol.insucontrol.Conexion;


public class Constantes {


    /**
     * URLs del Web Service
     */
    private static final String URL = "http://insucontrol.life";

    //OBTENER DATA
    public static final String GET_CIUDADES = URL + "/pdo_servicios/Ws_Ic/vista/obtenerCiudades.php";
    public static final String GET_PROVINCIA = URL + "/pdo_servicios/Ws_Ic/vista/obtenerProvincias.php";
    public static final String GET_CATEGORIA = URL + "/pdo_servicios/Ws_Ic/vista/obtenerCategoria.php";
    public static final String GET_ALIMENTOS = URL + "/pdo_servicios/Ws_Ic/vista/obtenerCarbohidratos.php";

    //INSERTAR DATA
    public static final String INSERT_REGISTRO = URL + "/pdo_servicios/Ws_Ic/vista/guardarUsuario.php";
    public static final String INSERT_INFOMEDICA = URL + "/pdo_servicios/Ws_Ic/vista/guardarInfoMedica.php";


    /**
     * Campos de las respuestas Json
     */
    public static final String MENSAJE = "mensaje";

    public static final String PROVINCIAS = "provincias";
    public static final String CIUDADES = "ciudades";

    public static final String ID = "ultimoId";
    //CONSTANTES BAJADA
    public static final String bajar_data="bajarData";

    //CONSTANTES SUBIDA
    public static final String insert_data="insertData";

    /**
     * Códigos del campo {@link //ESTADO}
     */
    public static final String SUCCESS = "1";
    public static final String FAILED = "2";

    /**
     * Tipo de cuenta para la sincronización
     */
    public static final String ACCOUNT_TYPE = "com.example.espol.insucontrol.account";

    /**
     * Autoridad del Content Provider
     */
    public final static String AUTHORITY
            = "com.example.espol.insucontrol";
    /**
     * Constantes Contracts
     */
    public static final int ESTADO_OK = 0;
    public static final int ESTADO_SYNC = 1;

    public static final String ESTADO = "estado";
    public static final String ID_REMOTA = "idRemota";
    public final static String PENDIENTE_INSERCION = "pendiente_insercion";


    public static final String MOD_PRECIO = "Modulo Precio";
    public static final String MOD_INV = "Modulo Inventario";
    public static final String MOD_PRESENCIA = "Modulo Surtido Correcto";
    public static final String MOD_SHARE = "Modulo Share of Shelf";
}
