package com.gmail.BetterLifeThanksApp.DontBeLate.PierwszyEkran;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran.WyswietlanieActivity;
import com.gmail.BetterLifeThanksApp.DontBeLate.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity  {


    private AutoCompleteTextView aCtv;
    private EditText godzina;
    private double s;
    private double d;
    private static final int LENGTH_OFFER = 11;
    private Context context=this;
    private final String NAZWA_PLIKU="etValues.txt"; // Nazwa pliku z której odczytuję i do której zapisuję dane(adresy)
    String format="";
    String jednostki="km";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        godzina =  findViewById(R.id.godzina);
        aCtv =  findViewById(R.id.autoComplete);


        zaladowanieAdresow();


    }


    /**
     * Funkcja jeszcze do dopracowania... Obecnie sprawdza: Stan sieci,wyszukuje wprowadzony przez Ciebie (wcześniej) adres w internecie pobierając jego współrzędne geograficzne i przekazuje je do następnej aktywności.Podziel to na klasy i rozróżnij czy ktooś nie ma internetu czy po prostu wpisałzły adres
     * Funkcja wywowyłana gdy kliknę Button "Jak szybko muszę iść"
     * @param view w ogóle nie używany
     *
     */

    public void klik(View view) {

        String godzina_s = godzina.getText().toString();
        String strAddress = aCtv.getText().toString(); // wczesniej bylo adres teraz actv



        if(!strAddress.isEmpty()) {


            boolean poprawny;


            TwojePolaczenie polaczenie = new TwojePolaczenie(this);
            boolean isConnected = polaczenie.isConnected();



            if (isConnected) {

                List<Address> address;

                try {


                    Geocoder coder = new Geocoder(this);
                    //Ponizej czasem zwraca blad,poniewaz byc moze masz za malo pamieci i jest usuwany NetworkLocator.
                    // Więc musisz włączyć usługę NetworkLocationService(która jest domyślnie włączana podczas uruchomienia telefonu).
                    //Najprostszym rozwiązaniem tego problemu jest po prostu zrestartowanie telefonu
                    //Trochę innym rozwiązaniem jest przeanalizowanie i zastosowanie klasy getFromLocationName()
                    address= coder.getFromLocationName(strAddress, 1);//Najlepiej uzyj if to .isPresent() (metoda w klasie Geocoder) to wtedy wykonaj tą linijkę inaczej wyswietl blad

                    if (!address.isEmpty()) {
                        Address location = address.get(0);

                        s = location.getLatitude();
                        d = location.getLongitude();

                        poprawny=true;


                    } else {
                      poprawny = false;
                        Toast.makeText(this, R.string.adres_niepoprawny, Toast.LENGTH_SHORT).show();
                        return;
                    }


                } catch (IOException e1) {
                    poprawny = false;//TU ZMIANA //tu nas wyrzuca gdy mamy zle dane
                }


                if (poprawny) {
                    if (!godzina_s.isEmpty()) {





                        ZapisOdczytDoZpliku zapis = new ZapisOdczytDoZpliku(context,NAZWA_PLIKU,LENGTH_OFFER);
                        zapis.zapisTablicyDoPliku(strAddress);






                            godzina_s+=".00";



                        Intent intent = new Intent(this, WyswietlanieActivity.class);

                            intent.putExtra("sz", s);
                            intent.putExtra("dl", d);
                            intent.putExtra("ad", strAddress);
                            intent.putExtra("g", godzina_s);
                            intent.putExtra("f",format);
                            intent.putExtra("j",jednostki);
                            startActivity(intent);



                    } else {
                        Toast.makeText(this, R.string.podaj_godzine, Toast.LENGTH_SHORT).show();
                    }

               } else {
                    Toast.makeText(this, R.string.internet_connected, Toast.LENGTH_SHORT).show();
                }

            }
            else
            {
                Toast.makeText(this,R.string.internet,Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(this, R.string.podaj_adres, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Funkcja sprawdza który format godzinowy wybrał użytkownik
     * @param view -RadioButtony które mogą oznaczać :
     *             -format 24-godzinny
     *             -format(godziny) AM
     *             -format(godziny) PM
     */

    public void formatGodziny(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){

            case R.id.h24:
                if(checked) {
                    godzina.setHint(R.string.kiedyDwaCztery);
                    format="";
                }
                    break;

            case R.id.am:
                if(checked) {
                    godzina.setHint(R.string.kiedy);
                    format=" AM";
                }
                    break;

            case R.id.pm:
                if(checked) {
                    godzina.setHint(R.string.kiedy);
                    format=" pm";
                }
                    break;

        }
    }

    /**
     * Funkcja wstawia do zmiennej adapter odczytaną wcześniej listę adresów(Miejscowość,ulicę),
     * następnie ustawia adapter dla zmiennej klasy AutoCompleteTextView(ta klasa odpowiedzialna jest za auto-uzupełnianie tekstu)
     */

    private void zaladowanieAdresow(){

           ZapisOdczytDoZpliku odczyt = new ZapisOdczytDoZpliku(context,NAZWA_PLIKU,LENGTH_OFFER);
            odczyt.odczytPlikuDoTablicy();


            ArrayAdapter <String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,odczyt.getListaAdresow());

            aCtv.setThreshold(1);
            aCtv.setAdapter(adapter);



        }

    /**
     * Funkcja sprawdza jakie jednostki długości zaznaczył użytkownik
     * @param view radiobutton oznaczający kilometry lub radiobutton oznaczający Stopy/Mile/Yardy
     */

    public void jednostkiDlugosci(View view) {

        boolean checkedKmFt = ((RadioButton) view).isChecked();

        switch (view.getId()){

            case R.id.km:
                if(checkedKmFt) {
                    jednostki = "km";
                }
                break;

                case R.id.yards:
                    if(checkedKmFt) {
                        jednostki = "yards";
                    }
                break;

        }

    }



}//close class

