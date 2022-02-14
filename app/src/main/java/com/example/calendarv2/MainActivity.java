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
import static com.example.calendarv2.R.id;
import static com.example.calendarv2.R.layout;
import static com.example.calendarv2.R.string;


/**
 * Main activity of the program- calendar and recycler view.
 */
public class MainActivity extends AppCompatActivity implements IClickDeleteListener {
    private RecyclerView recyclerView;
    private List<EventEntity> events = new ArrayList<>();
    private Realm realm;
    private Calendar calendar;
    private RealmAdapter realmAdapter;
    private CalendarView calendarView;
    private final int TIME_ZERO = 0, INDEX = 1, MONDAY = 2;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        this.setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        init();
        checkBD();
        realmAdapter = new RealmAdapter(events);
        recyclerView.setAdapter(realmAdapter);
        RealmConfiguration configuration;
        refreshList();
    }

    /**
     * Initializing MainActivity
     */
    public void init() {
        realm = Realm.getDefaultInstance();
        recyclerView = findViewById(id.recyclerView);
        calendarView = findViewById(id.calendarView);
        calendar = Calendar.getInstance();
        calendarView.setFirstDayOfWeek(MONDAY);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("SyntheticAccessor")
            @Override
            /**@see MainActivity#init()#onSelectedDayChange(CalendarView, int, int, int)
             *  refresh recyclerView after click on the some date  */
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth, TIME_ZERO, TIME_ZERO, TIME_ZERO);
                refreshList();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication());
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    public void refreshList() {
        events.clear();
        RealmResults<EventEntity> realmResults = realm.where(EventEntity.class).between(getString(string.date_start), calendar.getTimeInMillis(), (calendar.getTimeInMillis() + 86400000)).findAll();
        events.addAll(realmResults);
        realmAdapter.notifyDataSetChanged();
    }

    /**
     * @see MainActivity#deleteEvent(View, int) deletes event from database by id
     */
    public void deleteEvent(@NonNull View view, int id) {
        Realm realmDelete = Realm.getDefaultInstance();
        RealmResults<EventEntity> realmResultsStart = realmDelete.where(EventEntity.class).equalTo("id", id).findAll();
        realmDelete.beginTransaction();
        realmResultsStart.deleteAllFromRealm();
        realmDelete.commitTransaction();
    }

    /**
     * @see MainActivity#checkBD()  checks size of database, and if size==0 add data from json file
     */
    public void checkBD() {
        RealmResults<EventEntity> realmResultsStart = realm.where(EventEntity.class).findAll();
        if (realmResultsStart.size() == 0) {
            try {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(JsonDataFromAssets()));
                JSONArray jsonArray = jsonObject.getJSONArray(getString(string.events));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject eventData = jsonArray.getJSONObject(i);

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Number maxId = realm.where(EventEntity.class).max(getString(string.id));
                            int nextId = (maxId == null) ? INDEX : maxId.intValue() + INDEX;
                            EventEntity event = realm.createObject(EventEntity.class, nextId);
                            try {
                                event.setName(eventData.getString(getString(string.name)));
                                event.setDescription(eventData.getString(getString(string.description)));
                                event.setDateStart(eventData.getLong(getString(string.date_start)));
                                event.setDateFinish(eventData.getLong(getString(string.date_finish)));
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

    /**
     * @see MainActivity#JsonDataFromAssets() Decoding data from events.json
     */
    private String JsonDataFromAssets() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open(getString(string.event_file));
            int sizeOfFile = inputStream.available();
            byte[] bufferData = new byte[sizeOfFile];
            inputStream.read(bufferData);
            inputStream.close();
            json = new String(bufferData, getString(string.decoder));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {// Supporting ActionBar
        getMenuInflater().inflate(R.menu.menuadd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//Supporting Add button
        /** Handle item selection*/
        if (item.getItemId() == id.action_btn) {
            onClickAdd();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    /**
     * @see MainActivity#onClickAdd()  starts AddEventActivity
     */
    private void onClickAdd() {

        Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
        intent.putExtra(getString(string.day), calendar.get(Calendar.DAY_OF_MONTH));
        intent.putExtra(getString(string.month), calendar.get(Calendar.MONTH));
        intent.putExtra(getString(string.year), calendar.get(Calendar.YEAR));
        startActivityForResult(intent, 1);
    }

    /**
     * Getting the activity result to refresh list
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshList();
    }
}
