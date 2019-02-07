package com.gmail.BetterLifeThanksApp.DontBeLate.PierwszyEkran;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;



public class ZapisOdczytDoZpliku {

    /////////////////////////////////////////////////////////////
    private final String NAZWA_PLIKU;
    private final int LENGTH_OFFER;
    private static String[] listaAdresow;
    private final Context context;
/////////////////////////////////////////////////////////////////

    protected ZapisOdczytDoZpliku(Context context,String NAZWA_PLIKU,int LENGTH_OFFER) {
        this.context = context;
        this.NAZWA_PLIKU=NAZWA_PLIKU;
        this.LENGTH_OFFER = LENGTH_OFFER;
        if(listaAdresow==null)
        {
            listaAdresow = new String[LENGTH_OFFER];
        }
    }


    /**
     * Odczytuje (z pliku) wyszukiwane (w przeszłości) adresy(ulica,Miejscowość) i zapisuje je do tablicy.
     */

    protected void odczytPlikuDoTablicy(){

        //boolean istnieje = new File(NAZWA_PLIKU).isFile();

       //if(istnieje) {

            try {
                InputStream inputStream = context.openFileInput(NAZWA_PLIKU);//Attempt to invoke virtual method 'java.io.FileInputStream android.content.Context.openFileInput(java.lang.String)' on a null object reference

                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    int i = 0;
                    String line;
                    while (i < LENGTH_OFFER && (line = bufferedReader.readLine()) != null)//Odczyt z pliku dopóki 'i' mniejsze od dlugosci tablicy lub dopóki plik się nie skończy
                    {
                        listaAdresow[i] = line;
                        ++i;
                    }

                    inputStream.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
       // }


        plikNiewypelniony();


    }

    /**
     *   Jesli  w pliku nie bylo 11 pozycji(np.byly puste) - to uzupelnia tablice zeby byla pelna(uzywane raczej tylko za pierwszym dzialaniem aplikacji,lub gdyby ktoś usunął plik tekstowy)
     */

    private  void plikNiewypelniony() {

        String domyslnyAdres="Zlota 44,Warsaw";

        for(int i=0;i<LENGTH_OFFER;i++)
        {


            if(listaAdresow[i].equals("null") || listaAdresow==null)
            {
                listaAdresow[i]=domyslnyAdres;  //Jesli jakies elementy tablicy sa puste to wypelniam je domyslnym adresem
            }


        }

    }


    protected void zapisTablicyDoPliku(String strAddress){


        InputStream inputStream = null;
        try {
            inputStream = context.openFileInput(NAZWA_PLIKU);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (inputStream != null) {

            try {

                boolean takiSam = false;


                for (int i = 0; i < 10; i++) {
                    if (listaAdresow[i].equals(strAddress)) { //Czy element tablicy jest taki sam jak strAddress(to co przed chwilą wprowadziłem w pole?)

                        takiSam = true;
                        break;
                    }
                }

                if (!takiSam) {

                    System.arraycopy(listaAdresow, 0, listaAdresow, 1, 10);//Jeśli element wprowadzony do EditText nie jest taki sam jak jakikolwiek w tablicy to zapisuję do pliku to co wprowadzilem i przesuwam miejsca w tablicy ten wprowadzone wartosci o 1
                    listaAdresow[0] = strAddress;
                }
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(NAZWA_PLIKU, Context.MODE_PRIVATE));
            for (int i = 0; i < LENGTH_OFFER; i++) {
                outputStreamWriter.write(listaAdresow[i] + "\n"); //Zapisuję do pliku całą zawartość tablicy
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();

        }


    }

    /**
     *
     * @return lista adresów(Miejscowość,adres) pobraną wcześniej z pliku
     */
    protected String[] getListaAdresow() {
        return listaAdresow;
    }
}
