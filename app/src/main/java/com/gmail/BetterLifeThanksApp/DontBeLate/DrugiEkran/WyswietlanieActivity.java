package com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

//import com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran.funkcyjne.DFL;
import com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran.funkcyjne.TryGoogleListener;
import com.gmail.BetterLifeThanksApp.DontBeLate.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran.funkcyjne.DirectionFinder;
import com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran.funkcyjne.DirectionFinderListener;
import com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran.funkcyjne.Distance;

import org.w3c.dom.Text;

public class WyswietlanieActivity extends AppCompatActivity implements DirectionFinderListener,TryGoogleListener {


    private String godzina;
    private String adres;

    private LocationManager locationManager;
    private LocationListener locationListener;


    private String jednostki;
    private  WyswietlanieActivityManegment wam;








    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_displayman);
        //MobileAds.initialize(this,"tutaj bylo id");
        if(savedInstanceState!=null)
        {
            jednostki = savedInstanceState.getString("jednostki");

        }


        readvariables();
        mylocation();

    }

    /**
     * Metoda wywołuje pobiera dane dotyczące lokalizacji,prędkości i odległości użytkownika,a następnie je wyświetla
     */

    protected final void mylocation() {



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                wam.getObliczenia().roznicaCzasu(location);
                wam.getObliczenia().pobranieWspolrzednych(location);


                wam.getObliczenia().odlegloscRuchu(location);
                wam.getObliczenia().sredniaPredkosc();
                wam.getObliczenia().odlegloscOdCelu();
                wam.getObliczenia().sprawdzaGoogle(adres,WyswietlanieActivity.this);
                wam.getObliczenia().licze(godzina);
                wam.getObliczenia().prawidlowaPredkosciCzas();

                if(wam==null)
                {
                    wam = new WyswietlanieActivityManegment();
                    Log.i("InfoN","wam null1");
                }



                wam.getView().getWyswietlam().setText(getString(
                        R.string.display_up,
                                ObliczeniaLokalizacja.getTabDroga(),
                        ObliczeniaLokalizacja.getPredkosc(),
                        ObliczeniaLokalizacja.getPrawidlowa_predkosc(),
                        ObliczeniaLokalizacja.getIle_czasu(),
                        ObliczeniaLokalizacja.getIle_czasu_s(),
                        ObliczeniaLokalizacja.getJEDNOSTKI()
                ));

                jakieTempo(true,ObliczeniaLokalizacja.getPrawidlowa_predkosc());


                if(wam.getObliczenia().czyDziala())
                {

                    wam.getView().getWyswietlamG().setText(getString(
                            R.string.display_down,
                            ObliczeniaLokalizacja.getOdlegloscG(),
                            ObliczeniaLokalizacja.getpPredkoscG(),
                            (ObliczeniaLokalizacja.getOdlegloscG()* 60 / ObliczeniaLokalizacja.getPredkosc()),
                            ObliczeniaLokalizacja.getJEDNOSTKI()//TODO zobacz czy jednostki maja dobra wartosc
                    ));

                    jakieTempo(false, ObliczeniaLokalizacja.getpPredkoscG());
                }











            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                //Jeśli lokalizacja jest wyłączona to przenosi użytniwnika do ustawień Lokalizacji,tak by mógł ją włączyć
                Intent intent  = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        //Jeśli to mozliwe w tej wersji Androida to przyznajemy uprawnienia
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                },10);
            }
            //return;
        }else
        {
            configureButton();
        }

    }



    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 10:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    //jeśli uprawnienia zostały przyznane to idziemy do ponizszej funkcji
                    configureButton();
        }
    }

    /**
     * Metoda ustawia listener na "locationListener" w metodzie mylocation() który będzie wywoływany jeśli użytnownik przemieści się o przynajmniej 4 metry
     */

    protected final void configureButton() {

        locationManager.requestLocationUpdates("gps",0,4, locationListener);//Tak to dziala= jakis string,co ile milisec ma sie aktualizowac,co ile metrow,podajlistener

    }

    /**
     * Metoda odczytuje (przekazane przez wcześniejszą aktywność) wartości i przekształca je
     */
    protected void readvariables() {
        Intent receiveIntent = this.getIntent();
        double szerokosc= receiveIntent.getDoubleExtra("sz",0.00);
        double dlugosc = receiveIntent.getDoubleExtra("dl",0.00);
        adres = receiveIntent.getStringExtra("ad");
        godzina = receiveIntent.getStringExtra("g");

        String format = receiveIntent.getStringExtra("f");
        jednostki = receiveIntent.getStringExtra("j");

        if(format.equals(" AM") || format.equals(" pm"))
        {
            godzina+=format;
        }

        double zamiana = 0.001;//zamiana metrow na km
        if(!jednostki.equals("km"))
        {
            zamiana = 1.093613;//zamiana metrow na yardy
        }

        Intent intent = new Intent(this,WyswietlanieActivityView.class);
        startActivity(intent);


        wam  = new WyswietlanieActivityManegment(szerokosc,dlugosc,zamiana,jednostki);


        if(wam.getObliczenia().czyDziala())
        {
            wam.getView().getWyswietlamG().setTextSize(18);
            wam.getView().getInformacja().setText("");
        }

        wam.getObliczenia().licze(godzina);

    }

    /**
     * Zapisuje zmienne,które zaraz znikną
     * @param outState zapisany stan np obiektów
     */
    @Override
    protected final void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("jednostki",jednostki);
    }


    protected final void jakieTempo(boolean tempo,double prawidlowaPredkosc)
    {
        if(wam==null)
        {
            wam = new WyswietlanieActivityManegment();
            Log.i("InfoN","wam null2");
        }


        if(tempo) {
            if (prawidlowaPredkosc < ObliczeniaLokalizacja.getPredkosc()) {
                wam.getView().getTempo().setText(R.string.dobre_tempo);
            } else {
                wam.getView().getTempo().setText(R.string.za_wolno);
            }
        }
        else
        {
            if (prawidlowaPredkosc < ObliczeniaLokalizacja.getPredkosc()) {
                wam.getView().getTempoG().setText(R.string.dobre_tempo);
            } else {
                wam.getView().getTempoG().setText(R.string.za_wolno);
            }
        }

    }





    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(Distance dystans) {
       ObliczeniaLokalizacja.setOdlegloscG(dystans.value);//Ustawia jaki dystans wyznaczyło google
        FinderSucessKoniec();
    }



    public final void FinderSucessKoniec()
    {

        ObliczeniaLokalizacja.setGoogleDziala(true);


        if(wam ==null)
        {
            wam = new WyswietlanieActivityManegment();
        }

        wam.getView().getWyswietlamG().setTextSize(18);

        wam.getView().getInformacja().setText("");



        if(wam.getObliczenia()!=null) {
            Log.i("InfoN","obliczenia nie sa null");
            if (wam.getObliczenia().czyDziala())
            {
                Log.i("InfoN", "Zaraz mam wyswietlac");


                if(wam== null)
                {
                    wam = new WyswietlanieActivityManegment();
                    Log.i("InfoN","wam null3");
                }


                if(ObliczeniaLokalizacja.getPredkosc()==0.0)
                {
                    ObliczeniaLokalizacja.setPredkosc(0.000001);//TODO tymczasowe aaby uniknac dzielenia przez 0 ponizej(jesli chcesz uniknac lepiej zobic to w metodzie get ze jezeli jest 0.00 to zwraca 0.00000001
                }

                if(wam.getView()==null)
                {
                    Log.i("InfoN","wam.getView() null");
                }
                if(wam.getView().getWyswietlamG()==null)
                {
                    Log.i("InfoN","wam.getView().getWyswietlamG null");
                }
                if(wam.getView().getWyswietlam()==null)
                {
                    Log.i("InfoN","wam.getView().getWyswietlam null");
                }


                wam.getView().getWyswietlamG().setText(getString(
                        R.string.display_down,
                        ObliczeniaLokalizacja.getOdlegloscG(),
                        ObliczeniaLokalizacja.getpPredkoscG(),
                    ObliczeniaLokalizacja.kiedyBedzieszGoogle(),
                        ObliczeniaLokalizacja.getJEDNOSTKI()
                ));

                jakieTempo(false, ObliczeniaLokalizacja.getpPredkoscG());
            }
        }




        else
        {
            Log.i("InfoN","sa nullem");



          if(new ObliczeniaLokalizacja().czyDziala()) {

              if(wam == null)
              {
                  wam = new WyswietlanieActivityManegment();
              }



              if (ObliczeniaLokalizacja.getPredkosc() == 0.0) {
                  ObliczeniaLokalizacja.setPredkosc(0.000001);//TODO tymczasowe aaby uniknac dzielenia przez 0 ponizej(jesli chcesz uniknac lepiej zobic to w metodzie get ze jezeli jest 0.00 to zwraca 0.00000001
              }




              //test nr 3
              if (wam != null) {
                  new ObliczeniaLokalizacja();
                  wam.getView().getWyswietlamG().setText(getString(
                          R.string.display_down,
                          ObliczeniaLokalizacja.getOdlegloscG(),
                          ObliczeniaLokalizacja.getpPredkoscG(),
                          ObliczeniaLokalizacja.kiedyBedzieszGoogle(),
                          ObliczeniaLokalizacja.getJEDNOSTKI()
                  ));


              }





              }


              //jakieTempo(false, ObliczeniaLokalizacja.getpPredkoscG());
          }

    }







    /**
     * Metoda wysyła adres do klasy DirectionFinder
     * @param adres czyli ulica i numer domu.
     */

    @Override
    public void onTryGoogleSuccess(String adres) {
        ObliczeniaLokalizacja.setPierwszy(false);

        String origin =String.valueOf(ObliczeniaLokalizacja.getX_now())+","+String.valueOf(ObliczeniaLokalizacja.getY_now());

        try {
            new DirectionFinder(this, origin, adres).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }




}
