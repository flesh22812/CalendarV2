package com.example.calendarv2;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmDBApplication extends Application {
    private Object RealmConfiguration;
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
