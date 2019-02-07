package com.gmail.BetterLifeThanksApp.DontBeLate.PierwszyEkran;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TwojePolaczenie {

    private static ConnectivityManager cm;

    private static NetworkInfo activeNetwork;


    protected TwojePolaczenie(Context context) {
        TwojePolaczenie.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TwojePolaczenie.activeNetwork = TwojePolaczenie.cm.getActiveNetworkInfo();
    }

    /**
     * Funcja sprawdza czy jesteś połączony z siecią
     * @return true jeśli się jesteś połączony,false jeśli nie.
     */
    protected boolean isConnected()
    {
        return TwojePolaczenie.activeNetwork!=null &&activeNetwork.isConnectedOrConnecting();
    }



}
