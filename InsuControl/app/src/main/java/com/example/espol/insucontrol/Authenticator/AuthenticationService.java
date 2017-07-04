package com.example.espol.insucontrol.Authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


//Bound service para gestionar la autenticación
public class AuthenticationService extends Service {
    private ExpenseAuthenticator autenticador;

    @Override
    public void onCreate() {
        // Nueva instancia del autenticador
        autenticador = new ExpenseAuthenticator(this);
    }

    /*
     * Ligando el servicio al framework de Android
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return autenticador.getIBinder();
    }
}
