package org.varonesoft.luke.ricodarlo;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.varonesoft.luke.ricodarlo.components.RicordaloIntentService;
import org.varonesoft.luke.ricodarlo.database.models.Alert;

import java.util.Calendar;

public class TaskerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String mTitle = "Title";
        String mText = "Text";
        Long mID = -1L;
        final Alert mAlert;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mID = extras.getLong(Constants.Service.EXTRA_ALERTID);
        }

        Cursor cursor = getContentResolver().query(Uri.withAppendedPath(Alert.URI, String.valueOf(mID)), null, null, null,null);
        if (cursor != null && cursor.moveToNext()) {
            mAlert = new Alert(cursor);
        } else {
            mAlert = new Alert();
        }

        ((TextView )findViewById(R.id.tv_title)).setText(mAlert.getName());
        ((TextView )findViewById(R.id.tv_text)).setText(mAlert.getDesc());

        ((Button )findViewById(R.id.btn_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                // Setta counter a 0
                mAlert.setCounter(0);
                mAlert.setStatus( Alert.STATUS_ACTIVATED);
                mAlert.setLastFired(calendar.getTimeInMillis());
                mAlert.save( TaskerActivity.this);

                //Imposta il prossimo allarme
                RicordaloIntentService.startActionSetAlarm( TaskerActivity.this, mAlert);
                finish();

            }
        });

        ((Button )findViewById(R.id.btn_later)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Aumenta il counter
                mAlert.setCounter( mAlert.getCounter() +1);
                mAlert.save( TaskerActivity.this);

                // Imoosta lo snooze
//                RicordaloIntentService.startActionSetSnooze( TaskerActivity.this, mAlert);
                finish();
            }
        });
    }
}
