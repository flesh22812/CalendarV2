package com.example.calendarv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
/** @see AddEventActivity is for adding new event from calendar*/
public class AddEventActivity extends AppCompatActivity {
    private EditText editDescrip, editName;
    private Button btnAdd;
    private TimePicker timePickerS, timePickerF;
    private Realm mRealm;
    private int hourStart, minuteStart, hourFinish, minuteFinish;
    private int day, month, year;
    private RecyclerView recyclerView;
    private RealmAdapter realmAdapter;
    private List<EventEntity> events = new ArrayList<>();
    /** This constants needs for default values of intent*/
    private final int DEFAULT_YEAR = 1970, DEFAULT_DAY = 1, DEFAULT_MONTH = 1,INDEX = 1 ;


    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        this.setRequestedOrientation(SCREEN_ORIENTATION_UNSPECIFIED);
        setTitle(getString(R.string.new_contact));
        mRealm = Realm.getDefaultInstance();
        init();
    }

    /** initialization of AddEventActivity */
    private void init() {
        editDescrip = findViewById(R.id.editDescrip);
        editName = findViewById(R.id.editName);
        btnAdd = findViewById(R.id.btnAdd);
        timePickerS = findViewById(R.id.timePickerS);
        timePickerS.setIs24HourView(true);
        timePickerF = findViewById(R.id.timePickerF);
        timePickerF.setIs24HourView(true);
        btnAdd.setEnabled(false);
        EditText[] edList = {editName};
        BlockEditText textWatcher = new BlockEditText(edList, btnAdd);
        for (EditText editText : edList) editText.addTextChangedListener(textWatcher);
        Intent intent = getIntent();
        day = intent.getIntExtra(getString(R.string.day), DEFAULT_DAY);
        month = intent.getIntExtra(getString(R.string.month), DEFAULT_MONTH);
        year = intent.getIntExtra(getString(R.string.year), DEFAULT_YEAR);
    }
/** @see AddEventActivity#onClickSave(View)  saves data to database*/
    public void onClickSave(@NonNull View view) {

        hourStart = timePickerS.getHour();
        minuteStart = timePickerS.getMinute();
        hourFinish = timePickerF.getHour();
        minuteFinish = timePickerF.getMinute();
        /**Initialize timestamp from calendar date*/
        Timestamp timestampS = new Timestamp(year - 1900, month, day, hourStart, minuteStart, 0, 0);
        Timestamp timestampF = new Timestamp(year - 1900, month, day, hourFinish, minuteFinish, 0, 0);
        mRealm.executeTransactionAsync(new Realm.Transaction() {
                                           @SuppressLint("SyntheticAccessor")
                                           @Override
                                           public void execute(Realm realm) {
                                               Number maxId = realm.where(EventEntity.class).max(getString(R.string.id));
                                               int nextId = (maxId == null) ? INDEX : maxId.intValue() + INDEX;
                                               EventEntity event = realm.createObject(EventEntity.class, nextId);
                                               event.setName(editName.getText().toString());
                                               event.setDescription(editDescrip.getText().toString());
                                               event.setDateStart(timestampS.getTime());
                                               event.setDateFinish(timestampF.getTime());
                                           }
                                       },
                new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });

    }


}
