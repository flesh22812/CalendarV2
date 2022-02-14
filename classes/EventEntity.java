package com.example.calendarv2;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Event entity description
 */
public class EventEntity extends RealmObject {
    @PrimaryKey
    private int id;
    /**
     * @param dateStart time of beginning event in timestamp format
     */
    private long dateStart;
    /**
     * @param dateFinish time of endding event in timestamp format
     */
    private long dateFinish;
    private String description;
    @Required
    private String name;


    EventEntity(int id, long dateStart, long dateFinish, String name, String description) {
        this.id = id;
        this.dateStart = dateStart;
        this.dateFinish = dateFinish;
        this.name = name;
        this.description = description;

    }

    public EventEntity() {

    }

    @NonNull
    public int getId() {
        return id;
    }

    @NonNull
    public long getDateStart() {
        return dateStart;
    }

    @NonNull
    public long getDateFinish() {
        return dateFinish;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateStart(long dateStart) {
        this.dateStart = dateStart;
    }

    public void setDateFinish(long dateFinish) {
        this.dateFinish = dateFinish;

    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }
}
