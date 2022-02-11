package com.example.calendarv2;

import android.view.View;

import androidx.annotation.NonNull;

public interface IClickDeleteListener {
    void deleteEvent(@NonNull View view, int id);
}
