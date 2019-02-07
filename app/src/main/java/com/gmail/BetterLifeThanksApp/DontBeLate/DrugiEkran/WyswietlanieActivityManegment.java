package com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran;

import android.content.Context;

public class WyswietlanieActivityManegment {

    private static ObliczeniaLokalizacja obliczenia;
    private static WyswietlanieActivityView view;



    public WyswietlanieActivityManegment(double szerokosc, double dlugosc, double zamiana, String jednostki) {
        view = new WyswietlanieActivityView();
        obliczenia = new ObliczeniaLokalizacja(szerokosc,dlugosc,zamiana,jednostki);
    }

    public WyswietlanieActivityManegment(){

    }

    public ObliczeniaLokalizacja getObliczenia() {
        return obliczenia;
    }

    public WyswietlanieActivityView getView() {
        return view;
    }
}
