package com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.BetterLifeThanksApp.DontBeLate.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class WyswietlanieActivityView extends AppCompatActivity {

    private static TextView wyswietlam;//daj tu private
    private static TextView wyswietlamG;
    private static TextView informacja;
    private static TextView tempo;
    private static TextView tempoG;
    private static AdView mAdview;
    private static boolean active;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayman);
        Log.i("Siema","Siema");
        initializeVariables();
    }
/*
    public WyswietlanieActivityView(){}

    public WyswietlanieActivityView(String elo)
    {
        setContentView(R.layout.activity_displayman);
        initializeVariables();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);

    }
    */


    private void initializeVariables() {
        MobileAds.initialize(this,"ca-app-pub-3303420116746766~1597670594");
        wyswietlam =  findViewById(R.id.wyswietlam);
        wyswietlamG = findViewById(R.id.wyswietlamG);
        informacja = findViewById(R.id.informacja);
        tempo=findViewById(R.id.tempo);
        tempoG=findViewById(R.id.tempoG);
        mAdview=findViewById(R.id.adView);
    }

    public TextView getWyswietlam() {
        return wyswietlam;
    }

    public TextView getWyswietlamG() {
        if(wyswietlamG==null)
        {
            wyswietlamG = findViewById(R.id.wyswietlamG);
        }
            return wyswietlamG;

    }

    public TextView getInformacja() {
        return informacja;
    }

    public TextView getTempo() {
        return tempo;
    }

    public TextView getTempoG() {
        return tempoG;
    }



    @Override
    protected void onStart() {
        super.onStart();
        active=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active=false;
    }

    public static boolean isActive() {
        return active;
    }



}
