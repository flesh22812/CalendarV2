package com.example.calendarv2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

public class AddEventActivity extends AppCompatActivity {
    EditText editDescrip, editName;
    private Button btnAdd;
    private TimePicker timePickerS;
    private TimePicker timePickerF;
    Realm mRealm;
    int hourStart;
    int minuteStart;
    int hourFinish;
    int minuteFinish;
    int day, month, year;
    RecyclerView recyclerView;
    RealmAdapter realmAdapter;
    List<Event> events = new ArrayList<>();

    @Override             ///////// initialization of AddView
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        this.setRequestedOrientation(SCREEN_ORIENTATION_UNSPECIFIED);
        setTitle(getString(R.string.new_contact));
        mRealm = Realm.getDefaultInstance();
        init();
    }

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
        day = intent.getIntExtra("Day", 1);
        month = intent.getIntExtra("Month", 1);
        year = intent.getIntExtra("Year", 1970);
    }

    public void onClickSave(@NonNull View view) {

        hourStart = timePickerS.getHour();
        minuteStart = timePickerS.getMinute();
        hourFinish = timePickerF.getHour();
        minuteFinish = timePickerF.getMinute();
        Timestamp timestampS = new Timestamp(year - 1900, month, day, hourStart, minuteStart, 0, 0);
        Timestamp timestampF = new Timestamp(year - 1900, month, day, hourFinish, minuteFinish, 0, 0);
        mRealm.executeTransactionAsync(new Realm.Transaction() {
                                           @Override
                                           public void execute(Realm realm) {
                                               Number maxId = realm.where(Event.class).max("id");
                                               int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                                               Event event = realm.createObject(Event.class, nextId);
                                               event.setName(editName.getText().toString());
                                               event.setDescription(editDescrip.getText().toString());
                                               event.setDateStart(timestampS.getTime());
                                               event.setDateFinish(timestampF.getTime());
                                           }
                                       },
                new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });

    }


}
