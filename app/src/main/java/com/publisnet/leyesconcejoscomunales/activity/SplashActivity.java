package com.publisnet.leyesconcejoscomunales.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.publisnet.leyesconcejoscomunales.AppConfig;
import com.publisnet.leyesconcejoscomunales.R;
import com.publisnet.leyesconcejoscomunales.database.Ley;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private Intent mainIntent;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean first_time  = sharedPref.getBoolean(AppConfig.SPLASH_FIRST_TIME, true);

        mainIntent = new Intent(getApplicationContext(), MainActivity.class);

        if(!first_time){
            startActivity(mainIntent);
            finish();
            return;
        }


        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(AppConfig.SPLASH_FIRST_TIME, false);
                editor.apply();

                startActivity(mainIntent);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, AppConfig.SPLASH_DELAY);


        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();

        // Clear the realm from last time
        Realm.deleteRealm(realmConfiguration);

        // Create a new empty instance of Realm
        realm = Realm.getInstance(realmConfiguration);
        loadLeys();
    }
    private void loadLeys() {
        // In this case we're loading from local assets.
        // NOTE: could alternatively easily load from network
        InputStream stream;
        try {
            stream = getAssets().open("ley.json");
        } catch (IOException e) {
            return;
        }

        Gson gson = new GsonBuilder().create();

        JsonElement json = new JsonParser().parse(new InputStreamReader(stream));
        List<Ley> leyes = gson.fromJson(json, new TypeToken<List<Ley>>() {}.getType());

        // Open a transaction to store items into the realm
        // Use copyToRealm() to convert the objects into proper RealmObjects managed by Realm.
        realm.beginTransaction();
        Collection<Ley> realmCities = realm.copyToRealm(leyes);
        realm.commitTransaction();


    }

    /*Gson gson = new Gson();
        Ley[] lista = gson.fromJson("",Ley[].class);

        Ley ley = new Ley();
        ley.titulo="Prueba1";
        ley.descripcion = "mariano";

        realm.beginTransaction();
        final Ley ley1 = realm.copyToRealm(ley); // Persist unmanaged objects

        realm.commitTransaction();*/
}