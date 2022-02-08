package com.example.calendarv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;


public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> date_start = new ArrayList<>();
    ArrayList<String> date_finish = new ArrayList<>();
    ArrayList<String> description = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    Realm realm;
    Calendar calendar;
    RealmAdapter realmAdapter;
    private CalendarView calendarView;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        init();
        checkBD();
        realmAdapter = new RealmAdapter( events);
        recyclerView.setAdapter(realmAdapter);
        RealmConfiguration configuration;
        refreshList();
    }

    private void init() {
        realm = Realm.getDefaultInstance();
        recyclerView = findViewById(R.id.recyclerView);
        calendarView = findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();
        calendarView.setFirstDayOfWeek(2);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth, 0, 0, 0);
                refreshList();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshList();
            }
        });
    }

    public void refreshList() {
        events.clear();
        RealmResults<Event> realmResults = realm.where(Event.class).between("dateStart", calendar.getTimeInMillis(), (calendar.getTimeInMillis() + 86400000)).findAll();
        events.addAll(realmResults);
        realmAdapter.notifyDataSetChanged();
    }

    public void checkBD() {
        RealmResults<Event> realmResultsStart = realm.where(Event.class).findAll();
        if (realmResultsStart.size() == 0) {
            try {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(JsonDataFromAssets()));
                JSONArray jsonArray = jsonObject.getJSONArray(getString(R.string.events));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject eventData = jsonArray.getJSONObject(i);

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Number maxId = realm.where(Event.class).max(getString(R.string.id));
                            int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                            Event event = realm.createObject(Event.class, nextId);
                            try {
                                event.setName(eventData.getString(getString(R.string.name)));
                                event.setDescription(eventData.getString(getString(R.string.description)));
                                event.setDateStart(eventData.getLong(getString(R.string.date_start)));
                                event.setDateFinish(eventData.getLong(getString(R.string.date_finish)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private String JsonDataFromAssets() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("events.json");
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {// Supporting ActionBar
        getMenuInflater().inflate(R.menu.menuadd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Supporting Add button
        // Handle item selection
        if (item.getItemId() == R.id.action_btn) {
            onClickAdd();
            return true;
        } else
            return super.onOptionsItemSelected(item);

    }

    private void onClickAdd() {

        Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
        intent.putExtra("Day", calendar.get(Calendar.DAY_OF_MONTH));
        intent.putExtra("Month", calendar.get(Calendar.MONTH));
        intent.putExtra("Year", calendar.get(Calendar.YEAR));
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshList();
    }
}
