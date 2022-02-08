package com.example.calendarv2;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.example.calendarv2.R.string.myrealm;

public class RealmDBApplication extends Application {
    private Object RealmConfiguration;

    @Override
    public void onCreate() {

        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(getString(myrealm))
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
