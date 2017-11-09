package org.varonesoft.luke.ricodarlo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import org.varonesoft.luke.ricodarlo.Util.Log;
import org.varonesoft.luke.ricodarlo.components.RicordaloIntentService;
import org.varonesoft.luke.ricodarlo.database.models.Alert;

import java.util.Calendar;

import static org.varonesoft.luke.ricodarlo.Util.DateTimeFormatter.getDateTime;

public class EditorActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    // TAG
    private static final String TAG = EditorActivity.class.getSimpleName();

    // Alert Item
    private Alert mAlert;
    private Calendar localtime;

    // Fields
    private EditText mAlertName;
    private EditText mAlertDesc;
    private EditText mAlertDate;
    private EditText mAlertRptDays;
    private EditText mAlertRptHours;

    private boolean mModified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setResult(AppCompatActivity.RESULT_OK);

        // Init
        mModified = false;
        localtime = Calendar.getInstance();

        // Get Fields reference
        getFieldsReferences();

        // As it says
        getAlertFromIntent();

        // Sets EventClick
        setEventClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // SimplifiableIfStatement
        switch (id) {
            case R.id.action_save:
                if (checkToPerformSave()) {
                    getFields();
                    mAlert.save(this);
                    RicordaloIntentService.startActionCancelAlarm(this, mAlert);
                    RicordaloIntentService.startActionSetAlarm(this, mAlert);
                }
                setResult(RESULT_OK);
                finish();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * If the back button is pressed, cancel the edit or new
     * TODO should add a check to see if something changed
     */
    @Override
    public void onBackPressed() {

        Intent resultInt = new Intent();
        resultInt.putExtra("Result", "Done");
        setResult(RESULT_CANCELED, resultInt);

        super.onBackPressed();
        finish();
    }


    private void getFieldsReferences() {

        mAlertName = (EditText) findViewById(R.id.et_name);
        mAlertDesc = (EditText) findViewById(R.id.et_desc);
        mAlertDate = (EditText) findViewById(R.id.et_startdate);
        mAlertRptDays = (EditText) findViewById(R.id.et_repeat_days);
        mAlertRptHours = (EditText) findViewById(R.id.et_repeat_hours);
    }

    private void setEventClick() {

        ((ImageButton) findViewById(R.id.bi_startdate)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //TODO Check if it works
                        // 1st step: Date
                        final DatePickerDialog datedialog = new DatePickerDialog(EditorActivity.this, EditorActivity.this,
                                localtime.get(Calendar.YEAR), localtime.get(Calendar.MONTH), localtime.get(Calendar.DAY_OF_MONTH));
                        datedialog.show();
                    }
                }
        );
    }

    private void setDateText() {
        mAlertDate.setText(getDateTime(mAlert.getStartDate()));
    }

    private void getAlertFromIntent() {
        Intent intent = getIntent();

        if (intent.hasExtra(Alert.STRINGKEY_ALERT_ID)) {

            String id = intent.getStringExtra(Alert.STRINGKEY_ALERT_ID);
            Cursor cursor = getContentResolver().query(
                    Uri.withAppendedPath(Alert.URI, id), null, null, null, null);
            if (cursor != null) {

                if (cursor.moveToNext()) {

                    mAlert = new Alert(cursor);
                    localtime.setTimeInMillis(mAlert.getStartDate().longValue());
                    setFields();
                } else {

                    mAlert = new Alert();
                }
                cursor.close();
            } else {

                Log.e(TAG, String.format("Error retrieving data [id=%s]", id));
                setResult(RESULT_CANCELED);
                finish();
            }
        } else {

            mAlert = new Alert();
        }
    }

    private boolean checkToPerformSave() {

        String name = mAlertName.getText().toString();
        String desc = mAlertDesc.getText().toString();
        String rptdays = mAlertRptDays.getText().toString();
        String rpthours = mAlertRptHours.getText().toString();

        if (!(name.equals("") && name.equals(mAlert.getName())) &&
                !(desc.equals("") && desc.equals(mAlert.getDesc())) &&
                !(rptdays.equals("") && rptdays.equals(mAlert.getRptDays().toString())) ||
                !(rpthours.equals("") && rpthours.equals(mAlert.getRptHours().toString()))) {
            mModified = true;
            return true;
        }
        return false;
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if (mModified) {
//
//            setResult(RESULT_OK);
//            finish();
//        } else {
//
//            setResult(RESULT_CANCELED);
//            finish();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getFields() {

        // TODO Check values
        mAlert.setName(mAlertName.getText().toString());
        mAlert.setDesc(mAlertDesc.getText().toString());
        // La data e' gia' acquisita
        mAlert.setRptDays(Long.parseLong(mAlertRptDays.getText().toString()));
        mAlert.setRptHours(Long.parseLong(mAlertRptHours.getText().toString()));
    }

    private void setFields() {
        if (mAlert.get_id() < 0) return;

        mAlertName.setText(mAlert.getName());
        mAlertDesc.setText(mAlert.getDesc());
        setDateText();
        mAlertRptDays.setText(String.valueOf(mAlert.getRptDays()));
        mAlertRptHours.setText(String.valueOf(mAlert.getRptHours()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        // 1st Step: done.
        localtime.set(Calendar.YEAR, year);
        localtime.set(Calendar.MONTH, month);
        localtime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        // 2nd step: Time
        final TimePickerDialog timedialog = new TimePickerDialog(EditorActivity.this, EditorActivity.this,
                localtime.get(Calendar.HOUR_OF_DAY), localtime.get(Calendar.MINUTE), true);
        timedialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        // 2nd step: done.
        localtime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        localtime.set(Calendar.MINUTE, minute);

        // Attrib
        if (mAlert != null) {
            mAlert.setStartDate(localtime.getTimeInMillis());
        }

        // Update the textview
        setDateText();
    }
}
