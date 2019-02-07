package com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran.funkcyjne;

/**
 * Created by Maciej on 21.02.2018.
 */

public class Distance {
    public String text;
    public final double value; //zwraca wynik w metrach

    public Distance(String text, double value) {
        this.text = text;
        this.value = value/1000;//przekształcam na KM
    }

    public double getValue() {
        return value;
    }
}
