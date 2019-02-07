package com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran.funkcyjne.TryGoogleListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ObliczeniaLokalizacja {


    private static long time1 = 2;
    private static long time2 = 1;
    private static long r_time;
    private static double x_now = 1.0;
    private static double y_now = 1.0;
    private static double od_x;
    private static double od_y;
    private static double czasik;
    private static double droga;
    private static Location wczesniejsza = null;
    private static double predkosc;
    private static double ZAMIANA;
    private static int i = 1;
    private static double srednia_predkosc = 0.0;
    private static double mSzerokosc, mDlugosc;
    private static float[] tabDroga = new float[4];//chyba [3]
    private static double prawidlowa_predkosc;
    private static double roznica;
    private static boolean pierwszy=true;
    private static double ile_czasu;
    private static double ile_czasu_s;
    private static long r_przed;
    private static double odlegloscG;
    private static double roznicaOdleglosci=0.0;
    private static boolean googleDziala=false;
    private static double pPredkoscG;
    private static String JEDNOSTKI;
    private static int test;
    //private TryGoogleListener listener;


    protected ObliczeniaLokalizacja(){}

    protected ObliczeniaLokalizacja(double szerokosc, double dlugosc,double zamiana,String jednostki) {

        mSzerokosc = szerokosc;//szerokość geograficzna adresu który użytnownik podał w pierwszym ekranie
        mDlugosc = dlugosc;//Długość geograficzna adresu który użytnownik podał w pierwszym ekranie
        ZAMIANA = zamiana;
        JEDNOSTKI = jednostki;
        test=1;
    }


    /**
     * Liczenie różnicy czasu między ostatnim odczytem lokalizacji
     *
     * @param location - aktualna lokalizacja
     */
    protected void roznicaCzasu(Location location) {
        time1 = location.getTime(); //pobiera czas
        r_time = time1 - time2;     //liczy roznice miedzy ostatnim mierzeniem czas i akualnym
        time2 = time1;                //ustalamy ze "ostatni czas" to aktualny czas
    }

    /**
     * Pobranie współrzędnych(szerokości i długości geograficznej)
     *
     * @param location - aktualna lokalizacja
     */
    protected void pobranieWspolrzednych(Location location) {
        od_x = x_now; //wspolrzedne wczesniejsze =ostatnim wspolrzednym
        od_y = y_now;

        x_now = location.getLatitude(); //pobranie aktualnych wspolrzednych
        y_now = location.getLongitude();
    }

    /**
     * Odleglość od ostatniego odczytu Lokalizacji
     *
     * @param location aktualna lokalizacja
     */
    protected void odlegloscRuchu(Location location) {
        if (wczesniejsza != null)
        {
            droga = location.distanceTo(wczesniejsza);
            czasik = (double) r_time / 3600000; //zamiana longa na double milisekund na godziny
            droga *=ZAMIANA;
            predkosc = droga / czasik;
        }
                /*else
                {
                    wczesniejsza=location;
                    onLocationChanged(location);
                }*/

        wczesniejsza = location;
    }

    /**
     * Liczeniw sredniej predkosci z jaką porusza sie uzytkownik aplikacji
     */
    protected void sredniaPredkosc() {
        if (i < 7) //dlaczego <6 ? Bo srednia predkosc bedzie sie aktualizowala co minute
        {
            srednia_predkosc = (srednia_predkosc + predkosc) / i + 1;// test //dlatego ze bierzemy ostatnia predkosc jeszcze (wczesniej bylo /i a nie /i+1 ale to dlatego ze ponizej jest srednia_predkosc=predkosc a nie srednia_predkoc=0.0
            ++i;
        } else {
            i = 1;
            srednia_predkosc = predkosc;// test testowo wczesniej bylo =0.0
            // zobacz co by sie stalo jak bys w tym miejscu napisal continue albo funkcja goto/przerywajaca  po to by uzytkownik nie wysylal zerowej predkosci
        }
    }

    /**
     * Odleglosc uzytkownika od miejsca docelowego
     *
     */
    protected void odlegloscOdCelu() {

        Location.distanceBetween(mSzerokosc, mDlugosc, x_now, y_now, tabDroga);//tabDroga[0] zwraca wynik w metrach

        tabDroga[0] *= ZAMIANA;
    }

    /**
     * Sprawdza czy pierwszy raz jest wywoływana usługa google,jeśli tak,uruchamia funkcję klasy WyswietlanieActivity która wyszukuje w google adresu
     * @param adres adres miejsca docelowego
     */
        public void sprawdzaGoogle(String adres,TryGoogleListener listener)
        {


            if(pierwszy)
            {

                listener.onTryGoogleSuccess(adres);
                //new WyswietlanieActivity().theTryGoogle(adres);

            }
        }


    /**
     * Sprawdza czy funkcja dziala,jeśli tak to liczy odległość(według Google) i prędkość z jaką masz się poruszać
     * @return wartość logiczną czy usługa Google działa
     */

    protected boolean czyDziala()
        {

        if (googleDziala) {

            //odlegloscG = tabDroga[0] + roznicaOdleglosci;

            //odlegloscG = odleglosc+roznicaOdleglosci;

            //roznicaOdleglosci += (sqrt(pow(od_x - x_now, 2) + pow(cos(((x_now * PI) / 180)) * (od_y - y_now), 2))) * (40075.704 / 360); //liczy ile lacznie przebyles km
            float[] tabIleLaczniePrzebyles = new float[4];
            if(od_x!=1.0) {
                Location.distanceBetween(od_x, od_y, x_now, y_now, tabIleLaczniePrzebyles);
                tabIleLaczniePrzebyles[0]*=ZAMIANA;
            }
            else
            {
                tabIleLaczniePrzebyles[0]=0.0f;
            }

            //roznicaOdleglosci+=(double) tabIleLaczniePrzebyles[0];//tak naprawde zmienna "roznica odleglosci" to jest to ile łącznie przebyles

            //odlegloscG-=roznicaOdleglosci;//odejmujemy tyle ile przebyłeś km od odeglosci wyznaczonej przez Google (dziala jesli idziesz najkrótszą trasą według google)
            odlegloscG-=(double) tabIleLaczniePrzebyles[0];//odejmujemy odleglosc wyznaczoną przez google od tego ile przed chwilą przeszliśmy

            //odlegloscG -= roznicaOdleglosci; //odleglosc policzona poczatkowo przez Google = tej odleglosci - tyle km ile pokonales(dziala jesli:idziesz najkrotsza trasa wedlug google)

            //wyswietlamG.setText("Jeśli idziesz najkrótszą trasą według Google to jesteś od celu " + df2.format(odlegloscG) + "km\nPowinieneś się poruszać ze stałą prędkością " + df2.format(odlegloscG / roznica) + "km/h\nZ aktualną prędkością będziesz za " + df2.format(odlegloscG * 60 / predkosc) + " minut/y");


            pPredkoscG = odlegloscG / roznica;

            return true;
        }
            return false;

    }

    /**
     * Liczy prawidłową prędkość z jaką użytnownik powinien się poruszać
     * i w jakim czasie użytniwnik przybędzie jeśli będzie się poruszał w obecnym tempie
     */
    protected final void prawidlowaPredkosciCzas()
    {

        prawidlowa_predkosc = tabDroga[0]/roznica; //predkoc z jaka pownienes isc/jechac to odleglosc od miejsca dolecowego/roznice czasu pomiedzy aktualna godzina a planowana

        ile_czasu=(tabDroga[0]*60)/predkosc;
        ile_czasu_s=(tabDroga[0]*60)/srednia_predkosc;//wprowadzone *60
    }

    /**
     * Funkcja liczy różnicę pomiędzy godziną aktualną a docelową
     * @param godzina zwraca różnicę czasu w godzinach
     */

    protected void licze(String godzina) {
        SimpleDateFormat format = new SimpleDateFormat("hh.mm.ss");
        try {
            Date planowana = format.parse(godzina);
            Date now = new Date();
            String now_s = format.format(now);
            Date druga = format.parse(now_s);

            r_przed = Math.abs(planowana.getTime()- druga.getTime());

        } catch (ParseException e) {
            //wyswietlam.setText(R.string.zla_godzina);
            e.printStackTrace();
        }


        roznica = (double) (r_przed)/3600000;

    }

    protected static void setPierwszy(boolean pierwszy) {
        ObliczeniaLokalizacja.pierwszy = pierwszy;
    }

    protected static double getpPredkoscG() {
        return pPredkoscG;
    }

    protected static double getOdlegloscG() {
        return odlegloscG;
    }



    protected static double getPredkosc() {
        return predkosc;
    }

    protected static double getPrawidlowa_predkosc() {
        return prawidlowa_predkosc;
    }

    protected static double getIle_czasu() {
        return ile_czasu;
    }

    protected static double getIle_czasu_s() {
        return ile_czasu_s;
    }

    protected static float getTabDroga() {
        return tabDroga[0];
    }



    protected static void setGoogleDziala(boolean googleDziala) {
        ObliczeniaLokalizacja.googleDziala = googleDziala;
    }



    protected static int getI() {
        return i;
    }

    protected static void setI(int i) {
        ObliczeniaLokalizacja.i = i;
    }

    protected static double getX_now() {
        return x_now;
    }

    protected static double getY_now() {
        return y_now;
    }

    public static void setOdlegloscG(double odlegloscG) {
        ObliczeniaLokalizacja.odlegloscG = odlegloscG;
    }

    public static String getJEDNOSTKI() {
        return JEDNOSTKI;
    }


    public static void setPredkosc(double predkosc) {
        ObliczeniaLokalizacja.predkosc = predkosc;
    }

    public static double kiedyBedzieszGoogle()
    {
        return (odlegloscG * 60 / predkosc);
    }



}
