package com.example.calendarv2;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Configuration of Realm database
 */
public class RealmDBApplication extends Application {
    /**
     * @param REALM_NAME name of the database file
     */
    private final String REALM_NAME = "myrealm.realm";

    @Override
    public void onCreate() {

        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
