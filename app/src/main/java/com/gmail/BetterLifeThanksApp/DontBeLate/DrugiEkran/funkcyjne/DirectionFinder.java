package com.gmail.BetterLifeThanksApp.DontBeLate.DrugiEkran.funkcyjne;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
//import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;



public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination) {
        this.listener = listener;
        this.origin = origin;//wspolrzedne geograficzne
        this.destination = destination;//adres (miasto,ulica,numer domu)
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    /**
     * Utworzenie adresu URL pod jaki mamy się udać
     * @return adres URL
     * @throws UnsupportedEncodingException gdy nie będziemy mogli odkodować (np nie możemy ustawić utf-8)
     */
    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");

        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&key=" + GOOGLE_API_KEY;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {


        /**
         * Pobieramy dane ze strony
         * @param params link pod jaki masz się udać aby otrzymać informacje (zostają zwrócone w JSONie)
         * @return informacje odebrane z adresu strony (URL)
         */
        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            //} catch (MalformedURLException e) {
               // e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Odczytanie danych wyświetonych w formacie JSON i przekazanie dystansu(dzielącego nas od celu) do metody onDirectionFinderSuccess
     * @param data Dane pobrane ze strony
     * @throws JSONException gdy wystąpi problem z formatem JSON
     */
    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");

        JSONObject jsonRoute = jsonRoutes.getJSONObject(0);


        JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
        JSONObject jsonLeg = jsonLegs.getJSONObject(0);
        JSONObject jsonDistance = jsonLeg.getJSONObject("distance");


        Distance distance = new Distance(jsonDistance.getString("text"), jsonDistance.getDouble("value"));


        listener.onDirectionFinderSuccess(distance);
    }


}
