package com.example.calendarv2;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivityTest extends TestCase {
    @Test
    public void testOnCreate() {
        RealmConfiguration configuration;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<EventEntity> realmResults = realm.where(EventEntity.class).equalTo("id", 1).findAll();
        assertEquals(realmResults.size() > 0, true);
    }

    @After
    public void afterMethod() {
        System.out.println("Test finished");
    }


}