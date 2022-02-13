package com.example.calendarv2;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Configuration of Realm database
 */
public class RealmDBApplication extends Application {
    private Object RealmConfiguration;
    /**
     * Name of database file
     */
    private final String RealmName = "myrealm.realm";

    @Override
    public void onCreate() {

        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(RealmName)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
